#!/bin/sh

java -classpath './lib/*:./lib/core/*:./lib/shared/*' com.gc.mimicry.Main -mainScript $1
