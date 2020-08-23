package malayalamdictionary.samasya.app.adblocker.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import malayalamdictionary.samasya.app.MyApplication
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.app.helper.Common
import malayalamdictionary.samasya.app.helper.ConnectionDetector
import javax.inject.Inject

class AdblockActivity : AppCompatActivity()  {

    private var checkCount: Int = 0

    @Inject
    lateinit var preference: SharedPreferences

    @Inject
    lateinit var connectionDetector: ConnectionDetector

    private  lateinit var editor: SharedPreferences.Editor

    lateinit var textViewCount: TextView
    internal val SHARE_REQUEST = 1  // The request code

    override fun onCreate(savedInstanceState: Bundle?) {
        initDagger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adblock)

        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        textViewCount = findViewById(R.id.textView_count)
        val buttonShare = findViewById<Button>(R.id.button_share)
        editor = preference.edit()

        mToolbar.title = "AdBlock"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mToolbar.setNavigationOnClickListener { finish() }

        checkCount = preference.getInt(Common.COUNT, 5)
        if (checkCount > 0) {
            textViewCount.text = checkCount.toString()
        } else {
            buttonShare.text = resources.getString(R.string.ad_block)
            buttonShare.isEnabled = false
            textViewCount.visibility = View.GONE
        }

        val relativeLayoutMain = findViewById<RelativeLayout>(R.id.relayout_main)
        buttonShare.setOnClickListener {

            if (preference.getInt(Common.COUNT, 5) > 0) {

                if (connectionDetector.isConnectingToInternet()) {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Samasya - English Malayalam Dictionary :https://goo.gl/TltKno")
                    sendIntent.type = "text/*"
                    sendIntent.setPackage("com.whatsapp")
                    startActivityForResult(sendIntent, SHARE_REQUEST)

                    checkCount--

                    editor.putInt(Common.COUNT, checkCount)
                    editor.commit()

                } else {
                    Snackbar.make(relativeLayoutMain, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                buttonShare.text = resources.getString(R.string.ad_block)
                textViewCount.visibility = View.GONE
                buttonShare.isEnabled = false
            }
        }
    }

    private fun initDagger() {
        (applicationContext as MyApplication)
                .applicationComponent
                .inject(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == SHARE_REQUEST) {

            if (checkCount >= 0) {
                textViewCount.text = preference.getInt(Common.COUNT, 5).toString()
            }

        }
    }


}