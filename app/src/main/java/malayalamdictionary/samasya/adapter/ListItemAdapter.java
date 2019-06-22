package malayalamdictionary.samasya.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import malayalamdictionary.samasya.MainActivity;
import malayalamdictionary.samasya.R;

public class ListItemAdapter extends BaseAdapter implements Filterable {

    Context mContext;
    ArrayList<String> items;
    public static TextView textViewAutoComplete;
    private List<String> filteredItems;
    private ItemFilter mFilter = new ItemFilter();
    MainActivity mainActivity;
    CardView cardViewSuggestion;


    public ListItemAdapter(Context context, ArrayList<String> items, MainActivity mainActivity, CardView cardViewSuggestion){

        mContext=context;
        this.items=items;
        this.filteredItems = items;
        this.mainActivity=mainActivity;
        this.cardViewSuggestion=cardViewSuggestion;
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        if (convertView==null){
            LayoutInflater layoutInflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.autocompletion_list,parent,false);
        }
        textViewAutoComplete= (TextView) convertView.findViewById(R.id.textView_autocomplete);

        Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/mal.ttf");
        if (MainActivity.englishToMayalayam){
            textViewAutoComplete.setTypeface(Typeface.DEFAULT);
        }
        else {
            textViewAutoComplete.setTypeface(type);
        }
//        textViewAutoComplete.setTypeface(type);
        String location = filteredItems.get(position);
        if (!location.isEmpty()) {

            textViewAutoComplete.setText(location);
        }
        else {

        }


            textViewAutoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainActivity.fillData(filteredItems.get(position));
            }
        });
        return convertView;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            int count = items.size();
            final List<String> tempItems = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                if (items.get(i).toLowerCase().contains(filterString)) {
                    tempItems.add(items.get(i));
                }
            }

            results.values = tempItems;
            results.count = tempItems.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }

    public Filter getFilter() {
        return mFilter;
    }

}
