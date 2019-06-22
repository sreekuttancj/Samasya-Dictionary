package malayalamdictionary.samasya.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import malayalamdictionary.samasya.HistoryEnglishFragment;
import malayalamdictionary.samasya.HistoryMalalayalmFragment;

public class HistoryPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public HistoryPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                HistoryEnglishFragment tab1 = new HistoryEnglishFragment();
                return tab1;
            case 1:
                HistoryMalalayalmFragment tab2 = new HistoryMalalayalmFragment();
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
