package org.mimicry.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.mimicry.AlwaysFirstNodeStrategy;
import org.mimicry.Simulation;
import org.mimicry.SimulationParameters;
import org.mimicry.bundle.LocalApplicationRepository;
import org.mimicry.engine.LocalEngine;
import org.mimicry.engine.SimpleEventBroker;
import org.mimicry.timing.TimelineType;
import org.mimicry.ui.groovy.ConsoleFrame;

import com.jidesoft.docking.DefaultDockableHolder;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.DockingManager;

public class MainFrame extends DefaultDockableHolder
{
    private static final long serialVersionUID = -1396222277201712462L;

    public MainFrame()
    {
        setTitle("The Mimicry Framework");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        configureDockingManager();
        createSimulationAndFrames();

        setJMenuBar(createMenuBar());

        setSize(800, 600);
        UIUtils.centerOnScreen(this);
        setVisible(true);

        // askForNewSimulation();
    }

    private void askForNewSimulation()
    {
        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.add(new NewSimulationView());
        dialog.pack();
        dialog.setModal(true);
        dialog.setResizable(false);
        UIUtils.centerOnScreen(dialog);
        dialog.setVisible(true);
    }

    private void configureDockingManager()
    {
        getDockingManager().setInitSplitPriority(DockingManager.SPLIT_EAST_WEST_SOUTH_NORTH);
        getDockingManager().setCrossDraggingAllowed(true);
        getDockingManager().setCrossDroppingAllowed(true);
        getDockingManager().setEasyTabDock(true);
        getDockingManager().getWorkspace().setAcceptDockableFrame(true);
        getDockingManager().getWorkspace().setLayout(new BorderLayout());
    }

    private void createSimulationAndFrames()
    {
        try
        {
            // Global configuration
            String defaultPath = LocalApplicationRepository.getDefaultPath().getAbsolutePath();
            File repoPath = new File(Settings.getValue(Settings.REPOSITORY_PATH, defaultPath));
            LocalApplicationRepository appRepo = new LocalApplicationRepository(repoPath);
            File workspace = new File(Settings.getValue(Settings.WORKSPACE_PATH));

            // Infrastructure
            SimpleEventBroker broker = new SimpleEventBroker();
            LocalEngine engine = new LocalEngine(broker, appRepo, workspace);

            // Simulation specific configuration
            SimulationParameters simulationParams = new SimulationParameters();
            simulationParams.setTimelineType(TimelineType.SYSTEM);

            // Setup
            Simulation.Builder builder = new Simulation.Builder();
            builder.withNodeDistributionStrategy(new AlwaysFirstNodeStrategy());
            builder.withEventEngine(broker);
            builder.withSimulationParameters(simulationParams);
            UUID sessionId = UUID.randomUUID();
            builder.addSession(engine.createSession(sessionId, simulationParams));
            Simulation simu = builder.build();

            createFrames(simu, appRepo);
        }
        catch (IOException e)
        {
            String message = "Failed to create a simulation.\nReason: " + e.getMessage();
            JOptionPane.showMessageDialog(this, message, "Simulation failed.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createFrames(Simulation simulation, LocalApplicationRepository repo)
    {
        DockableFrame eventLogFrame = new EventLogFrame(simulation);
        eventLogFrame.setKey("mimicry.eventLog");
        eventLogFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        eventLogFrame.getContext().setInitSide(DockContext.DOCK_SIDE_SOUTH);

        DockableFrame consoleFrame = new ConsoleFrame(simulation);
        consoleFrame.setKey("mimicry.consoleFrame");
        consoleFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        consoleFrame.getContext().setInitSide(DockContext.DOCK_SIDE_CENTER);

        NetworkBrowserFrame networkBrowser = new NetworkBrowserFrame();
        networkBrowser.setKey("mimicry.networkBrowser");
        networkBrowser.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        networkBrowser.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);

        ApplicationRepositoryFrame appRepo = new ApplicationRepositoryFrame(repo);
        appRepo.setKey("mimicry.applicationRepository");
        appRepo.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        appRepo.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);

        getDockingManager().beginLoadLayoutData();
        getDockingManager().addFrame(consoleFrame);
        getDockingManager().addFrame(eventLogFrame);
        getDockingManager().addFrame(networkBrowser);
        getDockingManager().addFrame(appRepo);
        getDockingManager().loadLayoutData();
    }

    private JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem itmExit = new JMenuItem("Exit");

        JMenu menuView = new JMenu("View");
        JCheckBoxMenuItem itmEventLog = new JCheckBoxMenuItem("Event Log");
        JCheckBoxMenuItem itmSimulationConsole = new JCheckBoxMenuItem("Simulation Console");
        JCheckBoxMenuItem itmNetworkBrowser = new JCheckBoxMenuItem("Network Browser");
        JCheckBoxMenuItem itmAppRepo = new JCheckBoxMenuItem("Application Repository");

        JMenu menuHelp = new JMenu("Help");
        JMenuItem itmManual = new JMenuItem("Show Manual");
        JMenuItem itmAbout = new JMenuItem("About Mimicry ...");

        menuBar.add(menuFile);
        menuFile.add(itmExit);

        menuBar.add(menuView);
        menuView.add(itmEventLog);
        menuView.add(itmSimulationConsole);
        menuView.add(itmNetworkBrowser);
        menuView.add(itmAppRepo);

        menuBar.add(menuHelp);
        menuHelp.add(itmManual);
        menuHelp.add(itmAbout);

        itmAbout.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                AboutDialog dlg = new AboutDialog();
                UIUtils.centerOnScreen(dlg);
                dlg.setModal(true);
                dlg.setVisible(true);
            }
        });
        itmManual.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (Desktop.isDesktopSupported())
                {
                    try
                    {
                        File myFile = new File("Mimicry.pdf");
                        Desktop.getDesktop().open(myFile);
                    }
                    catch (IOException ex)
                    {
                        // no application registered for PDFs
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Can't open PDF file. Opening containing folder instead.");
                        try
                        {
                            Desktop.getDesktop().open(new File("."));
                        }
                        catch (IOException e1)
                        {
                            String message = "I'm afraid. I can't open the folder containing the PDF for you. "
                                    + "But it's located at:\n" + new File("./Mimicry.pdf").getAbsolutePath();
                            JOptionPane.showMessageDialog(MainFrame.this, message);
                        }
                    }
                }
                else
                {
                    String message = "I'm afraid. I can't open the PDF for you. But it's located at:\n"
                            + new File("./Mimicry.pdf").getAbsolutePath();
                    JOptionPane.showMessageDialog(MainFrame.this, message);
                }
            }
        });

        return menuBar;
    }
}
