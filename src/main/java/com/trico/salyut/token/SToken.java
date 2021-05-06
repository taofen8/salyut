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

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trico.salyut.Salyut;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.engine.Statements;
import com.trico.salyut.expr.ExprASTContext;
import com.trico.salyut.expr.ExprMainLoop;
import com.trico.salyut.expr.ExprTokenParser;
import com.trico.salyut.expr.ID;
import com.trico.salyut.expr.ast.ExprASTException;
import com.trico.salyut.expr.ast.VariableExprAST;
import com.trico.salyut.lambda_expr.LambdaExprASTContext;
import com.trico.salyut.lambda_expr.LambdaExprMainLoop;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.log.MessageTag;
import com.trico.salyut.yaml.SYaml;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;

import com.trico.salyut.STab;
import com.trico.salyut.path.Path;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;

/**
 * 所有Token的基类
 * @author shenyin
 */
public abstract class SToken implements ExprASTContext, LambdaExprASTContext {
	private String tokName;
    protected STab atTab;
    private Integer line;
    private Object param;
    private Long id;
    protected Statements<SToken> statements;
    protected Statements<SToken> catchStatements;

    private Long level = 0L;

    private ExecResult execResult;


	public  SToken prev;

    /** 从属于哪个block token[loop|segment|then|else] */
	public SToken blockTok;

	protected Object result = null;
	protected String yamlString;

    /** 从某个节点开始css selector查找 */
    private WebElement subFinder;

    /** 是否通过css selector进行集合查找  */
    private  boolean findMultiEles = false;

    /** token id计数器  */
	private static AtomicLong icr = new AtomicLong(0);

	public SToken() {
	    setExecResult(ExecResult.getThrough());
    }

    public static boolean isTargetTok(String tokName, Class<? extends SToken> tokClass){
        return tokName.equals(tokClass.getAnnotation(TokenMark.class).name());
    }

    public static boolean isComplexToken(String tokenStr){
        return "segment".equals(tokenStr) || "loop".equals(tokenStr) || "try".equals(tokenStr);
    }

    public boolean isSegment(){
	    return this instanceof Segment;
    }

    public boolean isIf(){
        return this instanceof If;
    }

    public boolean isElif(){
        return this instanceof Elif;
    }


    protected void parseParam(Object param) throws SalyutException{}

    public boolean avoidOutputMessage(){
	    return this instanceof Then;
    }

    public static <T extends SToken> T ofClz(Class<T> clz, Object param, STab tab, Long level, Integer lineNo,SToken blockTok,String script) throws SalyutException{
	    T token;
        try {
            token = clz.newInstance();

            /** 对于SToken的子类隐藏一些字段 */
            Field[] fields = clz.getSuperclass().getDeclaredFields();
            for (Field field:fields){
                field.setAccessible(true);
                if ("tokName".equals(field.getName())){
                    field.set(token,clz.getAnnotation(TokenMark.class).name());
                }
                else if ("param".equals(field.getName())){
                    field.set(token,param);
                }
                else if ("line".equals(field.getName())){
                    field.set(token,lineNo);
                }
                else if ("id".equals(field.getName())) {
                    field.set(token,icr.incrementAndGet());
                }
                else if ("level".equals(field.getName())) {
                    field.set(token,level);
                }
            }

            token.atTab = tab;
            token.blockTok = blockTok;
            token.yamlString = script;


            token.parseParam(param);


            if (token instanceof Block){
                String blockKey = ((Block) token).key();
                if (Objects.isNull(blockKey)){
                    token.statements = Statements.of(SToken.getList((List<Map<String,Object>>)param,tab,token.getLevel()+1,token));
                }
                else{
                    Map<String,Object> map = (Map<String,Object>)param;
                    /** 去除行号的影响 拿到key下面的token list*/
                    Iterator<String> it = map.keySet().iterator();
                    List<Map<String,Object>> list = null;
                    while(it.hasNext()){
                        String key = it.next();
                        if (cleanKey(key).equals(blockKey)){
                            list = (List<Map<String,Object>>)map.get(key);
                            break;
                        }
                    }
                    token.statements = Statements.of(SToken.getList(list,tab,token.getLevel()+1,token));

                    if (((Block) token).needCatch()){
                        while(it.hasNext()){
                            String key = it.next();
                            if (cleanKey(key).equals("catch")){
                                list = (List<Map<String,Object>>)map.get(key);
                                break;
                            }
                        }
                        token.catchStatements = Statements.of(SToken.getList(list,tab,token.getLevel()+1,token));
                    }

                }

            }
        }
        catch (Exception e){
            if (e instanceof SalyutException){
                throw (SalyutException)e;
            }
            else{
                token = null;
            }
        }

        return token;
    }

