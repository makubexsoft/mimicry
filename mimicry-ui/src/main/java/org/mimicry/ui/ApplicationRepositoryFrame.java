package org.mimicry.ui;

import java.awt.BorderLayout;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.mimicry.engine.deployment.ApplicationRepository;

import com.jidesoft.docking.DockableFrame;

public class ApplicationRepositoryFrame extends DockableFrame
{
    private ApplicationRepository repo;

    public ApplicationRepositoryFrame(final ApplicationRepository repo)
    {
        this.repo = repo;

        setTitle("Application Repository");

        JScrollPane scrollPane = new JScrollPane();
        getRootPane().getContentPane().add(scrollPane, BorderLayout.CENTER);

        JList<String> list = new JList<String>();
        list.setModel(new AbstractListModel<String>()
        {
            private static final long serialVersionUID = 476640818989868582L;
            String[] values = repo.listBundles().toArray(new String[0]);

            @Override
            public int getSize()
            {
                return values.length;
            }

            @Override
            public String getElementAt(int index)
            {
                return values[index];
            }
        });
        scrollPane.setViewportView(list);
    }
}
