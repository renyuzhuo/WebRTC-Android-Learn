#!/usr/bin/env bash
rm -rf app/src/main/java/org/
cp -r ../../src/webrtc/src/webrtc/examples/androidapp/src/org ./app/src/main/java/
rm -fr app/src/main/res/
cp -r ../../src/webrtc/src/webrtc/examples/androidapp/res/ ./app/src/main/