	/** Constructions */
    /**
     * 将Yaml load后的数据结构转化为 {@link SToken}列表,并在过程中处理缩进，行号等信息
     * 继承{@link SToken}后，如果要自定义的token可以被解析并执行
     * <p>
     * 根据相应的tokenName 创建实例
     * </p>
     *
     * @param list yaml对象
     * @param tab  当前tab
     * @param level 当前创建的token列表所在层级
     * @param blockTok 当前创建的token列表所在的块级token
     * @return {@link SToken}列表
     * @throws SalyutException
     * <p>
     * 块级token包括以下
     * @see Segment
     * @see Loop
     * @see Then
     * @see Else
     * </p>
     */
	public static List<SToken> getList(List<Map<String,Object>> list,STab tab, Long level, SToken blockTok) throws SalyutException{
	    if (null == list){
	        throw new SalyutException(SalyutExceptionType.ParseError, blockTok, "can not find sub tokens");
        }
        List<SToken> tokens = new ArrayList<>();
        String scriptWithToken;
        for (Map<String,Object> tokenMap : list){
            if (null == tokenMap){
                throw new SalyutException(SalyutExceptionType.ParseError, blockTok, "can not parse \"-\" symbol. Did you forget writing token name?");
            }
            scriptWithToken = SYaml.toScript((LinkedHashMap<String, Object>) tokenMap);
            String key = tokenMap.keySet().iterator().next();
            String tokenName = key;
            String line = null;
            if (key.contains("_")){
                String[] keyAndLine = key.split("_");
                tokenName = keyAndLine[0];
                line = keyAndLine[1];
            }

            SToken token = null;
            Integer lineNo = line != null ? Integer.parseInt(line):null;

            Class<? extends SToken> tokenClz = tab.tokenClzMap.get(tokenName);
            if (null != tokenClz){
                try {
                    token = SToken.ofClz(tokenClz,tokenMap.get(key),tab,level,lineNo,blockTok,scriptWithToken);
                }
                catch (Exception e){
                    if (e instanceof SalyutException){
                        throw (SalyutException)e;
                    }
                    else{
                        token = null;
                    }
                }
            }

            if (token == null){
                tab.offerMessageWithTag("[warning]:find undefined token"+tokenMap.toString(), MessageTag.Warning);
                continue;
            }

            if (tokens.size() > 0){
                token.prev = tokens.get(tokens.size()-1);
            }

            tokens.add(token);
        }
        return tokens;
    }

    public static List<Segment> getSegmentList(String script) throws SalyutException{

        try {
            List<Segment> segmentList = new ArrayList<>();
            List<Map<String,Object>> list = SYaml.toObj(script);
            String subYamlString;
            for (Map<String,Object> tokenMap : list) {
                subYamlString = SYaml.toScript(tokenMap);
                String key = tokenMap.keySet().iterator().next();
                String tokenName = key;
                if (key.contains("_")){
                    String[] keyAndLine = key.split("_");
                    tokenName = keyAndLine[0];
                }
                if (SToken.isTargetTok(tokenName,Segment.class)){
                    segmentList.add(Segment.produceSelf(subYamlString,null));

                }
            }
            return segmentList;
        }catch (Exception e){
            if (e instanceof SalyutException){
                throw e;
            }
            else{
                throw new SalyutException(SalyutExceptionType.ParseError,"get segment list error:"+e.getMessage());
            }
        }

    }

    /**
     * LambdaExprASTContext 实现
     * {@link LambdaExprASTContext}
     */
    @Override
    public ID exec(String script, Object param) {
        return new ID(((JavascriptExecutor) atTab.driver).executeScript(script,param));
    }

    /**
     * ExprASTContext 实现
     * {@link ExprASTContext}
     */
    @Override
    public ID segmentScopeVal() throws ExprASTException{
        Segment segToken = checkInSegmentBlock();
        if (segToken != null){
            ExprTokenParser parser = new ExprTokenParser("$"+segToken.getSegmentPath());
            parser.getNextToken();
            return new VariableExprAST(this,parser,false).getVal();
        }

        return null;
    }

