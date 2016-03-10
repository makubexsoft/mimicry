# RPC-Style Conversion #

In the traditional RPC-style conversion the application control flow causes an event that is propagated down the EventBridge. The application then waits till a corresponding response event is passed up from the EventBridge.

![http://mimicry.googlecode.com/files/RPC-Style-Conversion.png](http://mimicry.googlecode.com/files/RPC-Style-Conversion.png)

# Asynchronous Conversion #

In the asynchronous conversion style the application control flow simply spawn an event that is passed down the event bridge but in contrast to the [RPC-Style Conversion](#RPC-Style_Conversion.md) the application doesn't wait for any response.

![http://mimicry.googlecode.com/files/Async-Style-Conversion.png](http://mimicry.googlecode.com/files/Async-Style-Conversion.png)

# Push-Style Conversion #

In both cases depicted above the application control flow is the initiator of the event creation. This is not sufficient for resources that share common state across virtual nodes such as sockets which share their connection state. For this case the push-style conversion is used. The EventBridge spawns without any interaction of the affected application an event that is passed up to the application. Since there is no waiting application control flow it is required to have some application object holding the event state. For example a socket whose connection state is manipulated asynchronously by the event and which raises an exception once the application control flow returns.

![http://mimicry.googlecode.com/files/Push-Style-Conversion.png](http://mimicry.googlecode.com/files/Push-Style-Conversion.png)