package org.mimicry.ui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.mimicry.engine.EventEngine;
import org.mimicry.engine.EventListener;
import org.mimicry.engine.event.ApplicationEvent;

import com.jidesoft.swing.Searchable;
import com.jidesoft.swing.SearchableBar;
import com.jidesoft.swing.SearchableUtils;

public class EventLogView extends JPanel
{
	private JTextArea txtEventLog;
	
	public EventLogView(EventEngine eventBroker)
	{
		setLayout( new BorderLayout( 0, 0 ) );

		txtEventLog = new JTextArea();
		txtEventLog.setEditable( false );
		
		add( wrapInSearchableScrollPane(txtEventLog), BorderLayout.CENTER );
		
		eventBroker.addEventListener( new EventListener()
		{
			
			@Override
			public void handleEvent( final ApplicationEvent evt )
			{
				SwingUtilities.invokeLater( new Runnable()
				{
					
					@Override
					public void run()
					{
						txtEventLog.append( evt.toString() + "\n" );
					}
				} );
			}
		} );
	}
	
	private JComponent wrapInSearchableScrollPane(JTextArea comp)
	{
		JScrollPane scrollPane = new JScrollPane(comp);
		scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		
		final JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);
        
        final JPanel barPanel = new JPanel(new BorderLayout());
        
        panel.add(barPanel, BorderLayout.AFTER_LAST_LINE);
        Searchable searchable = SearchableUtils.installSearchable( comp );
        searchable.setRepeats(true);
        SearchableBar _textAreaSearchableBar = SearchableBar.install(searchable, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), new SearchableBar.Installer() {
            public void openSearchBar(SearchableBar searchableBar) {
                barPanel.add(searchableBar, BorderLayout.AFTER_LAST_LINE);
                barPanel.invalidate();
                barPanel.revalidate();
            }

            public void closeSearchBar(SearchableBar searchableBar) {
                barPanel.remove(searchableBar);
                barPanel.invalidate();
                barPanel.revalidate();
            }
        });
        //_textAreaSearchableBar.getInstaller().openSearchBar(_textAreaSearchableBar);
        return panel;
	}

}
