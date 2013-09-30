package com.gc.mimicry.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(Main.class);
    }

    public static void main(String[] args)
    {
        Settings.load();
        com.jidesoft.utils.Lm.verifyLicense("Marc-Christian Schulze", "Mimicry", "aS5VMaqDIacp52..:R4waWiGosri1oa1");
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                {
                    if ("Nimbus".equals(info.getName()))
                    {
                        try
                        {
                            UIManager.setLookAndFeel(info.getClassName());
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
                // LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                new MainFrame();
            }
        });
    }
}
