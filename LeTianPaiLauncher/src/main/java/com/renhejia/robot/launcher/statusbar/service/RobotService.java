package com.renhejia.robot.launcher.statusbar.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.renhejia.robot.commandlib.boot.CompanionApps;
import com.renhejia.robot.guidelib.utils.SystemUtil;
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager;
import com.renhejia.robot.launcher.nets.GeeUINetResponseManager;
import com.renhejia.robot.launcher.system.LetianpaiFunctionUtil;
import com.renhejia.robot.launcherbaselib.callback.NetworkChangingUpdateCallback;

import java.util.List;

public class RobotService extends Service {
    private Context mContext;
    private boolean isWifiConnected = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = RobotService.this;
        handler = new Handler(getMainLooper());
        addListeners();
        startServices();
    }

    private void startServices() {
        if (LetianpaiFunctionUtil.isFactoryRom()) {
            LetianpaiFunctionUtil.openFactoryApp(RobotService.this);
            return;
        }
        String region = SystemUtil.get(SystemUtil.REGION_LANGUAGE, "zh");
        startAll(CompanionApps.immediate());
        handler.postDelayed(() -> startAll(CompanionApps.after200ms()), 200);
        handler.postDelayed(() -> {
            startAll(CompanionApps.after1s(region));
            startDispatchService();
        }, 1000);
    }

    private void startAll(List<CompanionApps.Target> targets) {
        for (CompanionApps.Target target : targets) {
            try {
                Intent intent = new Intent();
                if (target.className != null) {
                    intent.setComponent(new ComponentName(target.packageName, target.className));
                } else {
                    intent.setPackage(target.packageName);
                    intent.setAction(target.action);
                }
                startService(intent);
            } catch (Exception e) {
                Log.e("RobotService", "start failed " + target.packageName, e);
            }
        }
    }

    private void addListeners() {
        NetworkChangingUpdateCallback.getInstance().registerChargingStatusUpdateListener(new NetworkChangingUpdateCallback.NetworkChangingUpdateListener() {
            @Override
            public void onNetworkChargingUpdateReceived(int networkType, int networkStatus) {
                Log.e("RobotService", "networkType: " + networkType);
                if (networkType == NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED) {
                    isWifiConnected = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isWifiConnected) {
                                WIFIConnectionManager.getInstance(mContext).connect();
                            }
                        }
                    }, 60000 * 2);
                } else {
                    isWifiConnected = true;
                    getLogo();
                    Log.e("RobotService", "----网络连接- networkType ----: " + networkType);
                }
            }
        });
    }

    private void getLogo() {
        GeeUINetResponseManager.getInstance(this).getLogoInfo();
    }

    private void startDispatchService() {
        Intent intent = new Intent(RobotService.this, DispatchService.class);
        startService(intent);
    }
}
