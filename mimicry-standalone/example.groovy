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
		className: "com.gc.mimicry.plugin.tcp.PortManager"
	],
	[
		className: "com.gc.mimicry.plugin.tcp.TCPConnectionManager"	
	],
	[
		className: "com.gc.mimicry.plugin.tcp.EventLogger"
	]
]

//
// Create a custom application descriptor
//
appBuilder = new ApplicationDescriptorBuilder("example-app")
appBuilder.with {
	withMainClass( "examples.PingPongServer" )
	withCommandLine( "8000" )
	withClassPath( "../sample-simu-app/target/classes" )
}
serverAppDesc = appBuilder.build()

appBuilder = new ApplicationDescriptorBuilder("example-app")
appBuilder.with {
	withMainClass( "examples.PingPongClient" )
	withCommandLine( "127.0.0.1 8000" )
	withClassPath( "../sample-simu-app/target/classes" )
}
clientAppDesc = appBuilder.build()

//
// Initialize the network
//
NetworkConfiguration netCfg = [
	clockType: ClockType.REALTIME,
	initialTimeMillis: 0
]
network.init(netCfg)

//
// Spawn server app
//

	serverConfig = new NodeConfiguration("ServerNode")
	serverConfig.eventStack.addAll( commonStack )
	
	serverNodeRef = network.spawnNode(serverConfig)
	serverRef = network.spawnApplication(serverNodeRef, serverAppDesc)
	
	// starts the main thread of the application
	network.startApplication(serverRef)

//
// Spawn client app
//

	clientConfig = new NodeConfiguration("ClientNode")
	clientConfig.eventStack.addAll( commonStack )
	
	clientNodeRef = network.spawnNode(clientConfig)
	clientRef = network.spawnApplication(clientNodeRef, clientAppDesc)
	
	// starts the main thread of the application
	network.startApplication(clientRef)



//
// Start the timeline
//
network.getClock().start(1.0)
