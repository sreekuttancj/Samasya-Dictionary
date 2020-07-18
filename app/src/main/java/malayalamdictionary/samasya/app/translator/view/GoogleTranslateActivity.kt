package malayalamdictionary.samasya.app.translator.view

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_google_translaet.*
import malayalamdictionary.samasya.R

class GoogleTranslateActivity : AppCompatActivity() {

    private val URL = "https://translate.google.co.in/?hl=en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_translaet)

        window.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON)
        toolbar.title = "Samasya"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }
        val activity = this
        val progressDialog = ProgressDialog(activity)

        with(webview){
            webChromeClient = object : WebChromeClient() {
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
            webViewClient = MyBrowser()
            settings.loadsImagesAutomatically = true
            settings.javaScriptEnabled = true
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            loadUrl(URL)
        }
    }

    private inner class MyBrowser : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}