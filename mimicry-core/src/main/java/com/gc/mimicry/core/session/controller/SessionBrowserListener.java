package com.gc.mimicry.core.session.controller;

import com.gc.mimicry.core.session.SessionInfo;

public interface SessionBrowserListener
{

	public void sessionAdded( int index, SessionBrowser browser, SessionInfo session );

	public void sessionRemoved( int index, SessionBrowser browser, SessionInfo session );
}
