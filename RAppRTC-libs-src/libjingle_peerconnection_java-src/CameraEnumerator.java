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
    public String[] getDeviceNames();

    /**
     * 是否是前置摄像头
     *
     * @param deviceName 硬件名称
     * @return 是否
     */
    public boolean isFrontFacing(String deviceName);

    /**
     * 是否是后置摄像头
     *
     * @param deviceName 硬件名称
     * @return 是否
     */
    public boolean isBackFacing(String deviceName);

    public CameraVideoCapturer createCapturer(String deviceName,
                                              CameraVideoCapturer.CameraEventsHandler eventsHandler);
}
