package net.cmbt.superweb;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wildma.pictureselector.PictureSelector;

import net.cmbt.superweb.inteface.SwebLoadListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class SuperWebView extends Activity {
    private static final int ACTIVITY_CAMREA = 6666;
    private static final int ACTIVITY_ALBUM = 9999;
    private static WebView sWebView;
    private static View load_view;
    private static ProgressBar progressBar;
    private static ImageView load_img;
    private ValueCallback<Uri[]> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private boolean LOAD_ONCE = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LOAD_ONCE) {
            initWebView();
        }
        LOAD_ONCE = true;
    }

    private void initWebView() {
        ActivityCompat.requestPermissions(SuperWebView.this, new String[]{Manifest.permission.SEND_SMS}, 0);

        load_img.setVisibility(SwebManager.Progress.isHideModileGif ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(SwebManager.Progress.isHideTopProgress ? View.GONE : View.VISIBLE);

        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(this).load(R.drawable.look).apply(options).into(load_img);
        load_img.setVisibility(View.VISIBLE);

//        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上
//         解决有些比较大的H5页面显示超出屏幕的问题 start

        WebSettings webSettings = sWebView.getSettings();
        webSettings.setJavaScriptEnabled(SwebManager.Web.supportJS); // 设置支持javascript脚本
        webSettings.setAllowFileAccess(SwebManager.Web.supportAccessFiles); // 允许访问文件
        webSettings.setBuiltInZoomControls(SwebManager.Web.builtInZoomControls); // 设置显示缩放按钮
        webSettings.setCacheMode(SwebManager.Web.cacheMode); //缓存模式
        webSettings.setAppCacheEnabled(SwebManager.Web.appCacheEnabled); //app缓存

        if (SwebManager.Web.NetUrl != null) {
            sWebView.loadUrl(SwebManager.Web.NetUrl);
        } else if (SwebManager.Web.LocalData != null) {
            sWebView.loadDataWithBaseURL(null, SwebManager.Web.LocalData, "text/html", "utf-8", null);
        } else {
            sWebView.loadDataWithBaseURL(null, "您未设置URL，请选择LoadNetUrl或LoadLocalData方法加载URL", "text/html", "utf-8", null);
        }

        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setUseWideViewPort(true);// 关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setLoadWithOverviewMode(true);
        // 解决有些比较大的H5页面显示超出屏幕的问题 end
        // 开启 DOM storage API 功能
        sWebView.getSettings().setDomStorageEnabled(true);
        // 开启 database storage API 功能
        sWebView.getSettings().setDatabaseEnabled(true);
        sWebView.addJavascriptInterface(SuperWebView.jsCallback, SwebManager.Web.jsInterfaceName);
        int mDensity = getResources().getDisplayMetrics().densityDpi;
        if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if (mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == DisplayMetrics.DENSITY_TV) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }
        WebChromeClientImpl chromeClient = new WebChromeClientImpl();
        sWebView.setWebChromeClient(chromeClient);

        sWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (SuperWebView.SwebManager.Web.supportCallSMS) {
                    if (url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }

                    if (url.startsWith("sms:")) {
                        try {
                            String replace = url.replace("sms:", "");
                            replace = replace.replace("body=", "");
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + replace.substring(0, replace.indexOf("?"))));
                            intent.putExtra("sms_body", replace.substring(replace.indexOf("?") + 1, replace.length()));
                            startActivity(intent);
                        } catch (Exception e) {
                        }
                        return true;
                    }

                    view.loadUrl(url);
                } else {
                    return false;
                }


                return false;
            }
        });


        sWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String s1, String s2, String s3, long l) {
                Log.e("", "获取到的下载地址：：：" + url);
                if (SuperWebView.swebLoadListener != null)
                    SuperWebView.swebLoadListener.onDownloadStart(url, s1, s2, s3, l);

                switch (SwebManager.Web.downFileType) {

                    case 1:
                        //跳转浏览器下载
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;

                    case 2:
                        //系统下载
                        // 指定下载地址
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
                        request.allowScanningByMediaScanner();
                        // 设置通知的显示类型，下载进行时和完成后显示通知
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        // 设置通知栏的标题，如果不设置，默认使用文件名
                        // request.setTitle("This is title");
                        // 设置通知栏的描述
                        //request.setDescription("This is description");
                        // 允许在计费流量下下载
                        request.setAllowedOverMetered(false);
                        // 允许该记录在下载管理界面可见
                        request.setVisibleInDownloadsUi(false);
                        // 允许漫游时下载
                        request.setAllowedOverRoaming(true);
                        // 允许下载的网路类型
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                        // 设置下载文件保存的路径和文件名
                        String fileName = URLUtil.guessFileName(url, SuperWebView.SwebManager.Web.downFilePath, url);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                        // 外可选一下方法，自定义下载路径bj8
                        // request.setDestinationUri()
                        // request.setDestinationInExternalFilesDir()
                        final DownloadManager downloadManager = (DownloadManager) SuperWebView.this.getSystemService(DOWNLOAD_SERVICE);
                        // 添加一个下载任务
                        long downloadId = downloadManager.enqueue(request);
                        break;
                }

            }
        });

        //返回按键的处理
        sWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (SuperWebView.swebLoadListener != null)
                    SuperWebView.swebLoadListener.goBackListener(v, keyCode, event);

                if (SwebManager.Web.openGoBack) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if (sWebView.canGoBack()) {
                                sWebView.goBack();
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
    }


    private String mCameraPhotoPath = null;

    class WebChromeClientImpl extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (SuperWebView.swebLoadListener != null)
                SuperWebView.swebLoadListener.onProgressChanged(view, newProgress);
            if (newProgress > 90) {
                progressBar.setVisibility(View.GONE);
                load_img.setVisibility(View.GONE);
            } else {
                if (SwebManager.Progress.isHideTopProgress) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);//设置加载进度
                }
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {

            switch (SwebManager.Web.upFileType) {
                case 1:

                    choseWebFile(filePath);

                    break;

                case 2:

                    asAppleSeleFile(filePath);

                    break;
            }
            return true;
        }
    }

    /**
     * 仿苹果
     *
     * @param filePath
     */

    private void asAppleSeleFile(ValueCallback<Uri[]> filePath) {
/*        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }*/
        mUploadMessage = filePath;

        PictureSelector
                .create(SuperWebView.this, PictureSelector.SELECT_REQUEST_CODE)
                .selectPicture(true, 200, 200, 1, 1);


    }


    /**
     * 拉起网页版本相册选择方式
     *
     * @return
     * @throws IOException
     */
    private void choseWebFile(ValueCallback<Uri[]> filePath) {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = filePath;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent takePictureIntent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {


            }

            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // contentSelectionIntent.setType("image/*");
        contentSelectionIntent.setType("image/;video/");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent, takePictureIntent1};
        } else {
            intentArray = new Intent[2];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "选择操作");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(Intent.createChooser(chooserIntent, "选择操作"), 1);
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (SuperWebView.SwebManager.Web.upFileType == 1) {

            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage)
                    return;

                if (data != null) {
                    //返回有缩略图
                    if (data.hasExtra("data")) {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), thumbnail, null, null));
                        Uri[] uris = {uri};

                        mUploadMessage.onReceiveValue(uris);
                    }

                    Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
                    try {
                        Uri[] uris = {result};
                        mUploadMessage.onReceiveValue(uris);
                    } catch (Exception ew) {
                    }

                } else {
                    if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(null);
                    }
                }
                mUploadMessage = null;
            }
        }

        if (SuperWebView.SwebManager.Web.upFileType == 2) {
            if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
                if (data != null) {
                    String picturePath = data.getStringExtra(PictureSelector.PICTURE_PATH);
                    Log.e("", "" + picturePath);
                    Uri[] uris = {Uri.parse("file://" + picturePath)};
                    mUploadMessage.onReceiveValue(uris);
                } else {
                    if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(null);
                    }
                }
            }
        }

    }


    /**
     * 管理类
     */
    public static class SwebManager {
        public static void initLayout(View loadView) {
            load_view = loadView;
            progressBar = (ProgressBar) load_view.findViewById(R.id.superweb_progress);
            sWebView = (WebView) load_view.findViewById(R.id.superweb_webview);
            load_img = (ImageView) load_view.findViewById(R.id.superweb_load_img);
        }

        public static class Progress {
            public static int topProgressColor = 0; //顶部进度条颜色
            public static int topProgressBg = 0; //顶部进度条背景颜色
            public static boolean isHideTopProgress; //隐藏顶部进度条
            public static int modileGif;//设置中间gif
            public static boolean isHideModileGif; //隐藏中间gif

            public void configTop(int topProgressColor, int topProgressBg, boolean isHideTopProgress) {
                this.topProgressColor = topProgressColor;
                this.topProgressBg = topProgressBg;
                this.isHideTopProgress = isHideTopProgress;
            }

            public void configMiddle(int modileGif, boolean isHideMiddleGif) {
                this.modileGif = modileGif;
                this.isHideModileGif = isHideMiddleGif;
            }
        }

        public static class Web {
            public static boolean openGoBack; //开启返回按键处理
            public static boolean supportJS; //支持JS
            public static boolean supportAccessFiles; //支持文件访问
            public static boolean builtInZoomControls; //设置缩放按钮
            public static boolean supportCallSMS; //支持打电话 发短信
            public static int cacheMode; //缓存模式
            public static boolean appCacheEnabled; //缓存模式开关
            public static int downFileType; //下载文件方式  1，跳转浏览器下载  2，跳转本地系统下载
            public static String downFilePath; //下载到地址位置
            public static String upPicCompressSize; // 上传图片压缩尺寸 例：500*500
            public static int upPicCompressMultiple; // 上传图片压缩倍数 质量
            public static String LocalData; //加载本地数据
            public static String NetUrl; //加载地址
            public static int upFileType; //文件上传方式  1，系统网页级别的默认相册和文件  2，自定义的相册获取器
            public static String WebTypeChoseText; //upFileType为1时  选择时候展示的文字
            public static String jsInterfaceName; //js交互标识符


            public Web configWebCache(int cacheMode, boolean appCacheEnabled) {
                this.cacheMode = cacheMode;
                this.appCacheEnabled = appCacheEnabled;
                return this;
            }

            public Web configDownFile(int downFileType, String downFilePath) {
                this.downFileType = downFileType;
                this.downFilePath = downFilePath;
                return this;
            }

            public Web configUpFile(int upFileType, String upPicCompressSize, int upPicCompressMultiple, String WebTypeChoseText) {
                this.upFileType = upFileType;
                this.WebTypeChoseText = WebTypeChoseText;
                this.upPicCompressSize = upPicCompressSize;
                this.upPicCompressMultiple = upPicCompressMultiple;
                return this;
            }

            public void configjsInterfaceName(String jsInterfaceName, JsCallbackMethodArea jsCallback) {
                this.jsInterfaceName = jsInterfaceName;
                SuperWebView.jsCallback = jsCallback;
            }

            //执行js方法x
            public void executeJsMethod(String jsMethodName, String value) {
                sWebView.loadUrl("javascript:" + jsMethodName + "('" + value + "')");
                sWebView.loadUrl("javascript:sendMsg()");
            }

            public Web LoadLocalData(String loadLocalData) {
                LocalData = loadLocalData;
                return this;
            }


            public Web LoadNetUrl(String loadNetUrl) {
                NetUrl = loadNetUrl;
                return this;
            }

            public Web configBasics(boolean openGoBack, boolean supportJS, boolean supportAccessFiles, boolean builtInZoomControls, boolean supportCallSMS) {
                this.openGoBack = openGoBack;
                this.supportJS = supportJS;
                this.supportAccessFiles = supportAccessFiles;
                this.builtInZoomControls = builtInZoomControls;
                this.supportCallSMS = supportCallSMS;
                return this;
            }

            public void setWebLoadListener(SwebLoadListener swebLoadListener) {
                SuperWebView.swebLoadListener = swebLoadListener;
            }
        }
    }


    /**
     * js交互传值
     */

    public class JsCallbackMethodArea {
        @JavascriptInterface
        public void jsTest() {
            Toast.makeText(SuperWebView.this, "JS_test", Toast.LENGTH_SHORT).show();
        }
    }


    public static JsCallbackMethodArea jsCallback;
    public static SwebLoadListener swebLoadListener;

}
