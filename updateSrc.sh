echo "cp source file into RAppRTC-libs-src begin"
cp ../webrtc/src/webrtc/api/android/java/src/org/webrtc/* ./RAppRTC-libs-src/libjingle_peerconnection_java-src/
cp ../webrtc/src/webrtc/api/android/java/src/org/webrtc/* ./RAppRTC-libs-src/libjingle_peerconnection_java-src/
cp ../webrtc/src/webrtc/api/android/jni/* ./RAppRTC-libs-src/libjingle_peerconnection_so-src/
cp ../webrtc/src/webrtc/api/android/README ./RAppRTC-libs-src/README-api
echo "cp source file into RAppRTC-libs-src end"

echo "cp apks into apks begin"
cp ../webrtc/src/out/arm64/apks/AppRTCMobile.apk apks/AppRTCMobile-arm64.apk
cp ../webrtc/src/out/arm/apks/AppRTCMobile.apk apks/AppRTCMobile-arm.apk
cp ../webrtc/src/out/x64/apks/AppRTCMobile.apk apks/AppRTCMobile-x64.apk
cp ../webrtc/src/out/x86/apks/AppRTCMobile.apk apks/AppRTCMobile-x86.apk
echo "cp apks into apks end"

git status

