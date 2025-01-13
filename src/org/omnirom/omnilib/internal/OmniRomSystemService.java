/*
 * SPDX-FileCopyrightText: 2016 The CyanogenMod Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.omnirom.omnilib.internal;

import android.content.Context;
import com.android.server.SystemService;

public abstract class OmniRomSystemService extends SystemService {
    public OmniRomSystemService(Context context) {
        super(context);
    }

    public abstract String getFeatureDeclaration();


    /**
     * Override and return true if the service should be started
     * before the device is decrypted.
     */
    public boolean isCoreService() {
        return true;
    }
}
