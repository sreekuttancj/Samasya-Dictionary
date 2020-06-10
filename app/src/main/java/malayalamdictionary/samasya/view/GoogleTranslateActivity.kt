package malayalamdictionary.samasya.view

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import malayalamdictionary.samasya.R

class GoogleTranslateActivity : AppCompatActivity() {

    val URL = "https://translate.google.co.in/?hl=en"
    private lateinit var browser: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_translaet)

        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        window.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON)
        browser = findViewById(R.id.webview)
        mToolbar.title = "Samasya"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mToolbar.setNavigationOnClickListener(View.OnClickListener { finish() })
        val activity = this
        val progressDialog = ProgressDialog(activity)


        browser.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                progressDialog.show()
                progressDialog.setTitle("Loading...")
                progressDialog.progress = 0
                activity.setProgress(progress * 1000)
                progressDialog.incrementProgressBy(progress)
                if (progress == 100 && progressDialog.isShowing)
                    progressDialog.dismiss()

            }
        }

        browser.webViewClient = MyBrowser()

        browser.settings.loadsImagesAutomatically = true
        browser.settings.javaScriptEnabled = true
        browser.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        browser.loadUrl(URL)
    }

    private inner class MyBrowser : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

    }
}