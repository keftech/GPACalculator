package kef.technology.jumianet;

import static kef.technology.jumianet.MainActivity.ORDER_NUM_KEY;
import static kef.technology.jumianet.MainActivity.getKolLink;
import static kef.technology.jumianet.MainActivity.isValidUrl;
import static kef.technology.jumianet.MainActivity.kolID;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

public class MyWebClient extends WebViewClient {

    private boolean isLoadSuccess;
    private final MainActivity activity;

    public MyWebClient(MainActivity activity){
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String  url = request.getUrl().toString();
        if(isValidUrl(url))
            view.loadUrl(getUrl(url));
        return true;
    }

    private String getUrl(String url){
        if (url.contains("/checkout/multistep/finish") && !url.contains(kolID)) {
            isLoadSuccess = false;
            return getKolLink(url.replace("#", ""));
        }
        else if (!isLoadSuccess && url.contains("/checkout/success") && activity != null) {
            isLoadSuccess = true;
            int orderNum = activity.prefs.getInt(ORDER_NUM_KEY, 0) + 1;
            activity.showReview(orderNum);
            activity.prefs.edit().putInt(ORDER_NUM_KEY, orderNum).apply();
        }
        return url;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(isValidUrl(url))
            view.loadUrl(getUrl(url));
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if(activity != null && view.getUrl().equals(request.getUrl().toString())){
           activity.webViewModel.getShowError().setValue(true);
           activity.webViewModel.getErrorMesaage().setValue(error.getDescription().toString());
        }
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if(activity != null && view.getUrl().equals(failingUrl)){
            activity.webViewModel.getShowError().setValue(true);
            activity.webViewModel.getErrorMesaage().setValue(description);
        }
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if(activity != null)
            activity.webViewModel.getShowError().setValue(false);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }
}
