package com.gc.mimicry.bridge.weaving;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.engine.ClassPathConfiguration;

/**
 * Specialized class loader for simulated applications. It applies the given aspects to the loaded classes. Internally
 * the class loader manages the classes at three stages which contain different classes.
 * <ul>
 * <li><b>Stage-0</b><br/>
 * Contains the JRE and Mimicry Core classes. There is only a single stage-0. Classes loaded in this stage are not
 * considered in the weaving process.</li>
 * <li><b>Stage-1</b><br/>
 * Contains the Mimicry Bridge and Aspects. This stage gets instantiated per simulated application. Classes of this
 * stage are woven using the given aspects.</li>
 * <li><b>Stage-2</b><br/>
 * Contains the classes of the simulated application. This stage gets instantiated for each stage-1. Classes of this
 * stage are woven using the given aspects.</li>
 * </ul>
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ApplicationClassLoader extends PostProcessingEnabledWeavingURLClassLoader
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(ApplicationClassLoader.class);
    }
    private final ByteCodeEnhancer enhancer;

    ApplicationClassLoader(Collection<URL> stage2ClassPath, Collection<URL> stage1ClassPath,
            Collection<URL> aspectPath, ClassLoader stage0ClassLoader)
    {
        super(stage2ClassPath.toArray(new URL[0]), aspectPath.toArray(new URL[0]), new Stage1ClassLoader(
                stage1ClassPath, stage0ClassLoader));

        ((Stage1ClassLoader) getParent()).setChild(this);

        enhancer = new DefaultByteCodeEnhancer();
    }

    public static ApplicationClassLoader create(ClassPathConfiguration config) throws MalformedURLException
    {
        // Aspects must also be available at [Stage-1]
        Set<URL> stage1ClassPath = new HashSet<URL>();
        stage1ClassPath.addAll(config.getAspectClassPath());
        stage1ClassPath.addAll(config.getStage1ClassPath());

        return new ApplicationClassLoader(config.getStage2ClassPath(), stage1ClassPath, config.getAspectClassPath(),
                config.getStage0ClassLoader());
    }

    @Override
    protected byte[] postProcess(String name, byte[] byteCode)
    {
        if (byteCode != null && !name.startsWith("org.aspectj") && !name.startsWith("javassist"))
        {
            int formerSize = byteCode.length;
            byteCode = enhancer.enhance(name, byteCode);
            if (logger.isDebugEnabled())
            {
                logger.debug(name + " has been enhanced by " + (byteCode.length - formerSize) + " bytes up to "
                        + byteCode.length + " bytes.");
            }
        }
        return byteCode;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        // At [Stage-2] it's not allowed to load any code of Mimicry itself
        if (name.startsWith("com.gc.mimicry."))
        {
            throw new ClassNotFoundException(name);
        }
        Class<?> c = super.findClass(name);
        if (logger.isDebugEnabled())
        {
            logger.debug("[STAGE-2] " + name);
        }
        return c;
    }

    /**
     * Important override otherwise would the [STAGE-1] directly ask [STAGE-0] instead of first asking [STAGE-2].
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        return getParent().loadClass(name);
    }

    private static class Stage1ClassLoader extends URLClassLoader
    {
        private ApplicationClassLoader child;

        public Stage1ClassLoader(Collection<URL> stage1ClassPath, ClassLoader stage0ClassLoader)
        {
            super(stage1ClassPath.toArray(new URL[0]), stage0ClassLoader);
        }

        public void setChild(ApplicationClassLoader child)
        {
            this.child = child;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException
        {
            //
            // STAGE-2
            //
            Class<?> clazz0 = child.findLoadedClass(name);
            if (clazz0 != null)
            {
                return clazz0;
            }
            try
            {
                return child.findClass(name);
            }
            catch (ClassNotFoundException e)
            {
                // ignore - [STAGE-1] and [STAGE-0] (parent) still there
            }

            //
            // STAGE-1
            //
            Class<?> clazz = findLoadedClass(name);
            if (clazz != null)
            {
                return clazz;
            }
            try
            {
                Class<?> c = findClass(name);
                if (logger.isDebugEnabled())
                {
                    logger.debug("[STAGE-1] " + name);
                }
                return c;
            }
            catch (ClassNotFoundException e)
            {
                // ignore - [STAGE-0] (parent - System Class Loader) will pick it up
            }

            //
            // STAGE-0
            //
            Class<?> c = getParent().loadClass(name);
            if (c != null && logger.isDebugEnabled())
            {
                logger.debug("[STAGE-0] " + name);
            }
            return c;
        }
    }
}
