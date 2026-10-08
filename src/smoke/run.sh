#!/bin/sh
# Builds every consumer under this directory against the library as installed in the local repository, on whatever
# JDK is current; CI runs it on Java 17, the release main targets. Install the library first, e.g.
#   mvn -B install -DskipTests -P jakarta-ee-11,jakarta-ee-11-hibernate-validator
# Each consumer brings exactly one engine, and nothing of Jakarta Bean Validation.
set -eu
here=$(cd "$(dirname "$0")" && pwd)
version=${1:-$(mvn -q -f "$here/../../pom.xml" help:evaluate -Dexpression=project.version -DforceStdout)}
for pom in "$here"/*/pom.xml; do
  echo "== $(basename "$(dirname "$pom")") against $version"
  mvn -B -f "$pom" verify -Dversion.jinahya-object-randomizer="$version"
done
