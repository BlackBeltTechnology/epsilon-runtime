#!/usr/bin/env bash

set -e
export POJECT_BUILD_NUMBER=$(printf "%05d" $TRAVIS_BUILD_NUMBER)
echo "Build: $PROJECT_BUILD_NUMBER"
export PROJECT_VERSION=$(mvn help:evaluate --settings .maven.xml -N -Dexpression=project.version | grep -v '\[' | sed "s/-SNAPSHOT/-b$PROJECT_BUILD_NUMBER/")
echo "Version: $PROJECT_VERSIONs"
mvn versions:set --settings .maven.xml -DnewVersion=$PROJECT_VERSION -f ./epsilon-runtime-parent/pom.xml
mvn versions:set --settings .maven.xml -DnewVersion=$PROJECT_VERSION
