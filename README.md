<p align="right">中文 | <a href="README-EN.md">EN</a></p>

# Salyut

基于标记语言的开源爬虫框架。[查看Salyut语法。](https://www.trico.cloud/tricoDoc/overview/index.html)

![GitHub license](https://img.shields.io/badge/license-Apache%202.0-blue) ![MVN version](https://img.shields.io/badge/mvn-v0.0.8-blue)

**Salyut** 是*trico script*的的解析执行引擎，通过简单的调用Salyut中方法，即可以运行*trico script*并得到相应的结果。**Salyut**是一个开源项目，您可以自行修改[expr]()目录下的类定制自己语法表达式，也可以通过修改或增加[token]()目录下的类来扩充**Salyut**的能力。

**Salyut**基于的技术

* 通过[Yaml]()来对词法进行解析，如果您对*Yaml*有一定的了解，可以更好的帮助您提升*trico script*的一些语言特性。

* 主要通过[Selenium]()来获取浏览器的操控和解析能力，如果您对*Selenium*有一定的了解可以更好的帮助您提升*trico script*的一些能力。

## 如何使用 ###
* jar包调用

	1.在工程目录使用`mvn clean package` 打包

	2.`cd target`

	3.`java  -Dscript.path=../sample/webCalc.seg -Ddriver.path=../env/geckodriver-macOS  -classpath salyut-jar-with-dependencies.jar com.trico.salyut.Salyut`

	4.参数说明
	* **script.path** - 要执行脚本的路径 样例在[sample](/sample)中可以找到。
	* **driver.path** - driver路径 在[env](/env)中可以找到。
	* **segment.path** - 预加载segment文件的路径。
	* **headless** - 是否无框启动浏览器。
	* **browser.count** - 可启动的浏览个数。
	* **newTab.path** - Salyut引擎创建新tab所需 在[env](/env)中可以找到，需要全路径。

* 代码调用

```java
Salyut.setEnv(EnvKey.DRIVER_PATH,{your driver path});
Salyut.setEnv(EnvKey.NEW_TAB_PATH,{your newTab file path});

Salyut.launch();
Salyut.setOutputListener(
        msg -> {
            //引擎处理过程中产生的消息
        }
);
Salyut.setResultListener(
        result -> {
            //返回结果
        }
);
String script = {your script content};
TricoScript script = new TricoScript(yaml);
Salyut.execScript(script.getContent(),"","");
```

## Maven ##
```mvn
<dependency>
    <groupId>com.trico.salyut</groupId>
    <artifactId>salyut</artifactId>
    <version>0.0.8-SNAPSHOT</version>
</dependency>
```



## 许可证 ###
**Salyut** 使用 [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证，您可以免费下载，修改以及部署源代码。您还可以将*Salyut Engine*进行服务化封装，从而让*Salyut Engine*具有更强大的业务处理能力。

**Salyut** 还添加了[Commons Clause 1.0]()条款，对于您通过*Salyut Engine*进行服务化封装后进行商业售卖做了限制。当然我们还是希望*Salyut Engine*能够更好的作出开源贡献，所以如果您对如何商用*Salyut Engine*有更好的想法，请随时与我们联系[feedback@trico.cloud]()

## 商业应用 ###
目前**Salyut**已经应用于[Trico Cloud](https://www.trico.cloud)商业平台，*Trico Cloud*致力于通过提供爬虫云原生服务，解决高可用，低成本数据抓取，并为爬虫开发者与使用者提供更好的爬虫生态。

## 联系方式 ###
* 邮箱: feedback@trico.cloud
* 邮箱: tigris.shin@gmail.com