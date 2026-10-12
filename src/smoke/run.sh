#!/bin/sh

###
# #%L
# jinahya-object-randomizer
# %%
# Copyright (C) 2026 Jin Kwon
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###
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
