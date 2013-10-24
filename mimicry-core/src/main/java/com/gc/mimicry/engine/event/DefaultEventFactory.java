package com.gc.mimicry.engine.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.util.StringUtils;
import com.gc.mimicry.util.VectorClock;
import com.google.common.base.Preconditions;

public class DefaultEventFactory implements EventFactory
{
    static
    {
        logger = LoggerFactory.getLogger(DefaultEventFactory.class);
    }
    private static final Logger logger;
    private static final String IMPL_SUFFIX = "_Impl";
    private final Identity id;
    private static final ClassPool pool = ClassPool.getDefault();
    private static final Map<Class<?>, Class<?>> cachedImpls = new HashMap<Class<?>, Class<?>>();

    private DefaultEventFactory(Identity id)
    {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    public static EventFactory create(Identity id)
    {
        return new DefaultEventFactory(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ApplicationEvent> T createEvent(Class<T> eventClass, UUID applicationId)
    {
        Class<?> impl = getImplementation(eventClass);
        try
        {
            Constructor<?> ctor = impl.getConstructor(VectorClock.class, UUID.class);
            return (T) ctor.newInstance(new Object[] { id.getClock(), applicationId });
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create event.", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ApplicationEvent> T createEvent(Class<T> eventClass, UUID applicationId, UUID controlFlow)
    {
        Class<?> impl = getImplementation(eventClass);
        try
        {
            Constructor<?> ctor = impl.getConstructor(VectorClock.class, UUID.class, UUID.class);
            return (T) ctor.newInstance(new Object[] { id.getClock(), applicationId, controlFlow });
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create event.", e);
        }
    }

    private <T extends ApplicationEvent> Class<?> getImplementation(Class<T> eventClass)
    {
        synchronized (cachedImpls)
        {
            Class<?> impl = cachedImpls.get(eventClass);
            if (impl == null)
            {
                impl = createImplementation(eventClass);
                cachedImpls.put(eventClass, impl);
            }
            return impl;
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ApplicationEvent> Class<T> createImplementation(Class<T> eventClass)
    {
        if (!eventClass.isInterface())
        {
            throw new IllegalArgumentException("Event class must be an interface.");
        }

        CtClass implClass = pool.makeClass(eventClass.getName() + IMPL_SUFFIX);
        try
        {
            implClass.addInterface(pool.get(eventClass.getName()));
            implClass.setSuperclass(pool.get(ApplicationEventBase.class.getName()));

            createProperties(eventClass, implClass);

            createCtor2Args(implClass);
            createCtor3Args(implClass);

            createToString(implClass, eventClass);

            Class<T> clazz = implClass.toClass();
            if (logger.isDebugEnabled())
            {
                logger.debug("Generated event implementation: " + clazz);
            }
            return clazz;
        }
        catch (NotFoundException e)
        {
            logger.error("Failed to generate event implementation.", e);
            throw new RuntimeException(e);
        }
        catch (CannotCompileException e)
        {
            logger.error("Failed to generate event implementation.", e);
            throw new RuntimeException(e);
        }
    }

    private void createToString(CtClass implClass, Class<?> eventClass) throws CannotCompileException
    {
        StringBuilder source = new StringBuilder();
        source.append("public String toString() {");
        source.append("StringBuilder buffer = new StringBuilder(\"");
        source.append(eventClass.getSimpleName());
        source.append("[\");");
        source.append("buffer.append(super.toString());");
        CtField[] fields = implClass.getFields();
        for (CtField field : fields)
        {
            source.append("buffer.append(\", ");
            source.append(field.getName());
            source.append("='\" + ");
            source.append(field.getName());
            source.append(" + \"'\");");
        }
        source.append("buffer.append(\"]\");");
        source.append("return buffer.toString(); }");
        CtMethod method = CtNewMethod.make(source.toString(), implClass);
        implClass.addMethod(method);
    }

    private void createCtor3Args(CtClass implClass) throws NotFoundException, CannotCompileException
    {
        CtClass uuidClass = pool.get(UUID.class.getName());
        CtConstructor ctor = CtNewConstructor.make(new CtClass[] { pool.get(VectorClock.class.getName()), uuidClass,
                uuidClass }, new CtClass[] {}, implClass);
        implClass.addConstructor(ctor);
    }

    private void createCtor2Args(CtClass implClass) throws NotFoundException, CannotCompileException
    {
        CtClass uuidClass = pool.get(UUID.class.getName());
        CtConstructor ctor = CtNewConstructor.make(new CtClass[] { pool.get(VectorClock.class.getName()), uuidClass },
                new CtClass[] {}, implClass);
        implClass.addConstructor(ctor);
    }

    private <T extends ApplicationEvent> void createProperties(Class<T> eventClass, CtClass implClass)
            throws NotFoundException, CannotCompileException
    {
        Method[] methods = eventClass.getDeclaredMethods();
        for (Method method : methods)
        {
            createProperty(implClass, method);
        }
    }

    private void createProperty(CtClass implClass, Method method) throws NotFoundException, CannotCompileException
    {
        if (methodIsGetter(method))
        {
            String methodName = method.getName().substring(3);
            createGetter(implClass, method, methodName);
        }
        else if (methodIsBooleanGetter(method))
        {
            String methodName = method.getName().substring(2);
            createGetter(implClass, method, methodName);
        }
        else if (methodIsSetter(method))
        {
            String methodName = method.getName().substring(3);
            createSetter(implClass, method, methodName);
        }
        else
        {
            String msg = "Invalid method signature. Neither valid getter nor setter. " + method;
            logger.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private void createSetter(CtClass implClass, Method method, String methodName) throws NotFoundException,
            CannotCompileException
    {
        String propertyName = StringUtils.toFirstLower(methodName);
        CtField field = getOrCreateField(implClass, method, propertyName);
        CtMethod setter = CtNewMethod.setter(method.getName(), field);
        implClass.addMethod(setter);
    }

    private void createGetter(CtClass implClass, Method method, String methodName) throws NotFoundException,
            CannotCompileException
    {
        String propertyName = StringUtils.toFirstLower(methodName);
        CtField field = getOrCreateField(implClass, method, propertyName);
        CtMethod getter = CtNewMethod.getter(method.getName(), field);
        implClass.addMethod(getter);
    }

    private CtField getOrCreateField(CtClass implClass, Method method, String propertyName) throws NotFoundException,
            CannotCompileException
    {
        try
        {
            return implClass.getField(propertyName);
        }
        catch (NotFoundException e)
        {
            String returnType = method.getReturnType().getName();
            if (returnType == "void")
            {
                // we are within a setter
                returnType = method.getParameterTypes()[0].getName();
            }
            CtClass fieldType = pool.get(returnType);
            CtField field = new CtField(fieldType, propertyName, implClass);
            implClass.addField(field);
            return field;
        }
    }

    private boolean methodIsSetter(Method method)
    {
        if (!method.getName().startsWith("set"))
        {
            return false;
        }
        if (!method.getReturnType().equals(void.class))
        {
            return false;
        }
        return method.getParameterTypes().length == 1;
    }

    private boolean methodIsBooleanGetter(Method method)
    {
        if (!method.getName().startsWith("is"))
        {
            return false;
        }
        if (!method.getReturnType().equals(boolean.class))
        {
            return false;
        }
        return method.getParameterTypes().length == 0;
    }

    private boolean methodIsGetter(Method method)
    {
        if (!method.getName().startsWith("get"))
        {
            return false;
        }
        if (method.getReturnType().equals(void.class))
        {
            return false;
        }
        return method.getParameterTypes().length == 0;
    }
}
