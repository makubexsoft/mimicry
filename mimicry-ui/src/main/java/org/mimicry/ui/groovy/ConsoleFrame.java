package org.mimicry.ui.groovy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.ui.Console;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.text.DefaultCaret;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.mimicry.engine.Simulation;
import org.mimicry.engine.event.DefaultEventFactory;
import org.mimicry.engine.event.EventFactory;
import org.mimicry.engine.event.Identity;
import org.mimicry.ext.timing.ClockController;
import org.mimicry.ui.model.FileSystemTreeCellRenderer;
import org.mimicry.ui.model.FileSystemTreeModel;
import org.mimicry.util.FileNameExtensionFilter;

import com.jidesoft.docking.DockableFrame;

public class ConsoleFrame extends DockableFrame implements RootPaneContainer
{
    private static final long serialVersionUID = 3114381363570076039L;

    private Console console;
    private JTree treeScriptDir;

    public ConsoleFrame(final Simulation simu)
    {
        setTitle("Simulation Console");
        setLayout(new BorderLayout());

        Console.setCaptureStdErr(false);
        Console.setCaptureStdOut(false);

        console = new MyConsole(simu);

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
                // SwingBuilder builder = console.getSwing();
                // setJMenuBar((JMenuBar) (builder.build((Class<?>) args[0])));
                return null;
            }
        });

        console.setShowScriptInOutput(false);
        console.setShowToolbar(false);
        console.run(config);
        console.getStatusLabel().setText("");

        JPanel toolbar = createToolbar();

        // Add additional splitter for script directory view
        JSplitPane hsplit = console.getSplitPane();
        JPanel p = new JPanel(new BorderLayout());
        p.add(BorderLayout.NORTH, toolbar);
        p.add(BorderLayout.CENTER, hsplit);
        JSplitPane vsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        vsplit.setLeftComponent(createScriptDirView());
        vsplit.setRightComponent(p);
        add(vsplit, BorderLayout.CENTER);

        enableAutoScroll();
    }

    private JPanel createToolbar()
    {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnRun = new JButton("Execute");
        btnRun.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                console.runScript();
            }
        });
        toolbar.add(btnRun);

        JButton btnStop = new JButton();
        btnStop.setHideActionText(true);
        btnStop.setAction(console.getInterruptAction());
        toolbar.add(btnStop);

        JButton btnVariables = new JButton("Variables");
        btnVariables.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                console.inspectVariables();
            }
        });
        toolbar.add(btnVariables);

        return toolbar;
    }

    private void enableAutoScroll()
    {
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
}

class MyConsole extends Console
{
    private Simulation simulation;

    public MyConsole(Simulation simulation)
    {
        this.simulation = simulation;
        setShell(createShell(simulation));
    }

    @Override
    public void newScript(ClassLoader parent, Binding binding)
    {
        if (simulation != null)
        {
            setShell(createShell(simulation));
        }
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

    private GroovyShell createShell(final Simulation simulation)
    {
        EventFactory eventFactory = DefaultEventFactory.create(Identity.create("Groovy-Shell"));
        ClockController clockController = new ClockController(simulation.getEventEngine(), eventFactory);

        BuiltInBinding binding = new BuiltInBinding();
        binding.defineBuiltInVariable("simulation", simulation);
        binding.defineBuiltInVariable("timeline", clockController);

        ImportCustomizer importCust = new ImportCustomizer();
        importCust.addStarImports("org.mimicry.engine");
        importCust.addStarImports("org.mimicry.core.deployment");
        importCust.addStarImports("org.mimicry.core.runtime");
        importCust.addStarImports("org.mimicry.core.timing");

        CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(importCust);

        return new GroovyShell(binding, config);
    }
}