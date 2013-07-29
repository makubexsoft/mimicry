// Initialize the simulation
simulation.init(NetworkConfiguration[
	clockType: ClockType.REALTIME,
	initialTimeMillis: 0
])

new com.gc.mimicry.plugin.net.tcp.TCPConnectionMonitor(simulation.getEventBroker())

// Define an EventStack that we use for both nodes
EventHandlerConfiguration[] commonStack = 
[
	[className: "com.gc.mimicry.plugin.net.PortManager"],
	[className: "com.gc.mimicry.plugin.net.tcp.SimpleTCPDataTransport"],
	[className: "com.gc.mimicry.plugin.net.tcp.TCPConnectionManager"],
	[className: "com.gc.mimicry.plugin.EventLogger"]
]

// Create a custom application descriptor
appBuilder = new ApplicationDescriptorBuilder("example-app")
appBuilder.with {
	withMainClass( "examples.PingPongServer" )
	withClassPath( "pingpong.jar" )
}
serverAppDesc = appBuilder.build()

appBuilder = new ApplicationDescriptorBuilder("example-app")
appBuilder.with {
	withMainClass( "examples.PingPongClient" )
	withClassPath( "pingpong.jar" )
}
clientAppDesc = appBuilder.build()

// Create some nodes
clientNode = simulation.createNode("ClientNode", commonStack)
serverNode = simulation.createNode("ServerNode", commonStack)

// Load applications into memory
client = simulation.loadApplication(clientNode, clientAppDesc)
server = simulation.loadApplication(serverNode, serverAppDesc)

// Attach plugin to interaction with stdin/stdout of apps
com.gc.mimicry.plugin.ConsoleWindowPlugin.attach(simulation.getEventBroker(), client);
com.gc.mimicry.plugin.ConsoleWindowPlugin.attach(simulation.getEventBroker(), server);
	
// Start the main threads of the applications
server.start("8000")
client.start("127.0.0.1", "8000")
	
// This plugin provides control of the timeline
new com.gc.mimicry.plugin.TimelineWindowPlugin(simulation, timeline);


