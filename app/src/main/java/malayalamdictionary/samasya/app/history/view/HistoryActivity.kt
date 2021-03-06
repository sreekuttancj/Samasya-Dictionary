package malayalamdictionary.samasya.app.history.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.toolbar.*
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.app.history.view.adapter.HistoryPagerAdapter

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        with(toolbar){
            setSupportActionBar(this)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.setNavigationOnClickListener { finish() }
            this.title = getString(R.string.history)
        }

        with(tab_layout){
            addTab(this.newTab().setText("English - Malayalam"))
            addTab(this.newTab().setText("Malayalam - English"))
            tabGravity = TabLayout.GRAVITY_FILL
        }

        val adapter = HistoryPagerAdapter(supportFragmentManager, tab_layout.tabCount)
        pager.adapter = adapter
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                pager.currentItem = tab.position

                when (tab.position) {

                    0 -> pager.currentItem = 0
                    1 -> pager.currentItem = 1

                    else ->
                        pager.currentItem = tab.position
                }
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
        })
    }
}