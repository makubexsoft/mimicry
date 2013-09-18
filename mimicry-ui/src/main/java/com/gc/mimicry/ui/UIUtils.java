package com.gc.mimicry.ui;

import java.awt.Rectangle;
import java.awt.Window;

public class UIUtils
{
    public static void centerOnScreen(Window frame)
    {
        Rectangle dim = frame.getGraphicsConfiguration().getBounds();

        Window owner = frame.getOwner();
        if (owner != null)
        {
            dim = owner.getGraphicsConfiguration().getBounds();
        }

        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        frame.setLocation(x, y);
    }
}
