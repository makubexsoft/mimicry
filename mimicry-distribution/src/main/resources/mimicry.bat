@echo off

java -classpath '.;./plugins/*;./lib/*;./lib/core/*;./lib/shared/*' com.gc.mimicry.Main -mainScript %1
