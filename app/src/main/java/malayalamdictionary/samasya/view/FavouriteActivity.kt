package malayalamdictionary.samasya.view

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_favourite.*
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.adapter.FavouritePagerAdapter

class FavouriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

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