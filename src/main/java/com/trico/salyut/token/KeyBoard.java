/*
 * Copyright (c) 2018 tirco.cloud. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found named CC-1.0.txt.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.trico.salyut.token;

import com.trico.salyut.STab;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.utils.KeyConstant;
import com.trico.salyut.utils.MouseConstant;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TokenMark(name = "keyboard")
public class KeyBoard extends SToken {
    @Attribute(name="keyBoardList")
    private String keyBoardList;
    @Attribute(name="mouseEventList")
    private String mouseEventList;
    @Attribute(name="top")
    private String top;
    @Attribute(name="left")
    private String left;

    @Override
    public void action() throws SalyutException {
        super.action();
        if (keyBoardList!=null && keyBoardList.length() > 0) {
            System.out.println(keyBoardList);
            // 获取到键盘映射
            List<keyBoard> keyBoardList = getKeyBoardList(this.keyBoardList);
            try {
                doPressKeyBoard(keyBoardList);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else if (left != null && top != null) {
            try {
                int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
                int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

                int targetWidth = (int) (screenWidth * Double.parseDouble(left));
                int targetHeight = (int) (screenHeight * Double.parseDouble(top));

                // 移动鼠标
                doMoveMouse(targetWidth, targetHeight);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else if (mouseEventList != null) {
            try {
                doMouseEvent(getMouseEventList(mouseEventList));
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    private void doMouseEvent(List<MouseEvent> mouseEventList) throws AWTException {
        Robot robot = new Robot();
        for (MouseEvent mouseEvent : mouseEventList) {
            Integer mouseEventInteger = MouseConstant.MOUSECONSTANT.get(mouseEvent.mouseEvent);
            if (mouseEventInteger == null) {
                throw new RuntimeException(mouseEvent.mouseEvent + "该鼠标事件未添加");
            }
            robot.mousePress(mouseEventInteger);
        }
        for (MouseEvent mouseEvent : mouseEventList) {
            Integer mouseEventInteger = MouseConstant.MOUSECONSTANT.get(mouseEvent.mouseEvent);
            if (mouseEventInteger == null) {
                throw new RuntimeException(mouseEvent.mouseEvent + "该鼠标事件未添加");
            }
            robot.mouseRelease(mouseEventInteger);
        }
    }

    private void doMoveMouse(Integer x, Integer y) throws AWTException {
        Robot robot = new Robot();
        robot.mouseMove(x, y);
    }

    private void doPressKeyBoard(List<keyBoard> keyboardList) throws AWTException {
        Robot robot = new Robot();
        for (keyBoard keyBoard : keyboardList) {
            Integer keyNum = KeyConstant.KEYMAP.get(keyBoard.keyBoard);
            if (keyNum == null) {
                throw new RuntimeException(keyBoard + " 此按键还没添加");
            }
            // 按下按键
            robot.keyPress(keyNum);
        }
        for (keyBoard keyBoard : keyboardList) {
            Integer keyNum = KeyConstant.KEYMAP.get(keyBoard.keyBoard);
            if (keyNum == null) {
                throw new RuntimeException(keyBoard + " 此按键还没添加");
            }
            // 松开按键
            robot.keyRelease(keyNum);
        }
    }

    private List<MouseEvent> getMouseEventList(String mouseEventList) {
        mouseEventList = mouseEventList.replace(" ", "").toLowerCase();
        String[] mouseEvents = mouseEventList.split("\\+");

        List<MouseEvent> result = new ArrayList<>();
        for (String mouseEvent : mouseEvents) {
            MouseEvent event = new MouseEvent(mouseEvent);
            result.add(event);
        }
        return result;
    }

    private List<keyBoard> getKeyBoardList(String keyBoardList) {
        // 去除空格,转化为小写
        keyBoardList = keyBoardList.replace(" ", "").toLowerCase();
        String[] keyBoards = keyBoardList.split("\\+");

        List<keyBoard> result = new ArrayList<>();
        for (String keyBoard : keyBoards) {
            keyBoard key = new keyBoard(keyBoard);
            result.add(key);
        }
        return result;
    }

    private class MouseEvent {
        private String mouseEvent;

        public MouseEvent(String mouseEvent) {
            this.mouseEvent = mouseEvent;
        }
    }

    private class keyBoard {
        private String keyBoard;

        public keyBoard(String keyBoard) {
            this.keyBoard = keyBoard;
        }
    }
}
