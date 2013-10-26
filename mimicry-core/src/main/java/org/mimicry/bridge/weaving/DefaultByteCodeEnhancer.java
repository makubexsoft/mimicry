package org.mimicry.bridge.weaving;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default byte code enhancer run after AspectJ has woven the application classes. It removes finalizers and replaces
 * <code>monitorenter</code> and <code>monitorexit</code> byte code {@link InstructionSelect#}
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class DefaultByteCodeEnhancer implements ByteCodeEnhancer
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(DefaultByteCodeEnhancer.class);
    }

    @Override
    public byte[] enhance(String className, byte[] byteCode)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Enhancing: " + className);
        }
        byteCode = removeFinalizer(className, byteCode);
        byteCode = transformSyncMethodsToBlocks(className, byteCode);
        byteCode = enhanceWithMonitorHandling(className, byteCode);
        return byteCode;
    }

    private static byte[] removeFinalizer(String className, byte[] byteCode)
    {
        try
        {
            CtClass clazz = new ClassPool().makeClass(new ByteArrayInputStream(byteCode));
            if (clazz.isInterface())
            {
                return byteCode;
            }

            try
            {
                CtMethod method = clazz.getMethod("finalize", "()V");
                if (null != method && !method.isEmpty())
                {
                    clazz.removeMethod(method);
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Removed finalizer of " + className);
                    }
                }
            }
            catch (NotFoundException ignore)
            {
            }

            byteCode = clazz.toBytecode();
        }
        catch (IOException e)
        {
            logger.error("Failed to remove finalizer from " + className, e);
        }
        catch (CannotCompileException e)
        {
            logger.error("Failed to remove finalizer from " + className, e);
        }
        return byteCode;
    }

    private static byte[] enhanceWithMonitorHandling(String className, byte[] byteCode)
    {
        ClassReader reader = new ClassReader(byteCode);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);

        MonitorInterceptingVisitor modifier = new MonitorInterceptingVisitor(Opcodes.ASM4, writer);
        reader.accept(modifier, ClassReader.EXPAND_FRAMES);

        return writer.toByteArray();
    }

    private static byte[] transformSyncMethodsToBlocks(String className, byte[] byteCode)
    {
        ClassReader reader = new ClassReader(byteCode);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);

        SyncMethodInterceptingVisitor modifier = new SyncMethodInterceptingVisitor(Opcodes.ASM4, writer);
        reader.accept(modifier, ClassReader.EXPAND_FRAMES);

        return writer.toByteArray();
    }
}

class SyncMethodInterceptingVisitor extends ClassVisitor
{
    private final int api;
    private String className;

    public SyncMethodInterceptingVisitor(int api, ClassVisitor cv)
    {
        super(api, cv);
        this.api = api;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access)
    {
        super.visitInnerClass(name, outerName, innerName, access);
        className = name;
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc)
    {
        super.visitOuterClass(owner, name, desc);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, String desc, String signature,
            String[] exceptions)
    {
        if ((access & Opcodes.ACC_SYNCHRONIZED) == 0)
        {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        MethodVisitor mv = super.visitMethod(access & ~Opcodes.ACC_SYNCHRONIZED, name, desc, signature, exceptions);
        return new MethodVisitor(api, mv)
        {
            @Override
            public void visitCode()
            {
                super.visitCode();
                if ((access & Opcodes.ACC_STATIC) == 0)
                {
                    // load "this"
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                }
                else
                {
                    // load ".class"
                    super.visitLdcInsn("L" + className + ";.class");
                }
                super.visitInsn(Opcodes.MONITORENTER);
            }

            @Override
            public void visitInsn(final int opcode)
            {
                switch (opcode)
                {
                    case Opcodes.ATHROW:
                    case Opcodes.RETURN:
                    {
                        if ((access & Opcodes.ACC_STATIC) == 0)
                        {
                            // load "this"
                            super.visitVarInsn(Opcodes.ALOAD, 0);
                        }
                        else
                        {
                            // load ".class"
                            super.visitLdcInsn("L" + className + ";.class");
                        }
                        super.visitInsn(Opcodes.MONITOREXIT);
                    }
                }
                super.visitInsn(opcode);
            }
        };
    }
}

class MonitorInterceptingVisitor extends ClassVisitor
{
    private final int api;
    private static final String CLASS_NAME = "org/mimicry/bridge/threading/MonitorInterceptor";

    public MonitorInterceptingVisitor(int api, ClassVisitor cv)
    {
        super(api, cv);
        this.api = api;
    }

    @Override
    public MethodVisitor visitMethod(int access, final String name, String desc, String signature, String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodVisitor(api, mv)
        {
            @Override
            public void visitInsn(final int opcode)
            {
                switch (opcode)
                {
                    case Opcodes.MONITORENTER:
                    {
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS_NAME, "monitorEnter", "(Ljava/lang/Object;)V");
                        break;
                    }
                    case Opcodes.MONITOREXIT:
                    {
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS_NAME, "monitorExit", "(Ljava/lang/Object;)V");
                        break;
                    }
                    default:
                        super.visitInsn(opcode);
                }
            }
        };
    }
}
