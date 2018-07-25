#!/usr/bin/env bash

set -e

export PROJECT_VERSION=$(mvn help:evaluate -N -Dexpression=project.version | grep -v '\[' | sed "s/-SNAPSHOT/-$TRAVIS_BUILD_NUMBER/")
mvn versions:set -DnewVersion=$PROJECT_VERSION -f ./epsilon-runtime-parent/pom.xml
mvn versions:set -DnewVersion=$PROJECT_VERSION