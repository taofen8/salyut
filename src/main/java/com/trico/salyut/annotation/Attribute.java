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

package com.trico.salyut.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {
    String name() default "";
    boolean required() default false;
    boolean exprScan() default false;
    String underKey() default "";
    boolean unique() default false;
    boolean rebuildEle() default false; // ele:'div' -> ele:'@div'
    boolean isSel() default false; //the action as the same as rebuildEle, but will expr scan first.
}
