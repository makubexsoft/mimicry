package com.gc.mimicry;

import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.p2p.DefaultMessagingSystem;
import com.gc.mimicry.core.session.engine.SimulationEngine;
import com.gc.mimicry.net.P2PClusterNode;

public class Main {

	private static P2PClusterNode	localNode;
	private static MessagingSystem	messaging;
	private static SimulationEngine	engine;

	public static void main( String[] args ) {
		localNode = new P2PClusterNode();
		messaging = new DefaultMessagingSystem( localNode );
		engine = new SimulationEngine( messaging, localNode );
		engine.start();
	}
}
