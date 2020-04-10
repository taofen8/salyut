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

import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.engine.ExecUnit;
import com.trico.salyut.STab;
import com.trico.salyut.Salyut;
import com.trico.salyut.exception.Check;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.log.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

import java.util.*;

/**
 * SBrowser
 * <p>
 * SBrowser 通过 {@link Salyut#getEnv(Salyut.EnvKey)}获取参数生成selenium的{@link RemoteWebDriver}(浏览器的主要操作对象)
 * 并且创建和维护{@link STab} 虚拟tab类来执行{@link com.trico.salyut.engine.Statements}
 * </p>
 *
 * @author shenyin
 */
public class SBrowser {
    /** driver实例 */
    private RemoteWebDriver driver;

    /** driver文件路径 */
    private String driverPath;

    /** 是否使用无框浏览器 默认为false */
    private boolean headless;

    /** 暂不处理 */
    private boolean enablePlugin = false;

    /** 新建Tab页网页地址 用来创建新Tab */
    private String newTabLink;

    /** 存储默认新建tab页的窗口句柄 */
    private String genNewTabHandler;

    /**
     * {@link STab} 目前只存在一个
     */
    private List<ExecUnit> tabList = new ArrayList<>();

    /**
     * 浏览器的执行线程 获取空闲的{@link STab}执行脚本
     */
    private SBrowserRunner<?> runner;

    /** 消息处理线程 */
    private OutputRunner output;
    public OutputRunner getOutput(){ return  output; }

    /** 是否为手机浏览器模式 */
    private boolean mobileMode;
    public boolean isMobileMode(){ return mobileMode; }

    /** 目前只存储一个job */
    private STab.Job stash;

    private DriverService driverService;

    // ------------------------------------------------------------------------

    public static SBrowser ofNonPlugin() throws SalyutException{
        return new SBrowser();
    }

