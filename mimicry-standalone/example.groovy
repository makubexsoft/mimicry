//
// Define an EventStack for all nodes
//
EventHandlerConfiguration[] commonStack = 
[
	[
		className: "example.SomeHandler",
		configuration: 
		[
			firstParam: "foo",
			secondParam: "bar"
		]
	],
	[
		className: "example.AnotherHandler"
	]
]

//
// Create a custom application descriptor
//
appBuilder = new ApplicationDescriptorBuilder("example-app")
appBuilder.with {
	withMainClass( "examples.PingPongServer" )
	withCommandLine( "8000" )
	withClassPath( "example-app.jar" )
}
appDescription = appBuilder.build()

//
// You should install a clock before creating nodes since it is passed
// to the event handlers when the event stack gets instantiated for each node
//
clock = network.installClock(ClockType.REALTIME)

//
// Spawn 10 nodes with separated application instances
//
10.times({
	config = new NodeConfiguration("node-" + it)
	config.eventStack.addAll( commonStack )
	
	nodeRef = network.spawnNode(config)
	appRef = network.spawnApplication(nodeRef, appDescription)
	
	network.startApplication(appRef)
})

//
// Start the timeline
//
// clock.start(1.0)
