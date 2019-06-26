package malayalamdictionary.samasya

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AboutUsActivity : AppCompatActivity() {

    private lateinit var mToolbar: Toolbar

    private lateinit var relativeLayoutLikeUs: RelativeLayout

    private val FACEBOOK_URL = "https://www.facebook.com/SamasyaDictionary/"
    private var FACEBOOK_PAGE_ID = "1618738521757689"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)


        mToolbar = findViewById<Toolbar>(R.id.toolbar)

        relativeLayoutLikeUs = findViewById(R.id.relayoutLikeUs)

        mToolbar.title = "About Us"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mToolbar.setNavigationOnClickListener { finish() }

        relativeLayoutLikeUs.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            val facebookUrl = getFacebookPageURL(this@AboutUsActivity)
            facebookIntent.data = Uri.parse(facebookUrl)
            startActivity(facebookIntent)
        }
    }

    private fun getFacebookPageURL(context: Context): String {
        val packageManager = context.packageManager
        return try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) { //newer versions of fb app
                "fb://facewebmodal/f?href=$FACEBOOK_URL"
            } else { //older versions of fb app
                "fb://page/$FACEBOOK_PAGE_ID"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            FACEBOOK_URL //normal web url
        }
    }
}