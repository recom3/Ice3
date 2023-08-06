package com.recom3.snow3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.recom3.snow3.mobilesdk.UrlHelper;
import com.recom3.snow3.mobilesdk.engageweb.AuthenticationService;

public class BrowserReconLoginActivity extends AppCompatActivity {

  public static final String TAG = BrowserReconLoginActivity.class.getSimpleName();
  //private ProgressDialogBuilder mProgressDialog;

  private WebView mWebview;
  
  private WebChromeClient webChromeClient = new WebChromeClient() {
      public void onProgressChanged(WebView param1WebView, int param1Int) {
        BrowserReconLoginActivity.this.setProgress(param1Int * 100);
        //if (param1Int == 100)
          //BrowserReconLoginActivity.this.mProgressDialog.dismiss();
      }
  };


  private WebViewClient webViewClient = new WebViewClient() {
      public void onPageStarted(WebView param1WebView, String param1String, Bitmap param1Bitmap) {
        //BrowserReconLoginActivity.this.mProgressDialog.show();
      }
      
      public void onReceivedSslError(WebView param1WebView, SslErrorHandler param1SslErrorHandler, SslError param1SslError) {
        param1SslErrorHandler.proceed();
      }
      
      public boolean shouldOverrideUrlLoading(WebView param1WebView, String param1String) {
        Intent intent;
        boolean bool1 = false;
        if (param1WebView.getHitTestResult() != null && !param1String.contains("error") && param1String.contains("code")) {
          if (param1WebView.getHitTestResult().getType() > 0)
            return bool1;

          intent = new Intent();
          intent.putExtra("code", UrlHelper.getCode(param1String));
          BrowserReconLoginActivity.this.setResult(-1, intent);
          BrowserReconLoginActivity.this.finish();
          return true;
        } 
        boolean bool2 = bool1;
        if (param1WebView.getHitTestResult() != null) {
          bool2 = bool1;
          if (param1String.contains("error")) {
            BrowserReconLoginActivity.this.setResult(0);
            BrowserReconLoginActivity.this.finish();
            bool2 = bool1;
          } 
        } 
        return bool2;
      }
    };

  private void loadReconUrl() {
    try {
      String url = AuthenticationService.getOauth2Url(MainActivityTest.RECON_CLIENT_ID, MainActivityTest.RECON_CLIENT_SECRET, MainActivityTest.RECON_REDIRECT_URI);
      url = "https://www.recom3.com/api/oauth2";
      this.mWebview.loadUrl(url);
    } catch (Exception ex)
    {
      Log.e(TAG, ex.getMessage());
    }
  }

  @SuppressLint({"SetJavaScriptEnabled"})
  protected void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);
    getWindow().requestFeature(2);
    getWindow().setFeatureInt(2, -1);

    //setTitle(2131230746);
    setContentView(R.layout.login_signup_browser_layout);

    this.mWebview = (WebView) findViewById(R.id.browser_recon_login_layout_webview);

    //this.mProgressDialog = (new ProgressDialogBuilder((Context)this)).setMessage(2131230760);
    if(this.mWebview!=null) {
      this.mWebview.getSettings().setJavaScriptEnabled(true);

      //this.mWebview.setWebChromeClient(this.webChromeClient);
      this.mWebview.setWebViewClient(this.webViewClient);

      boolean bLoadRecon = true;
      if(bLoadRecon) {
        loadReconUrl();
      }
    }
  }
  
  protected void onResume() {
    super.onResume();
    CookieManager.getInstance().removeAllCookie();
  }

}