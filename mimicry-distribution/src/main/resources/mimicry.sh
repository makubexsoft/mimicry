#!/bin/sh

java -classpath '.:./plugins/*:./lib/*' org.mimicry.Main -mainScript $1
