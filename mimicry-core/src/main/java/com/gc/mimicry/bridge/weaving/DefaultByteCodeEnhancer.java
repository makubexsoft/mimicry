package com.gc.mimicry.bridge.weaving;

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

        ModifierClassWriter modifier = new ModifierClassWriter(Opcodes.ASM4, writer); // new CheckClassAdapter(writer));
        reader.accept(modifier, ClassReader.EXPAND_FRAMES);

        // StringWriter sw = new StringWriter();
        // PrintWriter pw = new PrintWriter(sw);
        // CheckClassAdapter.verify(new ClassReader(writer.toByteArray()), false, pw);
        // System.out.println(sw.toString());

        // ClassReader classReader = new ClassReader(writer.toByteArray());
        // PrintWriter printWriter = new PrintWriter(System.out);
        // TraceClassVisitor traceClassVisitor = new TraceClassVisitor(printWriter);
        // classReader.accept(traceClassVisitor, ClassReader.SKIP_DEBUG);

        return writer.toByteArray();
    }
}

class ModifierClassWriter extends ClassVisitor
{
    private final int api;
    private static final String CLASS_NAME = "com/gc/mimicry/bridge/threading/MonitorInterceptor";

    public ModifierClassWriter(int api, ClassVisitor cv)
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
                        // GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
                        // LDC "test"
                        // INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V

                        // super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        // super.visitLdcInsn("test");
                        // super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print",
                        // "(Ljava/lang/String;)V");

                        super.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS_NAME, "monitorEnter", "(Ljava/lang/Object;)V");

                        // super.visitInsn(Opcodes.DUP);
                        // super.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS_NAME, "beforeMonitorEnter",
                        // "(Ljava/lang/Object;)V");
                        // super.visitInsn(Opcodes.DUP);
                        // super.visitInsn(opcode);
                        // super.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS_NAME, "afterMonitorEnter",
                        // "(Ljava/lang/Object;)V");
                        break;
                    }
                    case Opcodes.MONITOREXIT:
                    {
                        // super.visitInsn(Opcodes.DUP);
                        // super.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS_NAME, "beforeMonitorExit",
                        // "(Ljava/lang/Object;)V");
                        // super.visitInsn(Opcodes.DUP);
                        // super.visitInsn(opcode);
                        // super.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS_NAME, "afterMonitorExit",
                        // "(Ljava/lang/Object;)V");
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
