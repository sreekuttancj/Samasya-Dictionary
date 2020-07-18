package malayalamdictionary.samasya.app.favorite.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import malayalamdictionary.samasya.app.favorite.view.FavouriteEnglishFragment
import malayalamdictionary.samasya.app.favorite.view.FavouriteMalayalamFragment

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
