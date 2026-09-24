package com.renhejia.robot.commandlib.parser.power;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PowerMotionTest {

    @Test
    public void toStringIsJsonThatGsonReadsBack() {
        String json = new PowerMotion(3, 0).toString();
        assertEquals("{\"function\":3, \"status\":0}", json);

        PowerMotion parsed = new Gson().fromJson(json, PowerMotion.class);
        assertEquals(3, parsed.getFunction());
        assertEquals(0, parsed.getStatus());
    }

    @Test
    public void wakePairIsServoPowerThenCliff() {
        assertEquals("{\"function\":3, \"status\":1}", new PowerMotion(3, 1).toString());
        assertEquals("{\"function\":5, \"status\":1}", new PowerMotion(5, 1).toString());
    }
}
