package malayalamdictionary.samasya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class GoogleTranslateActivity extends AppCompatActivity {

    public static final String URL="https://translate.google.co.in/?hl=en";
    WebView browser;
    Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_google_translaet);

        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        browser = (WebView) findViewById(R.id.webview);
        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Samasya");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Activity activity = this;
        final ProgressDialog progressDialog = new ProgressDialog(activity);

        browser.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                progressDialog.show();
                progressDialog.setTitle("Loading...");
                progressDialog.setProgress(0);
                activity.setProgress(progress * 1000);
                progressDialog.incrementProgressBy(progress);
                if(progress == 100 && progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        });

        browser.setWebViewClient(new MyBrowser());

        browser.getSettings().setLoadsImagesAutomatically(true);
            browser.getSettings().setJavaScriptEnabled(true);
            browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            browser.loadUrl(URL);




    }

    private class MyBrowser extends WebViewClient {

        private ProgressBar progressBar;

//        public MyBrowser(ProgressBar progressBar) {
//            this.progressBar=progressBar;
//            progressBar.setVisibility(View.VISIBLE);
//        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            // TODO Auto-generated method stub
//            super.onPageFinished(view, url);
//            progressBar.setVisibility(View.GONE);
//        }
//
    }

}
