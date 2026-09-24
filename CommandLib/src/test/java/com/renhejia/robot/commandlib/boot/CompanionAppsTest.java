package com.renhejia.robot.commandlib.boot;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CompanionAppsTest {

    @Test
    public void mcuTargetIsTheGeeUiPackage() {
        CompanionApps.Target mcu = find(CompanionApps.after1s("zh"), "com.letianpai.robot.mcuservice");
        assertEquals(
                "com.letianpai.robot.mcuservice.service.LTPMcuService",
                mcu.className);
        assertNull(mcu.action);
    }

    @Test
    public void englishSkipsMiIotButStillStartsMcu() {
        assertFalse(contains(CompanionApps.after1s("en"), "com.geeui.miiot"));
        assertTrue(contains(CompanionApps.after1s("zh"), "com.geeui.miiot"));
        assertTrue(contains(CompanionApps.after1s("en"), "com.letianpai.robot.mcuservice"));
    }

    @Test
    public void soundEffectServiceIsNotTheVoiceAssistant() {
        CompanionApps.Target sound = CompanionApps.after200ms().get(0);
        assertEquals("com.letianpai.robot.audioservice", sound.packageName);
        assertEquals(
                "com.letianpai.robot.audioservice.service.LTPAudioService",
                sound.className);
    }

    private static boolean contains(java.util.List<CompanionApps.Target> targets, String packageName) {
        return find(targets, packageName) != null;
    }

    private static CompanionApps.Target find(java.util.List<CompanionApps.Target> targets, String packageName) {
        for (CompanionApps.Target target : targets) {
            if (packageName.equals(target.packageName)) {
                return target;
            }
        }
        return null;
    }
}
