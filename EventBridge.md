The _event bridge_ is located in-between the virtualized application and the _event stack_ of the logical node the application is running on. The event bridge is responsible for wrapping the API invocations into events and pass them to the event stack of the node. The control flow of the application might block until a timeout has exceeded or a corresponding response event has been sent up in the event stack.