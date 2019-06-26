package malayalamdictionary.samasya.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import malayalamdictionary.samasya.HistoryEnglishFragment
import malayalamdictionary.samasya.HistoryMalayalamFragment

class HistoryPagerAdapter(fm: FragmentManager , val mNumOfTabs: Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        return when(position){
            0 -> {
                HistoryEnglishFragment()
            }
            1 -> {
                HistoryMalayalamFragment()
            }
            else -> null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}