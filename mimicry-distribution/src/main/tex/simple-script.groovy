// Initialize the network
NetworkConfiguration netCfg = [
	clockType: ClockType.REALTIME,
	initialTimeMillis: 0
]
network.init(netCfg)

// Define an EventStack
EventHandlerConfiguration[] eventStack = 
[
	[
		className: "com.gc.mimicry.plugin.tcp.PortManager"
	],
	[
		className: "com.gc.mimicry.plugin.tcp.SimpleTCPDataTransport"
	],
	[
		className: "com.gc.mimicry.plugin.tcp.TCPConnectionManager",
		configuration: 
		[
			someProp: "value-1",
			otherProp: "value-2"
		]	
	]
]

// Create a custom application descriptor
builder = new ApplicationDescriptorBuilder("My-Application")
builder.with {
	withMainClass( "org.example.MainClass" )
	withCommandLine( "some parameters" )
	withClassPath( "my-jar.jar" )
	withClassPath( "some-dependency.jar" )
}
applicationDesc = builder.build()

// Define how the node should be named and which stack to use
nodeCfg = new NodeConfiguration("ServerNode")
nodeCfg.eventStack.addAll( eventStack )

// Create the actual node and application instances within the network
nodeRef = network.spawnNode(nodeCfg)
appRef = network.spawnApplication(nodeRef, applicationDesc)
	
// Start the main thread of the application
network.startApplication(appRef)

// Start the timeline of the simulation
// The multiplier of 1.0 indicates that the simulation is running
// as fast as the system time
timeline.start(1.0)
