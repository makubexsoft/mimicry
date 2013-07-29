package com.gc.mimicry;

import com.gc.mimicry.cluster.P2PClusterNode;
import com.gc.mimicry.cluster.messaging.MessagingSystem;
import com.gc.mimicry.cluster.messaging.p2p.DefaultMessagingSystem;
import com.gc.mimicry.cluster.session.engine.SimulationEngine;

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
