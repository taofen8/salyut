package com.trico.salyut.utils;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public final class KeyConstant {
    public final static Map<String, Integer> KEYMAP = new HashMap<>();

    static {
        // A-Z
        KEYMAP.put("a", KeyEvent.VK_A);
        KEYMAP.put("b", KeyEvent.VK_B);
        KEYMAP.put("c", KeyEvent.VK_C);
        KEYMAP.put("d", KeyEvent.VK_D);
        KEYMAP.put("e", KeyEvent.VK_E);
        KEYMAP.put("f", KeyEvent.VK_F);
        KEYMAP.put("g", KeyEvent.VK_G);
        KEYMAP.put("h", KeyEvent.VK_H);
        KEYMAP.put("i", KeyEvent.VK_I);
        KEYMAP.put("j", KeyEvent.VK_J);
        KEYMAP.put("k", KeyEvent.VK_K);
        KEYMAP.put("l", KeyEvent.VK_L);
        KEYMAP.put("m", KeyEvent.VK_M);
        KEYMAP.put("n", KeyEvent.VK_N);
        KEYMAP.put("o", KeyEvent.VK_O);
        KEYMAP.put("p", KeyEvent.VK_P);
        KEYMAP.put("q", KeyEvent.VK_Q);
        KEYMAP.put("r", KeyEvent.VK_R);
        KEYMAP.put("s", KeyEvent.VK_S);
        KEYMAP.put("t", KeyEvent.VK_T);
        KEYMAP.put("u", KeyEvent.VK_U);
        KEYMAP.put("v", KeyEvent.VK_V);
        KEYMAP.put("w", KeyEvent.VK_W);
        KEYMAP.put("x", KeyEvent.VK_X);
        KEYMAP.put("y", KeyEvent.VK_Y);
        KEYMAP.put("z", KeyEvent.VK_Z);

        // F1-F12
        KEYMAP.put("f1", KeyEvent.VK_F1);
        KEYMAP.put("f2", KeyEvent.VK_F2);
        KEYMAP.put("f3", KeyEvent.VK_F3);
        KEYMAP.put("f4", KeyEvent.VK_F4);
        KEYMAP.put("f5", KeyEvent.VK_F5);
        KEYMAP.put("f6", KeyEvent.VK_F6);
        KEYMAP.put("f7", KeyEvent.VK_F7);
        KEYMAP.put("f8", KeyEvent.VK_F8);
        KEYMAP.put("f9", KeyEvent.VK_F9);
        KEYMAP.put("f10", KeyEvent.VK_F10);
        KEYMAP.put("f11", KeyEvent.VK_F11);
        KEYMAP.put("f12", KeyEvent.VK_F12);

        // 常用按键
        KEYMAP.put("esc", KeyEvent.VK_ESCAPE);
        KEYMAP.put("backspace", KeyEvent.VK_BACK_SPACE);
        KEYMAP.put("ctrl", KeyEvent.VK_CONTROL);
        KEYMAP.put("alt", KeyEvent.VK_ALT);
        KEYMAP.put("shift", KeyEvent.VK_ESCAPE);
        KEYMAP.put("space",KeyEvent.VK_SPACE);
        KEYMAP.put("command", KeyEvent.VK_META);
        KEYMAP.put("delete", KeyEvent.VK_DELETE);
    }
}
