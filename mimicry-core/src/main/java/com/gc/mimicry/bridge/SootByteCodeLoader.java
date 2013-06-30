package com.gc.mimicry.bridge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.PatchingChain;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.JasminClass;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.options.Options;
import soot.toolkits.graph.LoopNestTree;
import soot.util.JasminOutputStream;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

/**
 * A byte code loader based on the soot framework for loading and weaving byte code. This class is used to pre-process
 * the byte code of the simulated application and weaves the byte code of all detected loops in order to invoke the
 * static method {@link LoopInterceptor#intercept()}. This is necessary to exit infinite loops when the simulator wants
 * to shutdown the simulated application asynchronously.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public final class SootByteCodeLoader
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(SootByteCodeLoader.class);
    }
    private static final Object lock = new Object();
    private final SootClass interceptorClass;
    private final SootMethod interceptorMethod;

    private String classPath;

    public SootByteCodeLoader(String[] urls)
    {
        Preconditions.checkNotNull(urls);

        classPath = Joiner.on(File.pathSeparator).join(urls);

        classPath += File.pathSeparator + System.getProperty("java.home") + File.separator;
        classPath += "lib" + File.separator + "rt.jar";

        classPath += File.pathSeparator + System.getProperty("java.home") + File.separator;
        classPath += "lib" + File.separator + "jce.jar";

        configureSoot();

        interceptorClass = Scene.v().loadClassAndSupport("com.gc.mimicry.bridge.LoopInterceptor");
        interceptorClass.setApplicationClass();
        for (SootMethod sm : interceptorClass.getMethods())
        {
            sm.retrieveActiveBody();
        }
        interceptorMethod = interceptorClass.getMethod("void intercept()");
    }

    public byte[] loadBytes(String className)
    {
        // Soot library is based on singletons and therefore not thread-safe
        synchronized (lock)
        {
            SootClass sootClass = loadClass(className);
            if (sootClass == null)
            {
                return null;
            }

            try
            {
                transformClass(sootClass);
                return getByteCodeOf(sootClass);
            }
            catch (Exception e)
            {
                logger.debug("Failed to transform byte code for class: " + className, e);
                return null;
            }
        }
    }

    private void configureSoot()
    {
        Scene.v().setSootClassPath(classPath);
        Options.v().set_soot_classpath(classPath);
        Options.v().set_verbose(false);
        Options.v().set_keep_line_number(true);
        Options.v().set_src_prec(Options.src_prec_only_class);
        Options.v().set_output_format(Options.output_format_jasmin);
        Options.v().set_keep_offset(true);
        // Options.v().set_allow_phantom_refs( true );
        PhaseOptions.v().setPhaseOption("jb", "on");
        PhaseOptions.v().setPhaseOption("jb", "use-original-names:true");

        // //PhaseOptions.v().setPhaseOption( "jb.ls", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.a", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.ule", "off" );
        // // PhaseOptions.v().setPhaseOption( "jb.tr", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.ulp", "on" );
        // PhaseOptions.v().setPhaseOption( "jb.lns", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.cp", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.dae", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.cp-ule", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.lp", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.ne", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.uce", "off" );
        // PhaseOptions.v().setPhaseOption( "jb.tt", "off" );
        // PhaseOptions.v().setPhaseOption( "jtp", "off" );
        // PhaseOptions.v().setPhaseOption( "jop", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.cp", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.cpf", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.cbf", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.dae", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.uce1", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.ubf1", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.uce2", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.ubf2", "off" );
        // PhaseOptions.v().setPhaseOption( "jop.ule", "off" );
        // PhaseOptions.v().setPhaseOption( "jap", "off" );
        // //
        // PhaseOptions.v().setPhaseOption( "bb.lso", "off" );
        // PhaseOptions.v().setPhaseOption( "bb.pho", "off" );
        // PhaseOptions.v().setPhaseOption( "bb.ule", "off" );
        // PhaseOptions.v().setPhaseOption( "bb.lp", "off" );

        // Options.v().set_whole_program( false );
    }

    private byte[] getByteCodeOf(SootClass sootClass) throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStream streamOut = new JasminOutputStream(bout);
        PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));

        JasminClass jasminClass = new JasminClass(sootClass);
        jasminClass.print(writerOut);
        writerOut.flush();
        streamOut.close();
        bout.close();

        return bout.toByteArray();
    }

    private void transformClass(SootClass sootClass)
    {
        // SootResolver.v().resolveClass( sootClass.getName(),
        // SootClass.SIGNATURES );
        Scene.v().loadNecessaryClasses();
        for (SootMethod method : sootClass.getMethods())
        {
            if (method.isConcrete())
            {
                weaveMethod(method);
            }
        }
    }

    private void weaveMethod(SootMethod method)
    {
        Body body = method.retrieveActiveBody();
        PatchingChain<Unit> units = body.getUnits();

        LoopNestTree loopNestTree = new LoopNestTree(body);
        for (Loop loop : loopNestTree)
        {
            InvokeExpr expression = Jimple.v().newStaticInvokeExpr(interceptorMethod.makeRef());
            Stmt statement = Jimple.v().newInvokeStmt(expression);
            units.insertAfter(statement, loop.getHead()); // TODO: was
                                                          // insertBefore
        }
    }

    private SootClass loadClass(String className)
    {
        try
        {
            // Scene.v().forceResolve( className, SootClass.BODIES );
            SootClass sootClass = Scene.v().loadClassAndSupport(className);
            sootClass.setApplicationClass();
            return sootClass;
        }
        catch (RuntimeException e)
        {
            SootClass sootClass = Scene.v().loadClassAndSupport(className);
            Scene.v().removeClass(sootClass);
            if (!e.getMessage().startsWith("couldn't find class:"))
            {
                logger.debug("Failed to load soot class: " + className, e);
            }
            return null;
        }
    }
}
