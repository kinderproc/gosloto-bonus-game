#!/bin/bash

set -e
set -x

env=""$1""

if [ -z "$1" ]
  then
    echo "No argument supplied, try set from env; env=${SYNC_ENV}"
    env="${SYNC_ENV}"
fi


  git config --global user.email $(git --no-pager show -s --format='%ae')
  git config --global user.name "deployment"
  git clone git@git.finch.fm:gosloto/gosloto-k8s-images.git -b "${env}" /opt
  sed -ri "s/tag: [0-9A-Za-z.-]*/tag: ${CI_PIPELINE_ID}/" /opt/finch-k8s/values."${CI_PROJECT_NAME}"."${env}".yaml
  cd /opt
  git status
  git add -A
  git commit -m 'change image tag'
  git pull
  git push
