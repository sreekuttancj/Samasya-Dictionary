package malayalamdictionary.samasya.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import malayalamdictionary.samasya.view.FavouriteEnglishFragment
import malayalamdictionary.samasya.view.FavouriteMalayalamFragment

class FavouritePagerAdapter(fm: FragmentManager, var numOfTabs: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> {
                FavouriteEnglishFragment()
            }
            1 -> {
                FavouriteMalayalamFragment()
            }
            else -> null
        }
    }

    override fun getCount(): Int {
        return numOfTabs
    }
}
