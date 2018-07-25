#!/usr/bin/env bash

set -e
export POJECT_BUILD_NUMBER=$(printf "%05d" $TRAVIS_BUILD_NUMBER)
export PROJECT_VERSION=$(mvn help:evaluate -N -Dexpression=project.version | grep -v '\[' | sed "s/-SNAPSHOT/-b$PROJECT_BUILD_NUMBER/")
mvn versions:set -DnewVersion=$PROJECT_VERSION -f ./epsilon-runtime-parent/pom.xml
mvn versions:set -DnewVersion=$PROJECT_VERSION
