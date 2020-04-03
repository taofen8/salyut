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
package com.trico.salyut.browser;

/**
 * Driver状态监听器
 *
 * @author Shen Yin
 * @since 0.0.8
 */
public interface DriverStateListener {

    /** Driver崩溃通知 */
    void driverIsDown();

    /** 手机浏览器模式通知 */
    void mobileMode();
}
