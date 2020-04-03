package com.trico.salyut.utils;

import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;

public class MouseConstant {
    public final static Map<String,Integer> MOUSECONSTANT;

    static {
        MOUSECONSTANT = new HashMap<>();
        MOUSECONSTANT.put("l_click", InputEvent.BUTTON1_DOWN_MASK);
        MOUSECONSTANT.put("m_click", InputEvent.BUTTON2_DOWN_MASK);
        MOUSECONSTANT.put("r_click", InputEvent.BUTTON3_DOWN_MASK);
    }
}
