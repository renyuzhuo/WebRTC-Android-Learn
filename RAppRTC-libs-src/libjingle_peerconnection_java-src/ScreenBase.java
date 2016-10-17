package org.webrtc;

/**
 * Screen Base, if you want to use screen capture, you should implement this ScreenBase.
 */
public interface ScreenBase {
    class ScreenBaseHealper {
        public static ScreenCapturer screenCapturer = null;
        public static final int START_SCREEN = 1;
    }
}