    @Override
    public boolean isGlobalVar(String path) {
        return Path.isGlobalVar("/"+path);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ID setValue(ID base, String label, Long index, Object val) {
        if (null == base){
            atTab.getPath().getStorage().put(label,val);
        }
        else {
            if (null == index){
                ((Map) base.val()).put(label,val);
            }
            else {
                if (index.intValue() == -1){
                    ((List) base.val()).add(val);
                }
                else{
                    ((List) base.val()).add(index.intValue(),val);
                }

            }
        }

        return new ID(val);
    }

    @Override
    public ID getValue(ID base, String label, Long index) {
        if (null == base){
            if (label.length() == 0){
                return new ID(atTab.getPath().getStorage());
            }
            else{
                return new ID(atTab.getPath().getStorage().get(label));
            }
        }
        else{
            if (null == index){
                return new ID(((Map) base.val()).get(label));
            }
            else{
                return new ID(((List) base.val()).get(index.intValue()));
            }
        }
    }

    @Override
    public void removeValue(ID base, String label, Long index) {
        if (null == base){
            atTab.getPath().getStorage().remove(label);
        }
        else {
            if (null == index){
                ((Map) base.val()).remove(label);
            }
            else{
                ((List) base.val()).remove(index.intValue());
            }
        }
    }

    @Override
    public ID getReservedValue(String rId) throws ExprASTException {
        if (atTab.getPath().isReserved(rId)){
            if ("0".equals(rId) || "1".equals(rId)){
                return ExprMainLoop.getReversed("$"+reversedPath(rId),this);
            }
            else if (atTab.getPath().isReservedConst(rId)){
                switch (rId){
                    case "true": return new ID(true);
                    case "false": return new ID(false);
                    case "now": return new ID(System.currentTimeMillis());
                    case "null": return new ID(null);
                    default:
                }
            }
            else{
                return getNearestReversed(rId,blockTok.level);
            }
        }

        return null;
    }

    @Override
    public ID getWebElement(String cssSelector) throws ExprASTException{

        if (this instanceof Wait){
            return new ID(cssSelector);
        }
        else{
            try{
                if (subFinder != null){
                    if (findMultiEles){
                        return new ID(subFinder.findElements(By.cssSelector(cssSelector)));
                    }
                    else {
                        return new ID(subFinder.findElement(By.cssSelector(cssSelector)));
                    }
                }
                else{
                    if (findMultiEles){
                        return new ID(atTab.driver.findElementsByCssSelector(cssSelector));
                    }
                    else{
                        return new ID(atTab.driver.findElementByCssSelector(cssSelector));
                    }
                }
            }catch (WebDriverException e){
                throw new ExprASTException(e.getMessage());
            }

        }
    }

    private ID getNearestReversed(String reversedValue, Long startLevel) throws  ExprASTException{
        ID e = null;
        while((e == null || e.isNull()) && startLevel >= 0){
            e = ExprMainLoop.getReversed("$"+reversedPath(reversedValue,startLevel),this);
            startLevel--;
        }
        return e;
    }

    /**
     * 将val赋值到保留字
     * @param reversed {@link Path.Reversed}
     * @param val 赋值对象
     * @throws SalyutException salyut异常
     */
    public void setExprValue(Path.Reversed reversed, Object val) throws SalyutException {
        String path = reversedPath(reversed.getValue());
        try{
            ExprMainLoop.set(path,this,val);
        }catch (ExprASTException e){
            throw new SalyutException(e.getType(),this,e.getAddonMessage());
        }

    }

    /**
     * 将val赋值到表达式路径
     * @param expr  表达式
     * @param val   赋值对象
     * @param ignoreSegPath 对于在segment中的路径 会追加{@link Segment#getSegmentPath()},
     *                      但是{@link Callin} 在初始化的时候已经加入了segment path，需要通过
     *                      ignoreSegPath = true来避免重复添加
     * @throws SalyutException salyut异常
     */
    public void setExprValue(String expr,Object val, boolean ignoreSegPath) throws SalyutException{
        if (!ignoreSegPath){
            Segment segToken = checkInSegmentBlock();
            if (segToken != null){
                expr = segToken.getSegmentPath() + expr;
            }
        }

        try {
            ExprMainLoop.set(expr,this,val);
        }
        catch (ExprASTException e){
            throw new SalyutException(e.getType(),this,e.getAddonMessage());
        }
    }

    /**
     * 调用{@link #setExprValue(String, Object, boolean)}
     * @param expr  表达式
     * @param val   赋值对象
     * @throws SalyutException salyut异常
     */
    public void setExprValue(String expr,Object val) throws SalyutException{
        setExprValue(expr,val,Path.isGlobalVar(expr));

    }

    public Object getExprValue(String expr) throws SalyutException{

        try{
            return ExprMainLoop.get(expr, this).val();
        }catch (ExprASTException e){
            throw new SalyutException(e.getType(),this,e.getAddonMessage());
        }
    }


    void removeExprValue(String expr) throws SalyutException{
        Segment segToken = checkInSegmentBlock();
        if (segToken != null){
            expr = segToken.getSegmentPath() + expr;
        }

        try {
            ExprMainLoop.remove(expr,this);
        }
        catch (ExprASTException e){
            throw new SalyutException(e.getType(),this,e.getAddonMessage());
        }

    }

    /**
     * 调用{@link Put}中支持的lambda表达式 语法解析器
     * 该lambda表达式 将会被JS引擎 {@link JavascriptExecutor}所执行
     * @return JS函数执行的返回的对象
     */
    Object execLambda(String expr,Object val) throws SalyutException{
        return LambdaExprMainLoop.execLambda(expr,this,val).val();
    }

    /**
     * 设置Token的返回值，分别作用于保留字 $0,$1
     * $0 token的名字
     * $1 token处理的结果
     * <p>
     *     - find: {ele:'#body'}
     *     - if: '$1'
     * </p>
     *
     * @param o result对象
     * @throws SalyutException salyut异常
     */
	public void setResult(Object o) throws SalyutException {
        try{
            setExprValue(Path.Reversed.R0,tokName);
            setExprValue(Path.Reversed.R1,o);
            this.result = o;
        }catch (ExprASTException e){
            throw  new SalyutException(SalyutExceptionType.ExprASTError,e.getMessage());
        }
	}

    /**
     * 当调用该函数时 {@link #getWebElement} 将获取css selector表达式
     * 下的所有节点, 同时可以调用 {@link #findMultiElesOff()} 来关闭。
     */
    void findMultiElesOn(){
	    this.findMultiEles = true;
    }

    void findMultiElesOff(){
        this.findMultiEles = false;
    }

	public static String cleanKey(String key) { // 去除行号
		Pattern p = Pattern.compile("([a-z]*)");
		Matcher m = p.matcher(key);
		if (m.find()){
            return m.group(1);
        }
		return null;
	}

    /**
     * 处理{@link Attribute},完成成员变量的内容装载
     */
	@SuppressWarnings("unchecked")
	private void processAttribute() throws SalyutException{
        Class thisClz = this.getClass();
        Field[] fields = thisClz.getDeclaredFields();
        for(Field field:fields){
            Attribute annotation = null;
            if ((annotation = field.getAnnotation(Attribute.class)) != null){
                if (annotation.name().length() > 0){
                    if (annotation.underKey().length() > 0){
                        Map paramMap = ((Map<String,Object>) param);
                        Iterator<String> it = paramMap.keySet().iterator();
                        while(it.hasNext()){
                            String key = it.next();
                            if (cleanKey(key).equals(annotation.underKey())){
                                Map secondMap = ((Map<String,Object>) paramMap.get(key));
                                String value = (String)secondMap.get(annotation.name());
                                if (null == value && annotation.required()){
                                    throw new SalyutException(SalyutExceptionType.ParseError,this,"must have "+annotation.name()+" attr.");
                                }

                                if (null == value){
                                    continue;
                                }

                                if (annotation.rebuildEle()){
                                    if (!value.startsWith("$")){
                                        value = "@"+value;
                                    }
                                }
                                try{
                                    field.setAccessible(true);
                                    if (annotation.exprScan()){
                                        field.set(this,getExprValue(value));
                                    }
                                    else if (annotation.isSel()){
                                        field.set(this,value);
                                    }
                                    else{
                                        field.set(this,value);
                                    }
                                }catch (Exception e){
                                    throw new SalyutException(SalyutExceptionType.RuntimeError,e.getMessage());
                                }

                            }
                        }
                    }
                    else{
                        String value;
                        if (annotation.unique()){

                            value = (String) param;
                        }
                        else{
                            Map paramMap = ((Map<String,Object>) param);
                            value = (String) paramMap.get(annotation.name());
                        }

                        if (null == value && annotation.required()){
                            throw new SalyutException(SalyutExceptionType.ParseError,this,"must have "+annotation.name()+" attr.");
                        }

                        if (null == value){
                            continue;
                        }

                        if (annotation.rebuildEle()){
                            if (!value.startsWith("$")){
                                value = "@"+value;
                            }
                        }

                        try {
                            field.setAccessible(true);
                            if (annotation.exprScan()) {
                                field.set(this, getExprValue(value));

                            }
                            else if (annotation.isSel()){
                                field.set(this, value);
                            }
                            else {
                                field.set(this, value);
                            }
                        }catch (Exception e){
                            if (e instanceof NullPointerException){
                                throw new SalyutException(SalyutExceptionType.RuntimeError,this,e.getMessage());
                            }
                            else{
                                if (e instanceof SalyutException){
                                    throw ((SalyutException) e);
                                }
                                else{
                                    throw new SalyutException(SalyutExceptionType.UnhandledError,this,e.getMessage());
                                }

                            }

                        }

                    }
                }
            }
        }
    }

    public WebElement getWebElementByExpr(String expr) throws SalyutException{
        Object value = getExprValue(expr);

        if (value instanceof WebElement){
            return ((WebElement) value);
        }
        else if (value instanceof String){
            value  =  getExprValue("@"+value);

            if (value instanceof WebElement){
                return ((WebElement) value);
            }

        }

        throw new SalyutException(SalyutExceptionType.RuntimeError,this,"can not get web element from expr `"+expr+"`");
    }


    public List<WebElement> getWebElementsByExpr(String expr) throws SalyutException{
        Object value = getExprValue(expr);
        if (value instanceof List){
            List list = ((List) value);
            boolean pass = true;
            for (Object o:list){
                if (!(o instanceof WebElement)){
                    pass = false;
                    break;
                }
            }

            if (pass){
                return ((List) value);
            }

        }
        else if (value instanceof String){
            findMultiElesOn();
            value  =  getExprValue("@"+value);
            findMultiElesOff();

            if (value instanceof List){
                List list = ((List) value);
                boolean pass = true;
                for (Object o:list){
                    if (!(o instanceof WebElement)){
                        pass = false;
                        break;
                    }
                }

                if (pass){
                    return ((List) value);
                }
            }
        }

        throw new SalyutException(SalyutExceptionType.RuntimeError,this,"can not get web elements from expr `"+expr+"`");
    }

	public void action() throws SalyutException {
        processAttribute();
        if (atTab.needStop()){
            throw new SalyutException(SalyutExceptionType.Stop,this,"stop at "+tokName);
        }
    }

	public void setExecResult(ExecResult result){
	    this.execResult = result;
	    if (atTab != null){
            this.execResult.jobId = atTab.jobId();
        }

    }
	public ExecResult getExecResult(){
	    return execResult;
    }

	@Override
	public String toString() {
		return " token: " + tokName;
	}


	private String _innerPath(String p) {
		String ret = p;
		Segment segToken;
		if ((segToken = this.checkInSegmentBlock()) != null) {
			ret = segToken.getSegmentPath() + ret;
		}
		return ret;
	}

	private String reversedPath(String reversedValue){
        return  reversedPath(reversedValue,level);
    }

    private String reversedPath(String reversedValue, Long level){
        return _innerPath("/" + Path.INNER_VAR_PREFIX + level + reversedValue);
    }

    String sysPath() {
		return "/" + Path.INNER_VAR_PREFIX + "sys";
	}

	private Segment checkInSegmentBlock() {
		SToken b = blockTok;
		while (b != null) {
			if (b.isSegment()) {
				return (Segment) b;
			}
			b = b.blockTok;
		}
		return null;
	}


	//恢复token的一些状态 以便于下次调用
	public ExecResult restoreAndCopyResult(){
        this.subFinder = null;
        ExecResult copy = execResult.copy();
        setExecResult(ExecResult.getThrough());
        return copy;
    }

    // ------------------------------------------------------------------------
    //  getter and setter
    // ------------------------------------------------------------------------
    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    void setSubFinder(WebElement finder){
        this.subFinder = finder;
    }

    public Object getResult() {
        return result;
    }

    public String yamlString() {
        return yamlString;
    }

    public STab getTab() {
        return atTab;
    }

    Long getLevel() {
        return level;
    }

    public Object getParam(){
        return param;
    }

    public String getTokName(){
        return tokName;
    }

}