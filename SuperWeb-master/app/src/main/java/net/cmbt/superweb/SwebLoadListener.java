package net.cmbt.superweb;

import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by LG on 2018/12/12.
 */

public interface SwebLoadListener {


    //返回按钮监听  已做处理 需要自定义可重复
     void goBackListener(View v, int keyCode, KeyEvent event);

    //下载监听  已做处理 需要自定义可重复
    void onDownloadStart(String url, String s1, String s2, String s3, long l);

    //选择图片 视频等获取到的uri
    void onSeleLocFileListener(Uri uri);

    //弹出alert
    void onJsAlert(WebView view, String url, String message, JsResult result);

    //打开文件选择器
    void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams);

    //进度
    void onProgressChanged(WebView view, int newProgress);

}
