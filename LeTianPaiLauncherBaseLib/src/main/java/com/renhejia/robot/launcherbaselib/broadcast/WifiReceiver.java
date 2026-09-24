package com.renhejia.robot.launcherbaselib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.renhejia.robot.launcherbaselib.callback.NetworkChangingUpdateCallback;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            // WiFi 状态变化
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                    // WiFi 已启用
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    // WiFi 已禁用
                    break;
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo == null) {
                return;
            }
            try {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    Log.e("letianpai_net", "net_Connect");
                    NetworkChangingUpdateCallback.getInstance().setNetworkStatus(
                            NetworkChangingUpdateCallback.NETWORK_TYPE_WIFI, 3);
                } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                    Log.e("letianpai_net", "net_Disconnect");
                    NetworkChangingUpdateCallback.getInstance().setNetworkStatus(
                            NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED, 3);
                }
            } catch (RuntimeException e) {
                Log.w("WifiReceiver", "ignored network state", e);
            }
        }
    }


}