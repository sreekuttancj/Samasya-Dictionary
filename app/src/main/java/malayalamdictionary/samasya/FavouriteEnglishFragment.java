package malayalamdictionary.samasya;

import android.app.AlertDialog;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import malayalamdictionary.samasya.adapter.FavouriteAdapter;
import malayalamdictionary.samasya.database.DatabaseHelper;
import malayalamdictionary.samasya.helper.FavouriteItem;

public class FavouriteEnglishFragment extends Fragment {

    List<FavouriteItem> favouriteItems;
    ArrayList<String> meaningArray;

    FavouriteAdapter listAdapter;
    ExpandableListView expListView;
    List<FavouriteItem> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Boolean selectAll=true;
    boolean actionModeEnabled;
    MenuItem menuItem;
    String copyText;
    ProgressDialog progress;
    FavouriteTask favouriteTask;
    public boolean expanded=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rowView;
        rowView = inflater.inflate(R.layout.fragment_favourite, container, false);


        expListView = (ExpandableListView)rowView.findViewById(R.id.lvExp_favourite);
        listDataHeader = new ArrayList<FavouriteItem>();
        listDataChild = new HashMap<String, List<String>>();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        expListView.setIndicatorBounds(width-GetDipsFromPixel(35), width-GetDipsFromPixel(5));

        progress = new ProgressDialog(getActivity());
        favouriteTask=new FavouriteTask();
        favouriteTask.execute();


        expListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                if(!expandableListView.isGroupExpanded(i))
                {
                    expanded=true;
                }
                else {
                    expanded=false;
                }

                    if(actionModeEnabled)
                {

                    expandableListView.setItemChecked(i, !expandableListView.isItemChecked(i));

                }



                    return actionModeEnabled;
            }
        });


        expListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = expListView.getCheckedItemCount();
                mode.setTitle(String.valueOf(checkedCount));
                listAdapter.toggleSelection(position);
                final SparseBooleanArray selected = listAdapter.getSelectedIds();

                if (checkedCount>1){
                    menuItem.setVisible(false);
                }
                else {

                    if (selected.valueAt(0)){
                        copyText= String.valueOf(listAdapter.getGroupItem(selected.keyAt(0)));

                    }
                    menuItem.setVisible(true);

                }

                if (checkedCount==listAdapter.getGroupCount()){
                    selectAll=false;

                }
                else {
                    selectAll=true;

                }

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.favourite_menu, menu);
//                toolbar.setVisibility(View.GONE);
                menuItem=menu.getItem(0);
                actionModeEnabled = true;

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.copy:

                        if (!expanded) {
                            String label = "copy";
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(label, copyText);
                            clipboard.setPrimaryClip(clip);
                            mode.finish();

                            Toast.makeText(getActivity(), copyText + " copied to clipboard", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(),"Please close expanded words before selection",Toast.LENGTH_SHORT).show();

                        }
                        return true;

                    case R.id.select_all:
                        if (selectAll) {
                            for (int i = 0; i < expListView.getAdapter().getCount(); i++) {
                                expListView.setItemChecked(i, true);
                                listAdapter.selectView(i, true);
                            }
                            selectAll=false;

                        }

                        else{
                            for ( int i=0; i < expListView.getAdapter().getCount(); i++) {

                                expListView.setItemChecked(i, false);
                                listAdapter.selectView(i, false);
                            }
                            selectAll=true;
                        }

                        return true;

                    case R.id.delete:

                        final SparseBooleanArray selected = listAdapter.getSelectedIds();
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setMessage("Are you sure,You wanted to delete this item");
                        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {


//                        // Calls getSelectedIds method from ListViewAdapter Class

//                        // Captures all selected ids with a loop
                                for (int i = (selected.size() - 1); i >= 0; i--) {

                                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                                    if (!expanded) {
                                        if (selected.valueAt(i)) {
                                            try {
                                                FavouriteItem favouriteItem = (FavouriteItem) listAdapter.getGroup(selected.keyAt(i));

                                                // Re/move selected items following the ids

                                                listAdapter.remove(favouriteItem);


                                                Cursor cursor = databaseHelper.getWritableDatabase().rawQuery("delete From samasya_eng_favourite where ENG like '" + favouriteItem.getName() + "'", null);
                                                cursor.moveToFirst();
                                                while (!cursor.isAfterLast()) {
                                                    cursor.moveToNext();
                                                }
                                                cursor.close();
                                            } catch (Exception sqle) {
                                                throw sqle;
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(getActivity(),"Please close expanded words before selection",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        });

                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }


            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mode.finish();

                listAdapter.removeSelection();
                actionModeEnabled = false;
//                toolbar.setVisibility(View.VISIBLE);

            }
        });


        return rowView;

    }


    private void prepareListData() {

        getDataFromDb();


        for (int i=0;i<favouriteItems.size();i++){
            listDataHeader.add(favouriteItems.get(i));
            List<String> meaning;
            meaning= getMeaningFromDb(listDataHeader.get(i).getName());
            listDataChild.put(listDataHeader.get(i).getName(), meaning);
        }


    }



    public void getDataFromDb(){

        DatabaseHelper myDbHelper2 = new DatabaseHelper(getActivity());
        try {
            myDbHelper2.open();
            Cursor cursor = myDbHelper2.getReadableDatabase().rawQuery("Select DISTINCT ENG From samasya_eng_favourite order by ENG asc", null);
            favouriteItems = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                favouriteItems.add(new FavouriteItem(cursor.getString(0)));
                cursor.moveToNext();
            }
            cursor.close();
            myDbHelper2.close();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    public ArrayList<String> getMeaningFromDb(String word){

        DatabaseHelper myDbHelper2 = new DatabaseHelper(getActivity());
        try {
            myDbHelper2.open();
            Cursor cursor = myDbHelper2.getReadableDatabase().rawQuery("Select * From samasya_eng_mal where ENG like '"+word+"'", null);
            meaningArray = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                meaningArray.add(cursor.getString(1));
                cursor.moveToNext();
            }
            cursor.close();
            myDbHelper2.close();
        } catch (SQLException sqle) {
            throw sqle;
        }

        return meaningArray;
    }

    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private class FavouriteTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            prepareListData();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Loading...");
            progress.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            listAdapter = new FavouriteAdapter(getActivity(), listDataHeader, listDataChild,true);
            expListView.setAdapter(listAdapter);

        }
    }



}
