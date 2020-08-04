#!/bin/sh
rm -rf bin
mkdir bin
cp ./src/*.xml ./bin
cp ./src/*.properties ./bin
javac -encoding UTF-8 -d ./bin -classpath .:./lib/* ./src/*.java
