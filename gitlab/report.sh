#!/bin/bash

branch=$(git ls-remote --heads origin | grep $(git rev-parse HEAD) | cut -d / -f 3)
echo "info: The branch name is ${branch}"


if [ -z "${SYNC_REPORT_MSG}" ]
  then
    echo "not found env, get msg from git last message"
    commitm=$(git log --format=%B -n 1)
  else
    echo "env founded; env=${SYNC_REPORT_MSG}"
    commitm="*${SYNC_REPORT_MSG}* from branch ${CI_COMMIT_BRANCH}"
fi

echo "info: The commit message is ${commitm}"

author=$(git --no-pager show -s --format='%ae')
echo "info: The commit author is ${author}"

sendMessage(){
  curl -F chat_id="-429417188" \
     -F parse_mode="Markdown" \
     -F disable_web_page_preview="true" \
     -F text="*${CI_PROJECT_NAME}*

Branch: ${branch}
Author: ${author}
Message: ${commitm}
Status: ${status}
${CI_PROJECT_URL}/-/pipelines
     " \
     https://api.telegram.org:443/bot1126636377:AAEkzyMc86hxRkSBjtQlXCQF1rV_lkqJ_0Q/sendMessage
}

if [ "$1" = "ok" ]; then
  status=💰💵
  sendMessage
elif [ "$1" = "error" ]; then
  status=🆘
  sendMessage
  exit 1
fi
