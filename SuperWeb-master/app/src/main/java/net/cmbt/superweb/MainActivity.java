package net.cmbt.superweb;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
public class MainActivity extends SuperWebView {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SuperWebLayout superWebLayout = (SuperWebLayout) findViewById(R.id.superWebLayout);
        SwebManager.initLayout(superWebLayout);
        //SuperWeb的进度条基本配置
        SwebManager.Progress progress = new SwebManager.Progress();
        progress.configTop(R.color.colorPrimary, R.color.colorPrimaryDark, false);
        progress.configMiddle(R.drawable.look, false);

        //SuperWeb的基础配置、上传文件配置、下载文件配置、缓存配置
        SwebManager.Web web = new SwebManager.Web();
        web.setLoadNetUrl("http://39.104.97.114:8888/?_loadType=app")
                .configBasics(true, true, true, true)
                .configUpFile(SuperContact.UPFILETYPE_CUSTOM, "200*200", 2, "选择操作")
                .configDownFile(SuperContact.DOWNFILETYPE_CUSTOM, "/dev/data")
                .configCacheMode(WebSettings.LOAD_DEFAULT, false);

        //JS调用本地方法
        web.configjsInterfaceName("app", new JsCallbackMethodArea() {
            @JavascriptInterface
            public void rigster(String data) {

            }
        });

        //本地调用JS方法
        web.executeJsMethod("login", "参数");
    }
}
