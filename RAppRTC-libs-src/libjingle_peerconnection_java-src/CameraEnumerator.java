/*
 *  Copyright 2016 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

/**
 * 相机枚举
 */
public interface CameraEnumerator {
    String[] getDeviceNames();

    /**
     * 是否是前置摄像头
     *
     * @param deviceName 硬件名称
     * @return 是否
     */
    boolean isFrontFacing(String deviceName);

    /**
     * 是否是后置摄像头
     *
     * @param deviceName 硬件名称
     * @return 是否
     */
    boolean isBackFacing(String deviceName);

    CameraVideoCapturer createCapturer(String deviceName,
                                       CameraVideoCapturer.CameraEventsHandler eventsHandler);
}
