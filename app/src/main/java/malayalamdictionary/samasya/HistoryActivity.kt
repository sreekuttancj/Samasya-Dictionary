package malayalamdictionary.samasya

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import malayalamdictionary.samasya.adapter.HistoryPagerAdapter

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("History")
        toolbar.setTitleTextColor(Color.parseColor("#000000"))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText("English - Malayalam"))
        tabLayout.addTab(tabLayout.newTab().setText("Malayalam - English"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val viewPager: ViewPager = findViewById(R.id.pager)

        toolbar.setNavigationOnClickListener { finish() }

        val adapter = HistoryPagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                viewPager.currentItem = tab.position

                when (tab.position) {

                    0 -> viewPager.currentItem = 0
                    1 -> viewPager.currentItem = 1

                    else ->

                        viewPager.currentItem = tab.position
                }
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
        })
    }
}