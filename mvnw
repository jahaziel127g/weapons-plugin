#!/bin/bash
# Maven Wrapper Script
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(dirname "$0")}"
MAVEN_OPTS="-Xms256m -Xmx512m"

exec mvn "$@"
