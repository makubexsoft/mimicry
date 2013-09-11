package stubs;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.bytecode.Bytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

public class GenerateUnsafe
{
    public static void main(String[] argv) throws Exception
    {
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.makeClass("com.gc.mimicry.util.Unsafe");
        clazz.setModifiers(Modifier.PUBLIC);

        CtConstructor ctor = CtNewConstructor.make("private Unsafe(){}", clazz);
        clazz.addConstructor(ctor);

        CtMethod monitorExit = new CtMethod(pool.get("void"), "monitorExit",
                new CtClass[] { pool.get("java.lang.Object") }, clazz);
        monitorExit.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
        Bytecode code = new Bytecode(monitorExit.getMethodInfo().getConstPool());
        code.add(Bytecode.ALOAD_0);
        code.add(Bytecode.MONITOREXIT);
        code.add(Bytecode.RETURN);
        code.setMaxLocals(1);
        code.setMaxStack(1);
        monitorExit.getMethodInfo().setCodeAttribute(code.toCodeAttribute());
        clazz.addMethod(monitorExit);

        CtMethod monitorEnter = new CtMethod(pool.get("void"), "monitorEnter",
                new CtClass[] { pool.get("java.lang.Object") }, clazz);
        monitorEnter.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
        Bytecode code2 = new Bytecode(monitorEnter.getMethodInfo().getConstPool());
        code2.add(Bytecode.ALOAD_0);
        code2.add(Bytecode.DUP);
        code2.add(Bytecode.MONITORENTER);
        code2.add(Bytecode.MONITOREXIT);
        code2.add(Bytecode.RETURN);
        code2.setMaxLocals(1);
        code2.setMaxStack(2);
        monitorEnter.getMethodInfo().setCodeAttribute(code2.toCodeAttribute());
        clazz.addMethod(monitorEnter);

        byte[] byteCode = clazz.toBytecode();
        //
        // -----------[ print ] ------------
        //

        ClassReader classReader = new ClassReader(byteCode);
        PrintWriter printWriter = new PrintWriter(System.out);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(printWriter);
        classReader.accept(traceClassVisitor, ClassReader.SKIP_DEBUG);

        //
        // -----------[ write to HD ] ------------
        //
        FileOutputStream out = new FileOutputStream("Unsafe.class");
        out.write(byteCode);
        out.close();
    }
}
