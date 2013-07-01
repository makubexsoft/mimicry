//
// Built-In Variables
// 
//   network	  SimulatedNetwork
//				  Reference to the network that is simulated
//   repository   ApplicationRepository
//				  Reference to the application repository that can be used to load applications from
//

//
// Define an EventStack for all nodes
//
EventHandlerConfiguration[] commonStack = 
[
	[
		className: "com.gc.mimicry.plugin.tcp.SimpleTCPSimulation",
		configuration: 
		[
			firstParam: "foo",
			secondParam: "bar"
		]
	],
	[
		className: "com.gc.mimicry.plugin.tcp.PortManager"
	]
]

//
// Create a custom application descriptor
//
appBuilder = new ApplicationDescriptorBuilder("example-app")
appBuilder.with {
	withMainClass( "examples.PingPongServer" )
	withCommandLine( "8000" )
	withClassPath( "../mimicry-core/sample-app.jar" )
}
appDescription = appBuilder.build()

//
// Initialize the network
//
NetworkConfiguration netCfg = [
	clockType: ClockType.REALTIME,
	initialTimeMillis: 0
]
network.init(netCfg)

//
// Spawn 10 nodes with separated application instances
//
10.times({
	config = new NodeConfiguration("node-" + it)
	config.eventStack.addAll( commonStack )
	
	nodeRef = network.spawnNode(config)
	appRef = network.spawnApplication(nodeRef, appDescription)
	
	// starts the main thread of the application
	network.startApplication(appRef)
})

//
// Start the timeline
//
network.getClock().start(1.0)
