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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

/**
 * <b>contains</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/contains/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "contains")
public class Contains extends SToken {
    @Attribute(name = "search", exprScan = true)
    private String search = null;
    @Attribute(name = "path", required = true)
    private String path = null;
    @Attribute(name = "regex", exprScan = true)
    private String regex = null;
    @Attribute(name = "icase", exprScan = true)
    private boolean icase = false;

    @Override
    public void action() throws SalyutException {
        super.action();
        Object pathValue = getExprValue("$"+path);
        if (!(pathValue instanceof String)){
            throw new SalyutException(SalyutExceptionType.RuntimeError,this,"this token just use for `String` type");
        }

        if (search != null && search.length() > 0){
            setResult(((String) pathValue).contains(search) ? 1 : 0);
        }
        else if (regex != null && regex.length() > 0){
            Pattern p;
            if (icase){
                p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
            }
            else{
                p = Pattern.compile(regex);
            }
            Matcher m = p.matcher(((String) pathValue));
            setResult(m.find() ? 1:0);
        }

    }
}
