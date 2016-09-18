/*
 *  Copyright 2013 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

/**
 * Interface for observing Stats reports (see webrtc::StatsObservers).
 * <p>
 * 报告状态
 */
public interface StatsObserver {
    /**
     * Called when the reports are ready.
     */
    void onComplete(StatsReport[] reports);
}
