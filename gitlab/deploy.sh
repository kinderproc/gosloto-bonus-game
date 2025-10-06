#!/bin/bash

# $1 - project
# $2 - serviceName
# $3 - pipeline number

set -e
set -x

# variable for deploy
branch=$(git ls-remote --heads origin | grep $(git rev-parse HEAD) | cut -d / -f 3)
version=$(grep -m 1 '<version>' pom.xml | sed 's/\(<version>\)\(.*\)\(<\/version>\)/\2/' | tr -d ' ')
dockerImageLink=""$1"/"$2":${version}"
dockerImageTag=""$1"/"$2":"$3""


#print version in logs
#color echo text https://stackoverflow.com/questions/5947742/how-to-change-the-output-color-of-echo-in-linux
echoBlueColor='\033[0;34m'
echoRedColor='\033[0;31m'
NC='\033[0m' # No Color
echo -e " ${echoRedColor} START DEPLOY "$2" ${NC}"
echo -e " ${echoBlueColor} BRANCH: ${branch} ${NC}"
echo -e " ${echoBlueColor} VERSION: ${version} ${NC}"
echo -e " ${echoBlueColor} dockerImageLink=${dockerImageLink} ${NC}"
echo -e " ${echoBlueColor} dockerImageTag=${dockerImageTag} ${NC}"


  if [ "$branch" != "master" ]; then
    mvn -B clean install deploy -DskipTests=true -T 1C -am -amd
  else
    echo -e " ${echoRedColor} DEPLOY JAR ARTIFACT TO NEXUS IGNORING ${NC}"
    mvn clean install -DskipTests=true -T 1C
  fi

mvn -pl "$2" jib:dockerBuild


#push to docker registry
docker tag ${dockerImageLink} ${dockerImageTag}
docker push ${dockerImageTag}

#TODO: need configure push only for prod
#docker push ${dockerImageLink}
