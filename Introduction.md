


# Introduction #

_Mimicry_ is a non-intrusive extensible simulation framework for Java applications. Instead of writing application prototypes which are tied to the simulator's API _Mimicry_ allows running real Java applications without any dependency to the simulator. You can extend _Mimicry_ to plug your custom simulations into the framework. This allows testing your deployable application binaries instead of any prototypes written specifically for simulation purposes.

# How does it work? #

The _Mimicry_ Framework uses byte-code manipulation at load-time and intercepts various API usages of the application. The intercepted invocations are wrapped into so-called _events_ which are passed to and processed by the simulation framework. The framework by default doesn't define any meaningful event handling mechanism. This event-handling logic is up to the framework's users who can write their custom so-called _event handlers_.