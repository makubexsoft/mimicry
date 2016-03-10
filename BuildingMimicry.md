# Preconditions #

You need the following tool chain installed and configured on your pc:
  * JDK 7
  * Maven
  * Git

# Building Mimicry #

First of all you need to checkout the Mimicry sources from git.
```
> git clone https://code.google.com/p/mimicry/ 
```
Then navigate into the parent directory.
```
> cd mimicry/parent
```
Finally, run maven:
```
> mvn clean install -DskipTests
```
Note: _The Unit-Tests currently fail but I'm working on that._