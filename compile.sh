#!/bin/bash

# java8
echo "--------------------------------------"
gn gen out/arm64 --args='target_os="android" target_cpu="arm64"'
gn gen out/arm --args='target_os="android" target_cpu="arm"'
gn gen out/x86 --args='target_os="android" target_cpu="x86"'
gn gen out/x64 --args='target_os="android" target_cpu="x64"'
# gn gen out/arm64-compile --args='target_os="android" target_cpu="arm64"'
echo "--------------------------------------"
ls out/
echo "--------------------------------------"
ninja -C out/arm64
ninja -C out/arm
ninja -C out/x86
ninja -C out/x64
echo "--------------------------------------"

echo "start copy"

cp out/arm64/lib.java/webrtc/api/libjingle_peerconnection_java.jar ~/AndroidStudioProjects/RAppRTC/app/libs/libjingle_peerconnection_java.jar
cp out/arm64/lib.java/webrtc/api/libjingle_peerconnection_java.jar ~/AndroidStudioProjects/RAppRTC/screencapture/libs/libjingle_peerconnection_java.jar
cp out/arm64/libjingle_peerconnection_so.so ~/AndroidStudioProjects/RAppRTC/app/libs/arm64-v8a/libjingle_peerconnection_so.so 
cp out/arm64/libjingle_peerconnection_so.so ~/AndroidStudioProjects/RAppRTC/screencapture/libs/arm64-v8a/libjingle_peerconnection_so.so

cp out/arm/lib.java/webrtc/api/libjingle_peerconnection_java.jar ~/AndroidStudioProjects/RAppRTC/app/libs/libjingle_peerconnection_java.jar
cp out/arm/lib.java/webrtc/api/libjingle_peerconnection_java.jar ~/AndroidStudioProjects/RAppRTC/screencapture/libs/libjingle_peerconnection_java.jar
cp out/arm/libjingle_peerconnection_so.so ~/AndroidStudioProjects/RAppRTC/app/libs/armeabi-v7a/libjingle_peerconnection_so.so 
cp out/arm/libjingle_peerconnection_so.so ~/AndroidStudioProjects/RAppRTC/screencapture/libs/armeabi-v7a/libjingle_peerconnection_so.so

cp out/x86/lib.java/webrtc/api/libjingle_peerconnection_java.jar ~/AndroidStudioProjects/RAppRTC/app/libs/libjingle_peerconnection_java.jar
cp out/x86/lib.java/webrtc/api/libjingle_peerconnection_java.jar ~/AndroidStudioProjects/RAppRTC/screencapture/libs/libjingle_peerconnection_java.jar
cp out/x86/libjingle_peerconnection_so.so ~/AndroidStudioProjects/RAppRTC/app/libs/x86/libjingle_peerconnection_so.so 
cp out/x86/libjingle_peerconnection_so.so ~/AndroidStudioProjects/RAppRTC/screencapture/libs/x86/libjingle_peerconnection_so.so

cp out/x64/lib.java/webrtc/api/libjingle_peerconnection_java.jar ~/AndroidStudioProjects/RAppRTC/app/libs/libjingle_peerconnection_java.jar
cp out/x64/lib.java/webrtc/api/libjingle_peerconnection_java.jar ~/AndroidStudioProjects/RAppRTC/screencapture/libs/libjingle_peerconnection_java.jar
cp out/x64/libjingle_peerconnection_so.so ~/AndroidStudioProjects/RAppRTC/app/libs/x86_64/libjingle_peerconnection_so.so 
cp out/x64/libjingle_peerconnection_so.so ~/AndroidStudioProjects/RAppRTC/screencapture/libs/x86_64/libjingle_peerconnection_so.so

echo "copy finish"
exit 0
