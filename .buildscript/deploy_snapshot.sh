#!/bin/bash
#
# Deploy a jar, source jar, and javadoc jar to Sonatype's snapshot repo.
#
# The script was originally adapted from https://coderwall.com/p/9b_lfq and
# http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/ and
# https://github.com/JakeWharton/RxBinding/blob/master/.buildscript/deploy_snapshot.sh
#
# Now it has been adapted to work with Github Actions.

SLUG="arturbosch/detekt"
JDK="8"
REF="refs/heads/master"

set -e

if [ "$GITHUB_REPOSITORY" != "$SLUG" ]; then
  echo "Skipping snapshot deployment: wrong repository. Expected '$SLUG' but was '$GITHUB_REPOSITORY'."
elif [ "$JDK_VERSION" != "$JDK" ]; then
  echo "Skipping snapshot deployment: wrong JDK. Expected '$JDK' but was '$JDK_VERSION'."
elif [ "$GITHUB_EVENT_NAME" != "push" ]; then
  echo "Skipping snapshot deployment: was not a push triggered build."
elif [ "$GITHUB_REF" != "$REF" ]; then
  echo "Skipping snapshot deployment: wrong ref. Expected '$REF' but was '$GITHUB_REF'."
else
  echo "Deploying snapshot..."
  ./gradlew artifactoryPublish -Dsnapshot=true --stacktrace
  echo "Snapshot deployed!"
fi