    private FirefoxOptions firefoxOptions(){
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        if (headless) {
            firefoxBinary.addCommandLineOptions("--headless");
        }
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addPreference("http.response.timeout", 5);
        firefoxOptions.addPreference("dom.max_script_run_time", 5);
        firefoxOptions.addPreference("browser.tabs.remote.autostart", false);
        if (mobileMode){
            firefoxOptions.addPreference("general.useragent.override",
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 8_1_3 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Mobile/12B466 MicroMessenger/6.2.4 NetType/WIFI Language/zh_CN");
        }
        firefoxOptions.setBinary(firefoxBinary);
        return firefoxOptions;
    }

    private void createDriver(){
        if ("firefox".equals(Salyut.getEnv(Salyut.EnvKey.BROWSER_TYPE))) {
            System.setProperty("webdriver.gecko.driver", driverPath);

            if (enablePlugin) {
                //TODO:
                //add some plugin
            }
            try {
                this.driverService = GeckoDriverService.createDefaultService();
                this.driver = new FirefoxDriver(((GeckoDriverService) driverService), firefoxOptions());
            } catch (Exception e) {
                Log.logger.error("new firefox driver created error:" + e);
            }

            Log.logger.info("new firefox driver created.");
        } else if ("chrome".equals(Salyut.getEnv(Salyut.EnvKey.BROWSER_TYPE))) {
            System.setProperty("webdriver.chrome.driver", driverPath);
            ChromeOptions chromeOptions = new ChromeOptions();
            if (headless) {
                chromeOptions.addArguments("headless");

            }
            chromeOptions.addArguments("test-type");
            this.driver = new ChromeDriver(chromeOptions);
        }

        driver.get(newTabLink);
        Log.logger.info(newTabLink);
        this.genNewTabHandler = driver.getWindowHandle();
    }

    private SBrowser() throws SalyutException {
        this.driverPath = Check.requireNonNull((String) Salyut.getEnv(Salyut.EnvKey.DRIVER_PATH),"driver path is null");
        this.newTabLink = Check.requireNonNull((String) Salyut.getEnv(Salyut.EnvKey.NEW_TAB_PATH),"tab link is null");

        this.headless =  (boolean)Salyut.getEnv(Salyut.EnvKey.HEADLESS);

        int upperLimit = (int)Salyut.getEnv(Salyut.EnvKey.RECREATE_UPPER_LIMIT);

        createDriver();

        {//创建Runner 负责执行任务
            this.runner = new SBrowserRunner.Builder<>()
                    .resources(tabList)
                    .pendingJobListener(
                            ()-> {
                                Salyut.ScriptStruct scriptStruct;
                                if (null != (scriptStruct = Salyut.pollScript())){
                                    open(scriptStruct.getScript(),scriptStruct.getChannelId(),scriptStruct.getMissionId());
                                }
                            }
                    )
                    .execResultListener(
                            (result, needRecreateDriver) -> {
                                boolean succeed = !ExecResult.InterruptType.FAILED.equals(result.intType);
                                Salyut.Result ret = new Salyut.Result.Builder()
                                        .succeed(succeed)
                                        .jobId(result.jobId)
                                        .data(result.val)
                                        .build();
                                if (null != Salyut.getResultListener()){
                                    Salyut.getResultListener().get(ret.get());
                                }
                                if (!needRecreateDriver){
                                    closeRedundantTabs();
                                    changeToPCMode();
                                }
                            }
                    )
                    .driverStateListener(
                            new DriverStateListener() {
                                @Override
                                public void driverIsDown() {
                                    reset();
                                }

                                @Override
                                public void mobileMode() {
                                    changeToMobileMode();
                                }
                            }
                    )
                    .upperLimit(upperLimit)
                    .build();
        }

        {   //创建Output 负责日志输出
            this.output = new OutputRunner(Salyut.getOutputListener());
        }
    }

    private void reset() {
        mobileMode = false;
        try {
            if (null != driver){
                driver.close();
                driver.quit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (null != driverService){
                driverService.stop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        tabList.clear();

        createDriver();
        newTab(1);
    }

    /**
     * 浏览器启动
     * 1.启动执行线程
     * 2.启动消息处理线程
     */
    public void start(){
        runner.start();
        output.start();
    }

    /**
     * 停止目标job
     * @param jobId String
     * @return boolean 是否找到job
     */
    public boolean stopJob(String jobId){
        for (ExecUnit tab:tabList){
            if (tab.isBusy() && ((STab)tab).hasTargetJob(jobId)){
                ((STab) tab).stop();
                return true;
            }
        }

        return false;
    }

    /**
     * 浏览器是否空闲
     * @return boolean 有一个tab空闲即为空闲
     */
    public boolean isIdle(){
        for (ExecUnit tab:tabList){
            if (tab.isIdle()) {
                return true;
            }
        }
        return false;
    }

    public void open(String script, String channelId, String missionId){
        for (ExecUnit tab:tabList) {
            if (tab.isIdle()) {
                try {
                    ((STab) tab).open(script, channelId, missionId);
                }
                catch (Exception e){
                    if (e instanceof SalyutException){
                        getOutput().offer(channelId,missionId,e.getMessage());
                    }
                    tab.reset();
                }
                break;
            }
        }
    }



    @SuppressWarnings("unchecked")
    public void newTab(int num) {
        for (int i = 0; i < num; i++) {
            driver.switchTo().window(genNewTabHandler);
            driver.findElement(By.cssSelector("a#new-blank-tab")).click();
            STab tab = new STab(driver);
            tab.setBrowser(this);
            tabList.add(tab);
            /* sleep 避免过快而来不及渠道tab handler */
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.switchTo().window(getLastTab());
        }
    }

    /** 关闭当前活动Tab */
    public void closeTab(){
        driver.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.switchTo().window(getLastTab());
    }

    private String getLastTab(){
        String[] tabs = tabSetOfWindow();
        return tabs[tabs.length -1];
    }

    private String getTabOfIndex(long index) throws SalyutException{
        String[] tabs = tabSetOfWindow();
        if (index < 0){
            index = tabs.length + index;
        }
        if (index < 0 || index >= tabs.length){
            throw new SalyutException(SalyutExceptionType.OutOfBounds,"index value out of bounds");
        }
        return tabs[(int)index];
    }

    private String[] tabSetOfWindow(){
        Set<String> tabs = new HashSet<String>(driver.getWindowHandles());
        Set<String> sortSet = new TreeSet<String>() ;
        sortSet.addAll(tabs);
        return sortSet.toArray(new String[sortSet.size()]);
    }

    /** 切换到制定tab */
    public void switchTab(long index) throws SalyutException{
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.switchTo().window(getTabOfIndex(index));
    }

    /** 清除任务中打开的tabs */
    private void closeRedundantTabs(){
        while (tabSetOfWindow().length > 2){
            closeTab();
        }
    }

    /** 切换为手机浏览器模式 */
    private void changeToMobileMode() {
        if (mobileMode){
            return;
        }
        this.mobileMode = true;
        reset();
        jobStashPop();
    }

    /** 切换为PC浏览器模式 */
    private void changeToPCMode(){
        if (!mobileMode){
            return;
        }
        this.mobileMode = false;
        reset();
    }

    /** 保存job */
    public void jobStash(STab.Job job){
        this.stash = new STab.Job.Builder()
                .script(job.script)
                .channelId(job.channelId)
                .id(job.id)
                .build();
    }

    /** 执行保存的job */
    private void jobStashPop(){
        open(stash.script,stash.channelId,stash.id);
        this.stash = null;
    }


}

