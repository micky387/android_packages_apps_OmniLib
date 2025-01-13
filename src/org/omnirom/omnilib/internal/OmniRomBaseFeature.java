/*
 * SPDX-FileCopyrightText: 2023 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.omnirom.omnilib.internal;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;

import org.omnirom.omnilib.internal.common.UserContentObserver;

import java.io.PrintWriter;

import android.provider.Settings;

public abstract class OmniRomBaseFeature {
    protected final Context mContext;
    protected final Handler mHandler;
    protected SettingsObserver mSettingsObserver;

    public OmniRomBaseFeature(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public abstract void onStart();

    protected abstract void onSettingsChanged(Uri uri);

    public abstract void dump(PrintWriter pw);

    public void start() {
        if (mSettingsObserver == null) {
            mSettingsObserver = new SettingsObserver(mHandler);
            onStart();
        }
    }

    protected final void registerSettings(Uri... settings) {
        mSettingsObserver.register(settings);
    }

    protected final boolean getBoolean(String setting, boolean defaultValue) {
        return Settings.System.getIntForUser(mContext.getContentResolver(),
                setting, (defaultValue ? 1 : 0), UserHandle.USER_CURRENT) == 1;
    }

    protected final void putBoolean(String setting, boolean value) {
        Settings.System.putIntForUser(mContext.getContentResolver(),
                setting, (value ? 1 : 0), UserHandle.USER_CURRENT);
    }

    protected final int getInt(String setting, int defaultValue) {
        return Settings.System.getIntForUser(mContext.getContentResolver(),
                setting, defaultValue, UserHandle.USER_CURRENT);
    }

    protected final void putInt(String setting, int value) {
        Settings.System.putIntForUser(mContext.getContentResolver(),
                setting, value, UserHandle.USER_CURRENT);
    }

    protected final String getString(String setting) {
        return Settings.System.getStringForUser(mContext.getContentResolver(),
                setting, UserHandle.USER_CURRENT);
    }

    protected final void putString(String setting, String value) {
        Settings.System.putStringForUser(mContext.getContentResolver(),
                setting, value, UserHandle.USER_CURRENT);
    }

    public void onDestroy() {
        mSettingsObserver.unregister();
    }

    final class SettingsObserver extends UserContentObserver {

        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void register(Uri... uris) {
            final ContentResolver cr = mContext.getContentResolver();
            for (Uri uri : uris) {
                cr.registerContentObserver(uri, false, this, UserHandle.USER_ALL);
            }

            observe();
        }

        public void unregister() {
            mContext.getContentResolver().unregisterContentObserver(this);
            unobserve();
        }

        @Override
        protected void update() {
            onSettingsChanged(null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            onSettingsChanged(uri);
        }
    }
}
