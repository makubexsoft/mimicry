#!/bin/sh

java -classpath '.:./plugins/*:./lib/*' com.gc.mimicry.Main -mainScript $1
