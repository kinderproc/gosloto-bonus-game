#!/bin/bash

set -e
set -x

branch=$(git ls-remote --heads origin | grep $(git rev-parse HEAD) | cut -d / -f 3)
version=$(grep -m 1 '<version>' pom.xml | sed 's/\(<version>\)\(.*\)\(<\/version>\)/\2/' | tr -d ' ')

echo START TESTING version: ${version}

mvn clean test -DskipTests=false -T 1C
