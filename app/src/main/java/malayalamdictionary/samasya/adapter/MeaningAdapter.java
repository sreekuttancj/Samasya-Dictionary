package malayalamdictionary.samasya.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import malayalamdictionary.samasya.R;


public class MeaningAdapter extends ArrayAdapter {

    private final Context context;
    private final String[] values;
    boolean englishToMalayalam;
    TextView textView;
    ClipboardManager clipboard;

    public MeaningAdapter(Context context, String[] values, boolean englishToMalayalam) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
        this.englishToMalayalam = englishToMalayalam;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rowlayout, parent, false);
        }
        textView = (TextView) convertView.findViewById(R.id.label);
        final Typeface tf = Typeface.createFromAsset(this.context.getResources().getAssets(), "fonts/mal.ttf");

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (englishToMalayalam) {
                      Toast.makeText(getContext(),"Long press to copy not available", Toast.LENGTH_SHORT).show();

                }
                else {

                ClipData clip = ClipData.newPlainText("item", values[position]);

                Toast.makeText(getContext(),String.valueOf(values[position]).toLowerCase()+" copied to clip board", Toast.LENGTH_SHORT).show();
                clipboard.setPrimaryClip(clip);

                }

                return true;
            }
        });
        if (englishToMalayalam) {
            textView.setTypeface(tf);
            textView.setText(this.values[position]);
        }
        else {

            textView.setText(this.values[position].toLowerCase());
        }

        try {
            textView.setTextColor(Color.parseColor("#000000"));

            String s = this.values[position];

            return convertView;
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

}


