package malayalamdictionary.samasya.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import malayalamdictionary.samasya.R;
import malayalamdictionary.samasya.helper.FavouriteItem;


public class FavouriteAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<FavouriteItem> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private SparseBooleanArray mSelectedItemsIds;
    boolean englishMalayalam;

    public FavouriteAdapter(Context context, List<FavouriteItem> listDataHeader,
                                      HashMap<String, List<String>> listChildData,boolean englishMalayalam) {
        mContext = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        mSelectedItemsIds = new SparseBooleanArray();
        this.englishMalayalam=englishMalayalam;

    }


    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).getName()).size();
    }

    public Object getGroupItem(int groupPosition) {
        try {
            return this.listDataHeader.get(groupPosition).getName();
        }
        catch (Exception e){
            Toast.makeText(mContext,"Please close expanded words before selection",Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).getName()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        FavouriteItem favouriteItem= (FavouriteItem) getGroup(groupPosition);
        String headerTitle =  favouriteItem.getName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_listview_layout, null);

        }

        final Typeface tf = Typeface.createFromAsset(mContext.getResources().getAssets(), "fonts/mal.ttf");


        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.textView_item);
        if (englishMalayalam){
            lblListHeader.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        }
        else {
            lblListHeader.setTextSize(20);
            lblListHeader.setTypeface(tf,Typeface.BOLD);

        }
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_view_item, null);
        }

        final Typeface tf = Typeface.createFromAsset(mContext.getResources().getAssets(), "fonts/mal.ttf");

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.textView_list_item);
        if (englishMalayalam){
            txtListChild.setTypeface(tf);
            txtListChild.setText(childText);

        }
        else {
            txtListChild.setTypeface(Typeface.DEFAULT);
            txtListChild.setText(childText.toLowerCase());

        }

        return convertView;    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void remove(FavouriteItem object) {
        listDataHeader.remove(object);
        notifyDataSetChanged();
    }



    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
    public void toggleSelection(int position) {

        selectView(position, !mSelectedItemsIds.get(position));
    }

}
