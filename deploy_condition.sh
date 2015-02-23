#!/bin/bash
jdk=$SHIPPABLE_JDK_VERSION

if [ "$jdk" == "openjdk6" ] 
	then
		mvn clean deploy
	else
		echo "This build will not be deployed!"
fi