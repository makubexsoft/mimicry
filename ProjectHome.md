Design and implementation of distributed systems is typically done in at least two phases:
  1. Design and verify algorithm and architecture
  1. Implement distributed system accordingly
In the first one the actual algorithm and architecture is designed.
To verify that the algorithm and architecture of the system scales well you might use network simulators such as [ns-2](http://www.isi.edu/nsnam/ns/).
Those simulators typically require you to write a prototype using a special API.
Due to the use of this specialized API and the implied architecture constraints it's often not feasible to reuse the prototype's code.

After the system has been design and verified to scale the actual production code needs to be written. Finally the built system needs to be tested again. Using integration-tests at this point is often not sufficient since you don't have fine grained control of the environment and exceptional behaviour that might occur.

This is the point where Mimicry comes into scene...

# What is Mimicry? #

_Mimicry_ is a non-intrusive extensible emulation framework which is intended to assist you in writing stable distributed systems. It allows you to run any compiled JVM application (e.g. written in Java, Scala, Groovy, etc.) in a virtual environment without introducing any dependency to a special API. Just compile your production ready application and deploy it to Mimicry which gives you full control of the aspects of the emulated environment such as:
  * Time
    * use a discrete or realtime clock to model your timeline
    * start, stop or advance the time manually
  * Random Numbers
    * Specify what an application receives from random number generators
  * Network Interactions
    * various protocols TCP, UDP, DNS, DHCP, etc.
    * Jitter, Delay, Bandwidth, Drop Rates, etc.
  * File System Access
    * model any file system layout

# How does it work? #

The _Mimicry_ Framework uses byte-code manipulation at load-time to intercept the application's interactions with the JVM. Those interactions are then transformed into specified events that are processed by Mimicry's engine. Depending on the interaction the application's control flow might block until Mimicry returns the computed result. By default Mimicry provides basic interaction models that can be plugged into the underlying engine but allows you to write your custom emulation models depending on what your requirements are. Furthermore Mimicry supports custom plugins that intercept additional JVM interactions and transform them into custom events.

### Limitations ###
  * **Not yet implemented but planned**
    * NIO - New IO package
    * ICMP - Internet Control Messaging Protocol
    * SSL - Secure Socket Layer
    * DHCP - Dynamic Host Configuration Protocol
  * **Evaluation of the following features required**
    * RMI - Remote Method Invocations
    * JNDI - Java Naming and Directory Interface
  * **Currently not covered by the scope of Mimicry**
    * JNI - Java Native Interface



---


This project leverages the JIDE Docking Framework (http://www.jidesoft.com/).