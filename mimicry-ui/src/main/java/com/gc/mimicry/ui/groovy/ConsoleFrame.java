package com.gc.mimicry.ui.groovy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.swing.SwingBuilder;
import groovy.ui.Console;

import java.awt.BorderLayout;
import java.io.File;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.text.DefaultCaret;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.engine.event.DefaultEventFactory;
import com.gc.mimicry.engine.event.EventFactory;
import com.gc.mimicry.engine.event.Identity;
import com.gc.mimicry.ext.timing.ClockController;
import com.gc.mimicry.ui.model.FileSystemTreeCellRenderer;
import com.gc.mimicry.ui.model.FileSystemTreeModel;
import com.gc.mimicry.util.FileNameExtensionFilter;
import com.jidesoft.docking.DockableFrame;

public class ConsoleFrame extends DockableFrame implements RootPaneContainer
{
    private static final long serialVersionUID = 3114381363570076039L;

    private JTree treeScriptDir;

    public ConsoleFrame(final Simulation simu)
    {
        setTitle("Simulation Console");

        Console.setCaptureStdErr(false);
        Console.setCaptureStdOut(false);

        final Console console = new Console()
        {
            @Override
            public void newScript(ClassLoader parent, Binding binding)
            {
                setShell(createShell(simu));
            };

            @Override
            public void exit(EventObject evt)
            {
                /* suppress */
            }

            @Override
            public void exit()
            {
                /* suppress */
            }

            @Override
            public void fileNewWindow()
            {
                /*
                 * we don't support multiple console instances due to threading issues
                 */
            }

            @Override
            public void fileNewWindow(EventObject evt)
            {
                /*
                 * we don't support multiple console instances due to threading issues
                 */
            }
        };

        Map<String, Object> config = new HashMap<String, Object>();
        config.put("rootContainerDelegate", new Closure<DockableFrame>(this)
        {
            private static final long serialVersionUID = -1252898300727076072L;

            @Override
            public DockableFrame call()
            {
                return ConsoleFrame.this;
            }
        });
        config.put("menuBarDelegate", new Closure<Void>(this)
        {
            private static final long serialVersionUID = 5136110700593205324L;

            @Override
            public Void call(Object... args)
            {
                SwingBuilder builder = console.getSwing();
                setJMenuBar((JMenuBar) (builder.build((Class<?>) args[0])));
                return null;
            }
        });

        console.setShowScriptInOutput(false);
        console.setShell(createShell(simu));
        console.run(config);

        // Add additional splitter for script directory view
        JSplitPane hsplit = console.getSplitPane();
        JSplitPane vsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        vsplit.setLeftComponent(createScriptDirView());
        vsplit.setRightComponent(hsplit);
        add(vsplit, BorderLayout.CENTER);

        // enable auto-scroll
        DefaultCaret caret = (DefaultCaret) console.getOutputArea().getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    private JComponent createScriptDirView()
    {
        ImageIcon scriptIcon = new ImageIcon(getClass().getResource("/icons/script.png"));
        ImageIcon dirIcon = new ImageIcon(getClass().getResource("/icons/folder-horizontal.png"));
        ImageIcon dirExpIcon = new ImageIcon(getClass().getResource("/icons/folder-horizontal-open.png"));

        treeScriptDir = new JTree(new FileSystemTreeModel(new File("."), new FileNameExtensionFilter(".groovy")));
        treeScriptDir.setRootVisible(false);
        treeScriptDir.setCellRenderer(new FileSystemTreeCellRenderer(dirIcon, dirExpIcon, scriptIcon));
        JScrollPane scroll = new JScrollPane(treeScriptDir);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("Script Directory"), BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private GroovyShell createShell(final Simulation simu)
    {
        EventFactory eventFactory = DefaultEventFactory.create(Identity.create("Groovy-Shell"));
        ClockController clockController = new ClockController(simu.getEventEngine(), eventFactory);

        BuiltInBinding binding = new BuiltInBinding();
        binding.defineBuiltInVariable("simulation", simu);
        binding.defineBuiltInVariable("timeline", clockController);

        ImportCustomizer importCust = new ImportCustomizer();
        importCust.addStarImports("com.gc.mimicry.core.deployment");
        importCust.addStarImports("com.gc.mimicry.core.runtime");
        importCust.addStarImports("com.gc.mimicry.core.timing");

        CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(importCust);

        return new GroovyShell(binding, config);
    }
}