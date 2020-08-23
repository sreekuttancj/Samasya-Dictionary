package malayalamdictionary.samasya.app.favorite.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_favourite.*
import kotlinx.android.synthetic.main.toolbar.*
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.app.favorite.view.adapter.FavouritePagerAdapter

class FavouriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        with(toolbar){
            setSupportActionBar(this)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.setNavigationOnClickListener { finish() }
            this.title = getString(R.string.favourite)
        }

        tab_layout.addTab(tab_layout.newTab().setText("English - Malayalam"))
        tab_layout.addTab(tab_layout.newTab().setText("Malayalam - English"))
        tab_layout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = FavouritePagerAdapter(supportFragmentManager, tab_layout.tabCount)
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

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }
}