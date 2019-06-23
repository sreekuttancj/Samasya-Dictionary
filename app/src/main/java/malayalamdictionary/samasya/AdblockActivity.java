package malayalamdictionary.samasya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import malayalamdictionary.samasya.helper.ConnectionDetector;

public class AdblockActivity extends AppCompatActivity {

    //todo remove add block activity class

    Toolbar mToolbar;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String COUNT = "countKey";

    int checkCount;
    Boolean isInternetPresent = false;
    ConnectionDetector connectionDetector;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    TextView textViewCount;
    Button buttonShare;
    static final int SHARE_REQUEST = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adblock);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        textViewCount= (TextView) findViewById(R.id.textView_count);
        buttonShare= (Button) findViewById(R.id.button_share);
        pref = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = pref.edit();
        connectionDetector=new ConnectionDetector(getApplicationContext());

        mToolbar.setTitle("AdBlock");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkCount=pref.getInt(COUNT,5);
        if (checkCount>0){
            textViewCount.setText(String.valueOf(checkCount));
        }
        else {
            buttonShare.setText("Ads Blocked");
            buttonShare.setEnabled(false);
            textViewCount.setVisibility(View.GONE);
        }

        final RelativeLayout relativeLayoutMain= (RelativeLayout) findViewById(R.id.relayout_main);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isInternetPresent = connectionDetector.isConnectingToInternet();

                if (pref.getInt(COUNT, 5) > 0) {

                    if (isInternetPresent) {



                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Samasya - English Malayalam Dictionary :https://goo.gl/TltKno");
                        sendIntent.setType("text/*");
                        sendIntent.setPackage("com.whatsapp");
                        startActivityForResult(sendIntent,SHARE_REQUEST);

                        checkCount--;

                        editor.putInt(COUNT, checkCount);
                        editor.commit();

                    } else {
                        Snackbar.make(relativeLayoutMain, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    buttonShare.setText("Ads Blocked");
                    textViewCount.setVisibility(View.GONE);
                    buttonShare.setEnabled(false);
                }
            }

        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==SHARE_REQUEST){


                        if (checkCount>=0) {
                            textViewCount.setText(String.valueOf(pref.getInt(COUNT, 5)));
                        }

        }
    }


}
