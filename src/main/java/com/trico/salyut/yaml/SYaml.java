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
package com.trico.salyut.yaml;

import com.trico.salyut.Salyut;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.token.SToken;
import com.trico.salyut.utils.Tuple2;
import org.yaml.snakeyaml.Yaml;

import java.security.Key;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Yaml解析转化类，并提供yaml格式化功能
 *
 * @author Shen Yin
 * @since 0.0.8
 */
public class SYaml {
    private static final Yaml YAML_ENGINE = new Yaml();

    private static final String TOKEN_FORMAT = "- %s: ";

    private static final String SUB_TOKEN_FORMAT = "%s: ";

    private static final String ATTRIBUTE_LIST_FORMAT = "{ %s }";

    private static final String ATTRIBUTE_FORMAT = "%s: ";

    private static final String EXPR_FORMAT = "'%s'";

    private static final String STANDARD_IDENT = "    ";

    private SYaml() {
        throw new AssertionError();
    }

    public enum  KeyType{
        TOKEN,SUB_TOKEN,ATTRIBUTE
    }


    @SuppressWarnings("unchecked")
    public static <T> T toObj(String script){
        return YAML_ENGINE.load(script);
    }

    public static String toPureCode(LinkedHashMap<String,Object> map) throws SalyutException{
        Objects.requireNonNull(map);
        String key = map.keySet().iterator().next();
        return parser(Tuple2.of(key,map.get(key)),"",KeyType.TOKEN);
    }

    public static <T> String toScript(T o){
        String yamlString =  YAML_ENGINE.dump(o);
        if (!yamlString.startsWith("-")){
            yamlString = "- " + yamlString;
        }
        return formatYaml(yamlString);

    }

    private static String formatYaml(String yamlString){
        StringBuilder builder = new StringBuilder();
        String[] lines = yamlString.split("\n");
        builder.append(lines[0]);
        builder.append("\n");
        for(int i = 1; i < lines.length; i++){
            builder.append(STANDARD_IDENT);
            builder.append(lines[i]);
            builder.append("\n");
        }
        return builder.toString();
    }

    public static String parser(Tuple2<Object,Object> tuple2, String indent, KeyType keyType) throws SalyutException{
        try{
            StringBuilder builder = new StringBuilder(indent);
            if (KeyType.TOKEN.equals(keyType)){
                builder.append(String.format(TOKEN_FORMAT, tuple2.f0));
            }
            else if (KeyType.SUB_TOKEN.equals(keyType)){
                builder.append(String.format(SUB_TOKEN_FORMAT,tuple2.f0));
            }
            else if (KeyType.ATTRIBUTE.equals(keyType)){
                builder.append(String.format(ATTRIBUTE_FORMAT,tuple2.f0));
            }

            if (tuple2.f1 instanceof String){
                builder.append(String.format(EXPR_FORMAT, tuple2.f1));
                return builder.toString();
            }
            else if (tuple2.f1 instanceof LinkedHashMap){
                Iterator it = ((LinkedHashMap) tuple2.f1).keySet().iterator();
                if (SToken.isComplexToken((String)tuple2.f0)){
                    builder.append("\n");
                    while (it.hasNext()){
                        Object key = it.next();
                        String subTokenStr = parser(Tuple2.of(key,((LinkedHashMap) tuple2.f1).get(key)),indent+STANDARD_IDENT+STANDARD_IDENT,KeyType.SUB_TOKEN);
                        builder.append(subTokenStr);
                        if (!subTokenStr.endsWith("\n")){
                            builder.append("\n");
                        }
                    }
                }
                else{
                    StringBuilder attrBuilder = new StringBuilder();
                    while (it.hasNext()) {
                        Object key = it.next();
                        attrBuilder.append(parser(Tuple2.of(key,((LinkedHashMap) tuple2.f1).get(key)),"",KeyType.ATTRIBUTE));
                        attrBuilder.append(",");
                    }

                    if (attrBuilder.length() > 0){
                        builder.append(String.format(ATTRIBUTE_LIST_FORMAT, attrBuilder.toString().substring(0,attrBuilder.length()-1)));
                    }

                }

                return builder.toString();
            }
            else if (tuple2.f1 instanceof List){
                builder.append("\n");
                List<LinkedHashMap> list = (List<LinkedHashMap>) tuple2.f1;
                for (LinkedHashMap map : list){
                    Object key =  map.keySet().iterator().next();
                    String tokenStr = parser(Tuple2.of(key,map.get(key)),indent+STANDARD_IDENT,KeyType.TOKEN);
                    builder.append(tokenStr);
                    if (!tokenStr.endsWith("\n")){
                        builder.append("\n");
                    }
                }

                return builder.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new SalyutException(SalyutExceptionType.ParseError,"yaml object to script error");
        }


        throw new SalyutException(SalyutExceptionType.UnhandledError,"script format is incorrect");
    }
}
