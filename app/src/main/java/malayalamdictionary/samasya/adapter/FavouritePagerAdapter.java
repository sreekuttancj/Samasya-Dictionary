package malayalamdictionary.samasya.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import malayalamdictionary.samasya.FavouriteEnglishFragment;
import malayalamdictionary.samasya.FavouriteMalayalamFragment;


public class FavouritePagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public FavouritePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FavouriteEnglishFragment tab1 = new FavouriteEnglishFragment();
                return tab1;
            case 1:
                FavouriteMalayalamFragment tab2 = new FavouriteMalayalamFragment();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
