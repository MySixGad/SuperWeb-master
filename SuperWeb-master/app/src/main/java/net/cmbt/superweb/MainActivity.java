package net.cmbt.superweb;

import android.os.Bundle;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Toast;

import net.cmbt.superweb.contact.SuperContact;
import net.cmbt.superweb.view.SuperWebLayout;

public class MainActivity extends SuperWebView {

    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainvvxccccc);

        SuperWebLayout superWebLayout = (SuperWebLayout) findViewById(R.id.superWebLayout);
        SwebManager.initLayout(superWebLayout);
        //SuperWeb的进度条基本配置
        SwebManager.Progress progress = new SwebManager.Progress();
        progress.configTop(R.color.colorPrimary, R.color.colorPrimaryDark, false);
        progress.configMiddle(R.drawable.look, false);

        //SuperWeb的基础配置、上传文件配置、下载文件配置、缓存配置
        final SwebManager.Web web = new SwebManager.Web();
        web.LoadNetUrl("https://github.com/MySixGad/SuperWeb-master")
                .configBasics(true, true, true, true, true)
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

    }


}
