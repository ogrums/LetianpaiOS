package com.renhejia.robot.launcherbaselib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.renhejia.robot.launcherbaselib.callback.NetworkChangingUpdateCallback;
import com.renhejia.robot.launcherbaselib.info.LauncherInfoManager;

/**
 * Wi-Fi and connectivity broadcasts. System calls here can throw on a normal
 * emulator (no DEVICE_POWER / location), so a failure must not kill the process.
 */
public class NetWorkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "NetWorkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        try {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)
                    || ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                publish(context);
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (state == WifiManager.WIFI_STATE_DISABLED) {
                    LauncherInfoManager.getInstance(context).setWifiStates(false);
                    NetworkChangingUpdateCallback.getInstance().setNetworkStatus(
                            NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED, -1);
                } else if (state == WifiManager.WIFI_STATE_ENABLED) {
                    LauncherInfoManager.getInstance(context).setWifiStates(true);
                }
            }
        } catch (RuntimeException e) {
            Log.w(TAG, "ignored " + action, e);
        }
    }

    private static void publish(Context context) {
        int type = networkType(context);
        int level = type == NetworkChangingUpdateCallback.NETWORK_TYPE_WIFI ? wifiLevel(context) : -1;
        NetworkChangingUpdateCallback.getInstance().setNetworkStatus(type, level);
    }

    private static int networkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED;
        }
        try {
            Network network = cm.getActiveNetwork();
            NetworkCapabilities capabilities = network == null ? null : cm.getNetworkCapabilities(network);
            if (capabilities == null) {
                return NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED;
            }
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return NetworkChangingUpdateCallback.NETWORK_TYPE_WIFI;
            }
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return NetworkChangingUpdateCallback.NETWORK_TYPE_MOBILE;
            }
        } catch (SecurityException e) {
            Log.w(TAG, "active network is not readable");
        }
        return NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED;
    }

    private static int wifiLevel(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return 0;
        }
        try {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info == null) {
                return 0;
            }
            return bars(info.getRssi());
        } catch (SecurityException e) {
            Log.w(TAG, "wifi info needs a location or system permission");
            return 0;
        }
    }

    /** Three bars, matching the old calculateSignalLevel(rssi, 3) range. */
    private static int bars(int rssi) {
        if (rssi >= -55) {
            return 2;
        }
        if (rssi >= -70) {
            return 1;
        }
        return 0;
    }

    public static boolean isWifiConnected(Context context) {
        return networkType(context) == NetworkChangingUpdateCallback.NETWORK_TYPE_WIFI;
    }
}
