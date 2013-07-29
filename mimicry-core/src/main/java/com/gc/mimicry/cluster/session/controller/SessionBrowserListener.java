package com.gc.mimicry.cluster.session.controller;

import com.gc.mimicry.cluster.session.SessionInfo;

public interface SessionBrowserListener
{

    public void sessionAdded(int index, SessionBrowser browser, SessionInfo session);

    public void sessionRemoved(int index, SessionBrowser browser, SessionInfo session);
}
