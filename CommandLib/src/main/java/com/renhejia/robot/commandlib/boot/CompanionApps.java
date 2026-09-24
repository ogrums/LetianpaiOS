package com.renhejia.robot.commandlib.boot;

import java.util.ArrayList;
import java.util.List;

/**
 * Other apps the launcher starts. The motion MCU is the separate package
 * com.letianpai.robot.mcuservice (GeeUIMcuService), not a module of this repo.
 */
public final class CompanionApps {

    public static final class Target {
        public final String packageName;
        public final String className;
        public final String action;

        public Target(String packageName, String className, String action) {
            this.packageName = packageName;
            this.className = className;
            this.action = action;
        }
    }

    private CompanionApps() {
    }

    public static List<Target> immediate() {
        List<Target> targets = new ArrayList<>();
        targets.add(action(
                "com.letianpai.robot.geeuiresources",
                "android.intent.action.LETIANPAI.RESOURCE"));
        return targets;
    }

    public static List<Target> after200ms() {
        List<Target> targets = new ArrayList<>();
        targets.add(component(
                "com.letianpai.robot.audioservice",
                "com.letianpai.robot.audioservice.service.LTPAudioService"));
        return targets;
    }

    /**
     * @param regionLanguage value of persist property region language; "en" skips Mi IoT
     */
    public static List<Target> after1s(String regionLanguage) {
        List<Target> targets = new ArrayList<>();
        targets.add(component(
                "com.letianpai.emqxservice",
                "com.letianpai.emqxservice.EmqxService"));
        targets.add(action(
                "com.letianpai.bugreportservice",
                "android.intent.action.BUGREPORT"));
        if (!"en".equals(regionLanguage)) {
            targets.add(component(
                    "com.geeui.miiot",
                    "com.geeui.miiot.MiIotService"));
        }
        targets.add(component(
                "com.letianpai.robot.mcuservice",
                "com.letianpai.robot.mcuservice.service.LTPMcuService"));
        targets.add(component(
                "com.letianpai.robot.taskservice",
                "com.letianpai.robot.control.service.DispatchService"));
        targets.add(component(
                "com.letianpai.robot.alarmnotice",
                "com.letianpai.robot.alarmnotice.service.AlarmService"));
        return targets;
    }

    private static Target component(String packageName, String className) {
        return new Target(packageName, className, null);
    }

    private static Target action(String packageName, String action) {
        return new Target(packageName, null, action);
    }
}
