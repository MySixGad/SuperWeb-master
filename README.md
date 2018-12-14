
#项目名: SuperWeb 
#作者: liuguo
#时间: 2018/12/12

#### 项目介绍
一个Web-H5套壳的项目框架; 都说套壳简单一个URL放进去webview就可以，但是真正去套时你会发现问题非常多，坑也非常多，没对比就没伤害，安卓和IOS还不太一样，
加载机制也不尽相同，在代码实现、扩展性、适配机型和屏幕上咱们安卓也略显劣势。举例来说在一些标签上类如<input> 安卓系统为安全起见，做了屏蔽。于是感觉很有必
要去做一套兼容性的框架，专门用来嵌套H5网页，不管是单个网页还是整个h5项目，都需要尽可能的去完全适配进来，做到无缝对接。


#### 解决问题
<1> 普通的原生api封装，支持https，比较大的H5页面显示超出屏幕的问题，访问文件，支持js,设置显示缩放按钮，缓存问题
<2> 拉不起前端"< alert >"，"< input >", "< a >"等标签解决，返回按键的处理
<3> 拍照、相册、录像、视频、下载文件等多种本地交互的及各机型适配
<4> JS交互方法的封装，动画，service等一些特殊需求，自定义加载进度条，goBack返回复加载闪屏问题。
<5> ...

#### 框架特点 
<1>  足够的机型适配
<2>  一套交互提供多个选择通道 
<3>  可扩展



#### 使用举例

	添加依赖：
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	dependencies {
	        compile 'com.github.MySixGad:SuperWeb-master:V1.0.5'
	}



	activity继承SuperWebView

	onCreate方法里复制以下代码：

       SuperWebLayout superWebLayout = (SuperWebLayout) findViewById(R.id.superWebLayout);
        SwebManager.initLayout(superWebLayout);
        //SuperWeb的进度条基本配置
        SwebManager.Progress progress = new SwebManager.Progress();
        progress.configTop(R.color.colorPrimary, R.color.colorPrimaryDark, false);
        progress.configMiddle(R.drawable.look, false);

        //SuperWeb的基础配置、上传文件配置、下载文件配置、缓存配置
        final SwebManager.Web web = new SwebManager.Web();
        web.LoadNetUrl("http://192.168.199.116/js.html")
                .configBasics(true, true, true, true, false)
                .configUpFile(SuperContact.UPFILETYPE_WEBDETIAL, "200*200", 2, "选择操作")
                .configDownFile(SuperContact.DOWNFILETYPE_WEBDETIAL, filePath)
                .configWebCache(WebSettings.LOAD_DEFAULT, false);

        //JS调用本地方法
        web.configjsInterfaceName("app", new JsCallbackMethodArea() {
            @JavascriptInterface
            public void callWXLogin() {
                Toast.makeText(MainActivity.this, "微信登录", Toast.LENGTH_SHORT).show();
            }
        });

        //本地调用JS方法
        web.executeJsMethod("wxLoginInfo", "给你参数");


	布局文件复制以下代码：

	<net.cmbt.superweb.SuperWebLayout
		android:id="@+id/superWebLayout"
		android:layout_width="match_parent"
		android:layout_height="match_parent" />


#### 解决方案
1. 项目里用好几个webview拆分目标h5项目应该怎么和js交互，token登录的问题？
2. goBack返回复加载闪屏问题？



#问题

不稳定版本，近期会修复bug和功能完善。欢迎git好友多多提出批评。
