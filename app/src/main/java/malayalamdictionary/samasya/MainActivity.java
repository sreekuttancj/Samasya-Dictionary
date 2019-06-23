package malayalamdictionary.samasya;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.annotation.IdRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kobakei.ratethisapp.RateThisApp;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import malayalamdictionary.samasya.adapter.ListItemAdapter;
import malayalamdictionary.samasya.adapter.MeaningAdapter;
import malayalamdictionary.samasya.database.DatabaseHelper;
import malayalamdictionary.samasya.helper.ConnectionDetector;
import malayalamdictionary.samasya.helper.FlipAnimation;
import malayalamdictionary.samasya.helper.OnSwipeTouchListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    AutoCompleteTextView autoCompleteTextView;
    ImageButton imageButtonMic;
    ImageButton imageButtonClear;
    CardView cardViewList;
    CardView cardViewListBack;
    ListView listViewSuggestion;
    ListView listViewSuggestionMalayalam;
    ListItemAdapter listItemAdapter;
    CardView cardViewListMeaning;
    CardView cardViewListMeaningBack;
    ListView listViewMeaning;
    ListView listViewMeaningMalayalam;
    MeaningAdapter meaningAdapter;
    TextToSpeech textToSpeech;
    TextView textViewHint;
    public static boolean englishToMayalayam=true;

    protected static final int REQ_CODE_SPEECH_INPUT = 1;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private BottomBar mBottomBar;
    private CoordinatorLayout coordinatorLayout;

//    NativeExpressAdView adView;
    String fromFav="null";
    String fromHis="null";

    FloatingActionButton floatingActionButton;
    private Typeface type;
    private Typeface typeButton;
    SharedPreferences pref;
    private FirebaseAnalytics mFirebaseAnalytics;


    public static RelativeLayout relayoutFirstPage;
    public static RelativeLayout relayoutSecondPage;
    RelativeLayout relativeLayoutTopbar;
    String[] symLetters;
    String[] consLetters;
    String[] vowLetters;
    String[] chilluLetters;
    boolean isShowKeyboard;
    int charIndex;
    String text;
    int cursorPossition;
    int isConsonants;
    int isChillu;
    String typedWord;
    boolean longPressed;

    ImageButton backspace;
    ImageButton buttonSpace;
    ImageButton imageButtonSearch;
    RelativeLayout relativeLayoutFeedback;
    TextView textViewFeedbackWord;
    Button buttonFeedback;
    Button buttonGoogleTranslator;
    boolean doubleBackToExitPressedOnce = false;
    Boolean isInternetPresent = false;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RateThisApp.Config config = new RateThisApp.Config(3, 5);
        RateThisApp.init(config);

              FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("English");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        this.type = Typeface.createFromAsset(getAssets(), "fonts/mal.ttf");
        typeButton=Typeface.createFromAsset(getAssets(), "fonts/mlwttkarthika.ttf");
        this.isShowKeyboard = false;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.three_buttons_activity);
        final ConnectionDetector connectionDetector=new ConnectionDetector(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences(AdblockActivity.MyPREFERENCES, MODE_PRIVATE);

//        Log.d("admob_id",getString(R.string.ad_unit_id));
//        adView = (NativeExpressAdView)findViewById(R.id.adView_native);
//        if (pref.getInt(AdblockActivity.COUNT,5)>0) {
//
//            adView.loadAd(new AdRequest.Builder().build());
//        }

        relayoutFirstPage=(RelativeLayout) findViewById(R.id.re_first);
        relayoutSecondPage= (RelativeLayout) findViewById(R.id.re_second);
        floatingActionButton= (FloatingActionButton) findViewById(R.id.fab_swip);
        relativeLayoutTopbar= (RelativeLayout) findViewById(R.id.top_bar);
        relativeLayoutFeedback= (RelativeLayout) findViewById(R.id.relayout_feedback);
        textViewFeedbackWord= (TextView) findViewById(R.id.textView_word);
        buttonFeedback= (Button) findViewById(R.id.buttonFeedBack);
        buttonGoogleTranslator= (Button) findViewById(R.id.button_google_translate);

        buttonGoogleTranslator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isInternetPresent=connectionDetector.isConnectingToInternet();

                if (isInternetPresent) {
                    String copyText = autoCompleteTextView.getText().toString().trim();
                    String label = "copy";
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(label, copyText);
                    clipboard.setPrimaryClip(clip);

                    if (englishToMayalayam){
                        Toast.makeText(getApplicationContext(), "Word copied to clipboard Long press to paste it into translator", Toast.LENGTH_LONG).show();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Copy word from search box and paste it into translator", Toast.LENGTH_LONG).show();

                    }

                    Intent intentTranslate = new Intent(MainActivity.this, GoogleTranslateActivity.class);
                    startActivity(intentTranslate);
                }
                else {
                    Snackbar.make(relativeLayoutFeedback,"No network connection",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        databaseHelper=new DatabaseHelper(this);
        buttonFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isInternetPresent = connectionDetector.isConnectingToInternet();

                if (isInternetPresent) {
                    hideKey();
                    hideMalayalam(R.id.keyBoardLayout);
                    if (englishToMayalayam) {
                        myRef.child(textViewFeedbackWord.getText().toString().trim()).setValue(textViewFeedbackWord.getText().toString().trim());
                    }
                    else {
//                        myRef1.child(textViewFeedbackWord.getText().toString().trim()).setValue(textViewFeedbackWord.getText().toString().trim());

                    }
                    Toast.makeText(getApplicationContext(),"Thank you for your suggestion, we will update it soon",Toast.LENGTH_LONG).show();
//                    Snackbar.make(drawerLayout,"Thank you for your suggestion, we will update it soon", Snackbar.LENGTH_LONG).show();

                    relativeLayoutFeedback.setVisibility(View.GONE);
                    cardViewList.setVisibility(View.GONE);
                    cardViewListBack.setVisibility(View.GONE);
                    cardViewListMeaning.setVisibility(View.GONE);
                    cardViewListMeaningBack.setVisibility(View.GONE);
                    autoCompleteTextView.setText("");

                }
                else {
                    hideKey();
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_LONG).show();

//                    Snackbar.make(drawerLayout,"No internet connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        imageButtonSearch= (ImageButton) findViewById(R.id.search_word);
        imageButtonSearch.setOnClickListener(this);
        relayoutFirstPage.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                flipCard();
            }

            public void onSwipeLeft() {
                flipCard();
            }

            public void onSwipeBottom() {
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        relayoutSecondPage.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                flipCard();
            }
            public void onSwipeLeft() {
                flipCard();
            }
            public void onSwipeBottom() {
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


        mBottomBar= BottomBar.attach(this, savedInstanceState);

        mBottomBar.setItemsFromMenu(R.menu.menu_bottom, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {

                }
//            }
        });


        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
               switch (menuItemId) {
                   case R.id.favorite_item:
                        updateFavourite();
                        break;

                    case R.id.text_to_speech:
                        if (englishToMayalayam) {
                            speak();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Pronunciation is not available for malayalam words",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.home:
                        break;

               }

            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.favorite_item:
                        updateFavourite();
                        break;

                    case R.id.text_to_speech:
                        if (englishToMayalayam) {
                            speak();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Pronunciation is not available for malayalam words",Toast.LENGTH_SHORT).show();
                        }                        break;
                    case R.id.home:
                        break;

                }

            }
        });


                backspace = (ImageButton) findViewById(R.id.backspace);
                buttonSpace= (ImageButton) findViewById(R.id.buttonSpace);

        buttonSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.dispatchKeyEvent(new KeyEvent(0,62));
            }
        });


        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoCompleteTextView.length() > 0) {
                    cursorPossition = autoCompleteTextView.getSelectionStart();
                    if (cursorPossition != 0) {
                        String str1 = autoCompleteTextView.getText().subSequence(0, cursorPossition - 1).toString();
                        autoCompleteTextView.setText(new StringBuilder(String.valueOf(str1)).append(autoCompleteTextView.getText().subSequence(cursorPossition, autoCompleteTextView.length()).toString()).toString());
                        autoCompleteTextView.setSelection(cursorPossition - 1);


                    }


                }
            }
        });



        Button toggleButton = (Button) findViewById(R.id.toggleButton);
                toggleButton.setTypeface(type);


                toggleButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        toggleButton();
                    }
                });


                Button chilluButton = (Button) findViewById(R.id.chilluButton);
                chilluButton.setTypeface(type);
                chilluButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        toggleChilluButton();
                    }
                });


                ((Button) findViewById(R.id.moreToSymbol2)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        hide(R.id.fixedRow2);
                        show(R.id.fixedRow3);
                    }
                });


                ((Button) findViewById(R.id.backToSymbol1)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        show(R.id.fixedRow2);
                        hide(R.id.fixedRow3);
                    }
                });


                ((Button) findViewById(R.id.moreTo2)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        hide(R.id.vowRow1);
                        hide(R.id.vowRow2);
                        hide(R.id.vowRow3);
                        show(R.id.vowRow4);
                        show(R.id.vowRow5);
                        show(R.id.vowRow6);
                    }
                });


                ((Button) findViewById(R.id.moreToCons)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        hide(R.id.consRow1);
                        hide(R.id.consRow2);
                        hide(R.id.consRow3);
                        show(R.id.consRow4);
                        show(R.id.consRow5);
                        show(R.id.consRow6);
                    }
                });

                ((Button) findViewById(R.id.moreToCons2)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        hide(R.id.consRow4);
                        hide(R.id.consRow5);
                        hide(R.id.consRow6);
                        show(R.id.consRow7);
                        show(R.id.consRow8);
                        show(R.id.consRow9);
                    }
                });
                ((Button) findViewById(R.id.backToCons)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        hide(R.id.consRow4);
                        hide(R.id.consRow5);
                        hide(R.id.consRow6);
                        show(R.id.consRow1);
                        show(R.id.consRow2);
                        show(R.id.consRow3);
                    }
                });
                ((Button) findViewById(R.id.backToCons2)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        hide(R.id.consRow7);
                        hide(R.id.consRow8);
                        hide(R.id.consRow9);
                        show(R.id.consRow4);
                        show(R.id.consRow5);
                        show(R.id.consRow6);
                    }
                });
                ((Button) findViewById(R.id.backTo1)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        hide(R.id.vowRow4);
                        hide(R.id.vowRow5);
                        hide(R.id.vowRow6);
                        show(R.id.vowRow1);
                        show(R.id.vowRow2);
                        show(R.id.vowRow3);
                    }
                });



        mBottomBar.setActiveTabColor("#3F51B5");


        this.symLetters = new String[]{"m", "n", "o", "p", "q", "s", "t", "v", "y", "r", "u", "z", "{", "w", "x", "."};
        this.consLetters = new String[]{"\u00f1", "\u00a1", "\u00f3", "\u00a7", "\u00af", "\u00e2", "\u00e4", "\u00bd",
                "\u00a8", "\u00ef", "\u00c5", "\u00ab", "\u00b8", "\u00bc", "\u00ae", "\u00b4", "\u00bf", "\u00f4",
                "\u00aa", "\u00b5", "\u00a3", "\u00d6", "\u00d1", "\u00da", "\u00d4", "\u00e3", "\u00d0", "\u00d5",
                "\u00de", "\u00e0", "\u00a6", "\u00d8", "\u00dd", "\u00df", "\u00b2", "\u00b0", "\u00b1", "\u00c8",
                "\u00ca", "\u00ba", "\u00c6", "\u00cd", "\u00a4", "\u00a2", "\u00a5", "\u00b9", "\u00be", "\u00c7",
                "\u00bb", "\u00c9", "\u00ce", "\u00cf", "\u00d7", "\u00d9", "\u00db", "\u00d3", "\u00e1", "\u00d2", "\u00dc"};
        this.vowLetters = new String[]{"A", "\\", "h", "a", "b", "k", "I", "c", "X", "S", "]", "e", "W", "d", "F", "C", "i", "N",
                "G", "H", "B", "R", "j", "D", "f", "Z", "K", "l", "g", "_", "U", "^", "P", "M", "`", "[", "L", "J", "Y", "V", "O", "T", "Q", "E"};
        this.chilluLetters = new String[]{"\u00c0", "\u00f0", "\u00b3", "\u00c4", "\u00ac"};
        this.isConsonants = 0;
        this.isChillu = 0;
        this.typedWord = "";
        this.longPressed = false;



        rebuildDatabase();
        setViews();


    }




    private void toggleChilluButton() {
        Button togChilluButton = (Button) findViewById(R.id.chilluButton);
        if (this.isChillu == 0) {
            hideAll();
            show(R.id.chilluRow);
            this.isChillu = 1;
            togChilluButton.setText("m");
        } else {
            hideAll();
            show(R.id.fixedRow2);
            this.isChillu = 0;
            togChilluButton.setText("\u00c0");
        }
        show(R.id.vowRow1);
        show(R.id.vowRow2);
        show(R.id.vowRow3);
        Button togButton = (Button) findViewById(R.id.toggleButton);
        this.isConsonants = 0;
        togButton.setText("\u00a1");
    }

    private void hide(int id) {
        findViewById(id).setVisibility(View.GONE);
    }

    private void hideAll() {
        hide(R.id.fixedRow2);
        hide(R.id.fixedRow3);
        hide(R.id.vowRow1);
        hide(R.id.vowRow2);
        hide(R.id.vowRow3);
        hide(R.id.vowRow4);
        hide(R.id.vowRow5);
        hide(R.id.vowRow6);
        hide(R.id.consRow1);
        hide(R.id.consRow2);
        hide(R.id.consRow3);
        hide(R.id.consRow4);
        hide(R.id.consRow5);
        hide(R.id.consRow6);
        hide(R.id.consRow7);
        hide(R.id.consRow8);
        hide(R.id.consRow9);
        hide(R.id.chilluRow);
        hide(R.id.numSymRow1);
        hide(R.id.numSymRow2);
        hide(R.id.numSymRow3);
        hide(R.id.numSymRow4);
    }


    private void flipCard()
    {
        View rootLayout = (View ) findViewById(R.id.drawer_layout);
        View cardFace = (View) findViewById(R.id.re_first);
        View cardBack = (View) findViewById(R.id.re_second);


        FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

        if (cardFace.getVisibility() == View.GONE)
        {
            autoCompleteTextView.setTypeface(Typeface.DEFAULT);
            autoCompleteTextView.setText("");
            autoCompleteTextView.setVisibility(View.GONE);
            flipAnimation.reverse();
            englishToMayalayam=true;
            textViewHint.setVisibility(View.VISIBLE);
            imageButtonMic.setVisibility(View.VISIBLE);
            imageButtonClear.setVisibility(View.GONE);
            imageButtonSearch.setVisibility(View.GONE);
            hideKey();

            hideMalayalam(R.id.keyBoardLayout);
            textViewHint.setText("English - Malayalam");
            floatingActionButton.setImageResource(R.drawable.e);

        }
        else {
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/mlwttkarthika.ttf");
            autoCompleteTextView.setTypeface(font);
            autoCompleteTextView.setText("");
            englishToMayalayam=false;
            textViewHint.setVisibility(View.VISIBLE);
            autoCompleteTextView.setVisibility(View.GONE);
            imageButtonClear.setVisibility(View.GONE);
            imageButtonSearch.setVisibility(View.VISIBLE);
            hideMalayalam(R.id.keyBoardLayout);
            hideKey();

            imageButtonMic.setVisibility(View.GONE);
            textViewHint.setText("Malayalam - English");
            floatingActionButton.setImageResource(R.drawable.mala);


        }



        rootLayout.startAnimation(flipAnimation);
    }

    public void onCardClick(View view)
    {

        flipCard();
    }
    public void setViews(){



        initNavigationDrawer();
        cardViewList= (CardView) findViewById(R.id.card_view_list);
        cardViewListBack= (CardView) findViewById(R.id.card_view_list_back);
        listViewSuggestion= (ListView) findViewById(R.id.list_view_suggestion);
        listViewSuggestionMalayalam= (ListView) findViewById(R.id.list_view_suggestion_back);
        cardViewListMeaning= (CardView) findViewById(R.id.card_view_list_meaning);
        cardViewListMeaningBack= (CardView) findViewById(R.id.card_view_list_meaning_back);

        listViewMeaning = (ListView) findViewById(R.id.list_view_meaning);
        listViewMeaningMalayalam= (ListView) findViewById(R.id.list_view_meaning_back);
        autoCompleteTextView= (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
//        autoCompleteTextView.setTypeface(type);
        textViewHint= (TextView) findViewById(R.id.textViewHint);
        imageButtonMic= (ImageButton) findViewById(R.id.imageButtonMic);
        imageButtonClear= (ImageButton) findViewById(R.id.imageButton_close);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });



        setSymbolButtons();
        setConsonantButtons();
        setVowelButtons();
        setChilluButtons();

        autoCompleteTextView.setOnClickListener(this);
        imageButtonMic.setOnClickListener(this);
        imageButtonClear.setOnClickListener(this);
        textViewHint.setOnClickListener(this);
        Intent intent=getIntent();
        if (intent.getStringExtra("fav")!=null){
            fromFav=intent.getStringExtra("fav");
            fillData(fromFav);

        }
        if (intent.getStringExtra("his")!=null){
            fromHis=intent.getStringExtra("his");
            fillData(fromHis);
        }

        textChange();

    }

    public void rateApp(){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(Uri.parse("https://goo.gl/TltKno"));
        if (!MyStartActivity(intent)) {
            //Market (Google play) app seems not installed, let's try to open a webbrowser
            intent.setData(Uri.parse("https://goo.gl/TltKno"));
            if (!MyStartActivity(intent)) {
                //Well if this also fails, we have run out of options, inform the user.
                Toast.makeText(MainActivity.this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public  boolean MyStartActivity(Intent aIntent) {
        try
        {

            startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }


   public void initNavigationDrawer(){
       navigationView = (NavigationView)findViewById(R.id.navigation_view);
       navigationView.setItemIconTintList(null);
       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(MenuItem item) {
               int id = item.getItemId();
               switch (id){
                   case R.id.home:
                       finish();
                       Intent intentHome=new Intent(MainActivity.this,MainActivity.class);
                       startActivity(intentHome);
                       drawerLayout.closeDrawers();
                       break;
                   case R.id.favourite:

                       Intent intentFavourite=new Intent(MainActivity.this,FavouriteActivity.class);
                       startActivity(intentFavourite);
                       drawerLayout.closeDrawers();

                       break;
                   case R.id.history:
                       Intent intentHistory=new Intent(MainActivity.this,HistoryActivity.class);
                       startActivity(intentHistory);
                       drawerLayout.closeDrawers();
                       drawerLayout.closeDrawers();


                       break;
                   case R.id.block_add:

                       Intent intentAdBlock=new Intent(MainActivity.this,AdblockActivity.class);
                       startActivity(intentAdBlock);
                       drawerLayout.closeDrawers();

                       break;
                   case R.id.google_translate:
                       Intent intentTranslate=new Intent(MainActivity.this, GoogleTranslateActivity.class);
                       startActivity(intentTranslate);
                       drawerLayout.closeDrawers();

                       break;

                   case R.id.rate:
                       rateApp();
                       drawerLayout.closeDrawers();

                       break;
                   case R.id.share:
                       Intent sendIntent = new Intent();
                       sendIntent.setAction(Intent.ACTION_SEND);
                       sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Samasya - English Malayalam Dictionary :https://goo.gl/TltKno");
                       sendIntent.setType("text/*");
                       startActivity(sendIntent);
                       break;
                   case R.id.about:
                       Intent intentAbout=new Intent(MainActivity.this,AboutUsActivity.class);
                       startActivity(intentAbout);
                       drawerLayout.closeDrawers();

                       break;
               }
               return true;
           }
       });

       drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
       actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

           @Override
           public void onDrawerClosed(View drawerView) {
               super.onDrawerClosed(drawerView);
               mBottomBar.show();
           }

           @Override
           public void onDrawerOpened(View drawerView) {
               super.onDrawerOpened(drawerView);
               hideKey();
               mBottomBar.hide();
           }
       };
       drawerLayout.addDrawerListener(actionBarDrawerToggle);
       actionBarDrawerToggle.syncState();

   }


    public void rebuildDatabase(){


        try {
            // check if database exists in app path, if not copy it from assets
            databaseHelper.createDb();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
    }




    public void textChange(){
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (englishToMayalayam){
                    autoCompleteTextView.setTextSize(18);
                }
                else {
                    autoCompleteTextView.setTextSize(20);

                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



                if(s.length()>1) {

                    if (englishToMayalayam) {
                        getWords();
                        if (listViewSuggestion.getAdapter().getCount()==0){
                            cardViewList.setVisibility(View.GONE);
                            textViewFeedbackWord.setTypeface(Typeface.DEFAULT);
                            textViewFeedbackWord.setText(autoCompleteTextView.getText().toString().trim());
                            relativeLayoutFeedback.setVisibility(View.VISIBLE);
                        }
                        else {


                            relativeLayoutFeedback.setVisibility(View.GONE);

                        }
                        imageButtonSearch.setVisibility(View.GONE);
                        mBottomBar.hide();
//                        adView.setVisibility(View.GONE);
                    }
                    else {

                        getWordsMalayalam();
                        imageButtonSearch.setVisibility(View.VISIBLE);
                        if (listViewSuggestionMalayalam.getAdapter().getCount()==0){
                            cardViewListBack.setVisibility(View.GONE);
                            textViewFeedbackWord.setTypeface(type);
                            textViewFeedbackWord.setText(autoCompleteTextView.getText().toString().trim());
                            relativeLayoutFeedback.setVisibility(View.VISIBLE);
                        }
                        else {

                            relativeLayoutFeedback.setVisibility(View.GONE);
                        }
                    }


                    imageButtonClear.setVisibility(View.VISIBLE);
                    imageButtonMic.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                }

                else {

                    floatingActionButton.setVisibility(View.VISIBLE);
//                    adView.setVisibility(View.GONE );
                    if (englishToMayalayam) {
                        imageButtonMic.setVisibility(View.VISIBLE);
                    }
                    imageButtonClear.setVisibility(View.GONE);
                    cardViewList.setVisibility(View.GONE);
                    cardViewListMeaningBack.setVisibility(View.GONE);
                    cardViewListMeaning.setVisibility(View.GONE);
                    mBottomBar.hide();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void getWords() {

        DatabaseHelper myDbHelper2 = new DatabaseHelper(this);

        try {

            myDbHelper2.open();
            Cursor c2 = myDbHelper2.getReadableDatabase().rawQuery("Select distinct ENG From samasya_suggestion where ENG like  '" + autoCompleteTextView.getText().toString().trim().replace("''", "").replace("'", "''") + "%'  LIMIT 20", null);
            ArrayList<String> strings = new ArrayList();
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                strings.add(c2.getString(0).toLowerCase());
                c2.moveToNext();
            }

            c2.close();
            listItemAdapter=new ListItemAdapter(getApplicationContext(),strings,this,cardViewList);
            if (listItemAdapter.getCount()!=0){
                cardViewList.setVisibility(View.VISIBLE);
                cardViewListMeaning.setVisibility(View.GONE);
            }
            listViewSuggestion.setVisibility(View.VISIBLE);
            listViewSuggestion.setAdapter(listItemAdapter);

            databaseHelper.close();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }


    public void getWordsMalayalam() {
        DatabaseHelper myDbHelper2 = new DatabaseHelper(this);
        try {

            myDbHelper2.open();
            Cursor c2 = myDbHelper2.getReadableDatabase().rawQuery("Select distinct MAL From samasya_eng_mal where MAL GLOB  '" + autoCompleteTextView.getText().toString().trim().replace("''", "").replace("'", "''") + "*'  LIMIT 20", null);
            ArrayList<String> strings = new ArrayList();
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                strings.add(c2.getString(0));
                c2.moveToNext();
            }

            c2.close();
            listItemAdapter = new ListItemAdapter(getApplicationContext(), strings, this, cardViewListMeaningBack);
            if (listItemAdapter.getCount() != 0) {
                cardViewListBack.setVisibility(View.VISIBLE);
                cardViewListMeaningBack.setVisibility(View.GONE);
            }
            listViewSuggestionMalayalam.setVisibility(View.VISIBLE);
            listViewSuggestionMalayalam.setAdapter(listItemAdapter);
            databaseHelper.close();
        } catch (SQLException sqle) {
            throw sqle;
        }

    }


    public void setText(String text){
        autoCompleteTextView.setText(text);
    }

    private void historyUpdate() {
        DatabaseHelper myDbHelper2 = new DatabaseHelper(this);
        String bk_txt =autoCompleteTextView.getText().toString().trim();
        try {
            myDbHelper2.open();
            try {
                if (bk_txt.length() > REQ_CODE_SPEECH_INPUT) {
                    Cursor cursor = null;

                    if (englishToMayalayam) {

                        cursor = myDbHelper2.getWritableDatabase().rawQuery("INSERT INTO samasya_eng_history (ENG) values('" + bk_txt.replace("'", "''") + "')", null);
                    }
                    else {
                        cursor = myDbHelper2.getWritableDatabase().rawQuery("INSERT INTO samasya_mal_history (MAL) values('" + bk_txt.replace("'", "''") + "')", null);

                    }
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                myDbHelper2.close();
            } catch (SQLException sqle) {

                throw sqle;
            }
        } catch (SQLException sqle2) {

            throw sqle2;
        }
    }


    public void fillData(String text) {

        if (text!=null) {
            imageButtonClear.setVisibility(View.VISIBLE);
            autoCompleteTextView.setVisibility(View.VISIBLE);
            textViewHint.setVisibility(View.GONE);
            autoCompleteTextView.setText(text);
            if (!englishToMayalayam){
                autoCompleteTextView.setTypeface(type);
            }
            else {
                autoCompleteTextView.setTypeface(Typeface.DEFAULT);

            }
            autoCompleteTextView.setSelection(autoCompleteTextView.getText().length());
        }
        imageButtonMic.setVisibility(View.GONE);
        imageButtonSearch.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);

        hideKey();
        DatabaseHelper myDbHelper1 = new DatabaseHelper(this);
        try {
            myDbHelper1.open();
            Cursor c1 = null;

            if (englishToMayalayam) {
                historyUpdate();

                c1 = myDbHelper1.getReadableDatabase().rawQuery("Select * From samasya_eng_mal where ENG like '" + autoCompleteTextView.getText().toString().trim().replace("''", "").replace("'", "''") + "'", null);

            }
            else {
                historyUpdate();

                c1 = myDbHelper1.getReadableDatabase().rawQuery("Select * From samasya_eng_mal where MAL like '" + autoCompleteTextView.getText().toString().trim().replace("''", "").replace("'", "''") + "'", null);

            }

                ArrayList strings = new ArrayList();
            c1.moveToFirst();
            if (englishToMayalayam) {
                while (!c1.isAfterLast()) {
                    strings.add(c1.getString(1).replace("\u00ad", "\u00ef"));


                    c1.moveToNext();
                }
            }
            else {
                while (!c1.isAfterLast()) {
                    strings.add(c1.getString(0).replace("\u00ad", "\u00ef"));

                    c1.moveToNext();
                }

            }

            String[] mString = (String[]) strings.toArray(new String[strings.size()]);

            if (mString.length == 0) {
                textViewFeedbackWord.setTypeface(typeButton);
                textViewFeedbackWord.setText(autoCompleteTextView.getText().toString().trim());
                relativeLayoutFeedback.setVisibility(View.VISIBLE);

            }

            meaningAdapter = new MeaningAdapter(this, mString,englishToMayalayam);
            if (meaningAdapter.getCount()!=0){
                cardViewListMeaning.setVisibility(View.VISIBLE);
                cardViewListMeaningBack.setVisibility(View.VISIBLE);
                cardViewList.setVisibility(View.GONE);
                cardViewListBack.setVisibility(View.GONE);
            }
            listViewMeaning.setVisibility(View.VISIBLE);
//            adView.setVisibility(View.GONE);
            if (englishToMayalayam) {
                mBottomBar.show();
                listViewMeaning.setAdapter(meaningAdapter);
            }
            else {
                mBottomBar.show();
                hideMalayalam(R.id.keyBoardLayout);
                listViewMeaningMalayalam.setAdapter(meaningAdapter);

            }
            myDbHelper1.close();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }



    private void hideKey() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((autoCompleteTextView.getWindowToken()), 0);
    }




    public void speak(){

        hideKey();
        if (!autoCompleteTextView.getText().toString().equals("")) {
            textToSpeech.setLanguage(Locale.getDefault());
            textToSpeech.isLanguageAvailable(Locale.getDefault());
            textToSpeech.speak(autoCompleteTextView.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
        }
        else {
            Toast.makeText(getApplicationContext(),"Please choose a word",Toast.LENGTH_SHORT).show();
        }

    }

    public void speakToText(){

        Intent intentSpeak = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentSpeak.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentSpeak.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intentSpeak.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intentSpeak, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT :
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    autoCompleteTextView.setText(result.get(0));
                    autoCompleteTextView.setSelection(autoCompleteTextView.getText().length());
                    imageButtonMic.setVisibility(View.GONE);
                    textViewHint.setVisibility(View.GONE);
                }

                break;
            default:
        }
    }

    public void updateFavourite(){


        if (!autoCompleteTextView.getText().toString().trim().equals("")) {

            DatabaseHelper databaseHelper = new DatabaseHelper(getApplication());
            String favourite = autoCompleteTextView.getText().toString().trim();
            try {
                databaseHelper.open();
                try {
                    if (favourite.length() > REQ_CODE_SPEECH_INPUT) {


                        Cursor c444 = null;
                        if (englishToMayalayam){
                            c444 = databaseHelper.getWritableDatabase().rawQuery("INSERT INTO samasya_eng_favourite (ENG) values('" + favourite.replace("'", "''") + "')", null);
                            Toast.makeText(this, favourite.concat(" added to favourites"), Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(this, " Word added to favourites", Toast.LENGTH_SHORT).show();

                            c444 = databaseHelper.getWritableDatabase().rawQuery("INSERT INTO samasya_mal_favourite (MAL) values('" + favourite.replace("'", "''") + "')", null);

                        }

                        c444.moveToFirst();
                        while (!c444.isAfterLast()) {
                            c444.moveToNext();
                        }
                        c444.close();
                    }
                    databaseHelper.close();
                } catch (SQLException sqle) {
                    throw sqle;
                }
            } catch (SQLException sqle2) {
                throw sqle2;
            }

        }
        else {
            Toast.makeText(getApplicationContext(),"Please choose a word",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.autoCompleteTextView) {

//            adView.setVisibility(View.GONE);
            mBottomBar.hide();
            if (!englishToMayalayam){
                showKeyboard();
                hideKey();
                relativeLayoutFeedback.setVisibility(View.GONE);

            }
            else {
                relativeLayoutFeedback.setVisibility(View.GONE);
                hideMalayalam(R.id.keyBoardLayout);
                showKey();

            }
        }

        if (v.getId()==R.id.imageButtonMic){
            hideKey();
            imageButtonMic.setVisibility(View.VISIBLE);
            cardViewListMeaning.setVisibility(View.GONE);
            speakToText();
        }

        if (v.getId()==R.id.imageButton_close){

            autoCompleteTextView.setText("");
            cardViewList.setVisibility(View.GONE);
            cardViewListMeaning.setVisibility(View.GONE);
            cardViewListBack.setVisibility(View.GONE);
            cardViewListMeaningBack.setVisibility(View.GONE);
            if (pref.getInt(AdblockActivity.COUNT,5)>0) {

//                adView.setVisibility(View.VISIBLE);
            }
            relativeLayoutFeedback.setVisibility(View.GONE);

        }

        if (v.getId()==R.id.textViewHint){

            textViewHint.setVisibility(View.GONE);
//            adView.setVisibility(View.GONE);
            autoCompleteTextView.setVisibility(View.VISIBLE);
            autoCompleteTextView.requestFocus();
            mBottomBar.hide();
            if (englishToMayalayam) {
                showKey();

            }
            else {
                mBottomBar.hide();
                showKeyboard();
            }
        }

        if (v.getId()==R.id.search_word){
            hideMalayalam(R.id.keyBoardLayout);
            fillData(autoCompleteTextView.getText().toString().trim());
        }

    }


    public void showKey(){
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(autoCompleteTextView, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fromFav="null";
    }


    private void toggleButton() {
        Button togButton = (Button) findViewById(R.id.toggleButton);
        if (this.isConsonants == 0) {
            hideAll();
            show(R.id.consRow1);
            show(R.id.consRow2);
            show(R.id.consRow3);
            this.isConsonants = 1;
            togButton.setText("A");
        } else {
            hideAll();
            show(R.id.vowRow1);
            show(R.id.vowRow2);
            show(R.id.vowRow3);
            this.isConsonants = 0;
            togButton.setText("\u00a1");
        }
        show(R.id.fixedRow2);
        Button togChilluButton = (Button) findViewById(R.id.chilluButton);
        this.isChillu = 0;
        togChilluButton.setText("\u00c0");
    }


    private void setVowelButtons() {
        int[] BUTTON_IDS = new int[]{R.id.vowButton1, R.id.vowButton2, R.id.vowButton3, R.id.vowButton4, R.id.vowButton5, R.id.vowButton6, R.id.vowButton7, R.id.vowButton8, R.id.vowButton9, R.id.vowButton10, R.id.vowButton11, R.id.vowButton12, R.id.vowButton13, R.id.vowButton14, R.id.vowButton15, R.id.vowButton16, R.id.vowButton17, R.id.vowButton18, R.id.vowButton19, R.id.vowButton20, R.id.vowButton21, R.id.vowButton22, R.id.vowButton23, R.id.vowButton24, R.id.vowButton25, R.id.vowButton26, R.id.vowButton27, R.id.vowButton28, R.id.vowButton29, R.id.vowButton30, R.id.vowButton31, R.id.vowButton32, R.id.vowButton33, R.id.vowButton34, R.id.vowButton35, R.id.vowButton36, R.id.vowButton37, R.id.vowButton38, R.id.vowButton39, R.id.vowButton40, R.id.vowButton41, R.id.vowButton42, R.id.vowButton43, R.id.vowButton44};
        int i = 0;
        int length = BUTTON_IDS.length;
        for (int i2 = 0; i2 < length; i2 += 1) {
            Button theButton = (Button) findViewById(BUTTON_IDS[i2]);
            theButton.setTextAppearance(getApplicationContext(), R.style.buttonStyle);
            theButton.setBackgroundResource(R.drawable.button_style);
            theButton.setTypeface(typeButton);
            theButton.setTextSize(20.0f);
            theButton.setText(this.vowLetters[i]);
            theButton.setOnClickListener(new AnonymousClass21(theButton));
            i += 1;
        }
    }

    private void setChilluButtons() {
        int[] BUTTON_IDS = new int[]{R.id.chilluButton1, R.id.chilluButton2, R.id.chilluButton3, R.id.chilluButton4, R.id.chilluButton5};
        int i = 0;
        int length = BUTTON_IDS.length;
        for (int i2 = 0; i2 < length; i2 += 1) {
            Button theButton = (Button) findViewById(BUTTON_IDS[i2]);
            theButton.setTextAppearance(getApplicationContext(), R.style.buttonStyle);
            theButton.setBackgroundResource(R.drawable.button_style);
            theButton.setTypeface(typeButton);
            theButton.setTextSize(20.0f);
            theButton.setText(this.chilluLetters[i]);
            theButton.setOnClickListener(new AnonymousClass24(theButton));
            i += 1;
        }
    }


    private void setConsonantButtons() {
        int[] CONS_BUTTON_IDS = new int[]{R.id.consButton1, R.id.consButton2, R.id.consButton3, R.id.consButton4,
                R.id.consButton5, R.id.consButton6, R.id.consButton7, R.id.consButton8, R.id.consButton9, R.id.consButton10,
                R.id.consButton11, R.id.consButton12, R.id.consButton13, R.id.consButton14, R.id.consButton15,
                R.id.consButton16, R.id.consButton17, R.id.consButton18, R.id.consButton19, R.id.consButton20,
                R.id.consButton21, R.id.consButton22, R.id.consButton23, R.id.consButton24, R.id.consButton25,
                R.id.consButton26, R.id.consButton27, R.id.consButton28, R.id.consButton29, R.id.consButton30,
                R.id.consButton31, R.id.consButton32, R.id.consButton33, R.id.consButton34, R.id.consButton35,
                R.id.consButton36, R.id.consButton37, R.id.consButton38, R.id.consButton39, R.id.consButton40,
                R.id.consButton41, R.id.consButton42, R.id.consButton43, R.id.consButton44, R.id.consButton45,
                R.id.consButton46, R.id.consButton47, R.id.consButton48, R.id.consButton49, R.id.consButton50,
                R.id.consButton51, R.id.consButton52, R.id.consButton53, R.id.consButton54, R.id.consButton55,
                R.id.consButton56, R.id.consButton57, R.id.consButton58, R.id.consButton59};
        int i = 0;
        int length = CONS_BUTTON_IDS.length;
        for (int i2 = 0; i2 < length; i2 += 1) {
            Button theButton = (Button) findViewById(CONS_BUTTON_IDS[i2]);
            theButton.setTextAppearance(getApplicationContext(), R.style.buttonStyle);
            theButton.setBackgroundResource(R.drawable.button_style);
            theButton.setTypeface(typeButton);
            theButton.setTextSize(20.0f);
            theButton.setText(this.consLetters[i]);
            theButton.setOnClickListener(new AnonymousClass22(theButton));
            i += 1;
        }
    }



    private void setSymbolButtons() {
        int[] SYM_BUTTON_IDS = new int[]{R.id.symButton1, R.id.symButton2, R.id.symButton3, R.id.symButton4, R.id.symButton5,
                R.id.symButton6, R.id.symButton7, R.id.symButton8, R.id.symButton9, R.id.symButton10, R.id.symButton11,
                R.id.symButton12, R.id.symButton13, R.id.symButton14, R.id.symButton15};
        int i = 0;
        int length = SYM_BUTTON_IDS.length;
        for (int i2 = 0; i2 < length; i2 += 1) {
            Button theButton = (Button) findViewById(SYM_BUTTON_IDS[i2]);
            theButton.setTextAppearance(MainActivity.this, R.style.buttonStyle);
            theButton.setBackgroundResource(R.drawable.button_style);
            theButton.setTypeface(typeButton);
            theButton.setTextSize(20.0f);
            theButton.setText(this.symLetters[i]);
            theButton.setOnClickListener(new AnonymousClass23(theButton));
            i += 1;
        }
    }

    private void showKeyboard() {
        if (!this.isShowKeyboard) {
            show(R.id.keyBoardLayout);
            //            if (this.lvHeight == 0) {
//                this.lvHeight = this.listWords.getHeight();
//            }
//            LayoutParams params = this.listWords.getLayoutParams();
//            params.height = this.lvHeight - ((int) ((208.0f * getResources().getDisplayMetrics().density) + 0.5f));
//            this.listWords.setLayoutParams(params);
//            this.isShowKeyboard = true;
        }
    }
    public void show(int id){
        findViewById(id).setVisibility(View.VISIBLE);

    }
    public void hideMalayalam(int id){
        findViewById(id).setVisibility(View.GONE);

    }


    class AnonymousClass24 implements View.OnClickListener {

        Button button;
        AnonymousClass24(Button button) {
            this.button = button;
        }


        @Override
        public void onClick(View v) {

            try{
                charIndex = autoCompleteTextView.getSelectionStart();
                text = autoCompleteTextView.getText().toString();

                if (charIndex >= 0) {
                    autoCompleteTextView.setText(new StringBuilder(String.valueOf((String) text.subSequence(0, charIndex))).append(this.button.getText()).append(text.subSequence(charIndex, text.length())).toString());
                    if (autoCompleteTextView.getSelectionStart() != autoCompleteTextView.length()) {
                        autoCompleteTextView.setSelection(charIndex + 1);
                    }
                } else {
                    autoCompleteTextView.setText(Html.fromHtml(new StringBuilder(String.valueOf(autoCompleteTextView.getText().toString())).append(this.button.getText()).toString()));
                    autoCompleteTextView.setSelection(autoCompleteTextView.length());
                }
                cursorPossition += 1;
            } catch (Exception e) {
            }
        }

    }

    class AnonymousClass22 implements View.OnClickListener {
        Button button;
        AnonymousClass22(Button button) {
            this.button = button;
        }

        @Override
        public void onClick(View v) {

            try {
                charIndex = autoCompleteTextView.getSelectionStart();
                text = autoCompleteTextView.getText().toString();
                if (charIndex >= 0) {
                    autoCompleteTextView.setText(new StringBuilder(String.valueOf((String) text.subSequence(0, charIndex))).append(this.button.getText()).append(text.subSequence(charIndex, text.length())).toString());
                    if (autoCompleteTextView.getSelectionStart() != autoCompleteTextView.length()) {
                        autoCompleteTextView.setSelection(charIndex + 1);
                    }
                } else {
                    autoCompleteTextView.setText(Html.fromHtml(new StringBuilder(String.valueOf(autoCompleteTextView.getText().toString())).append(this.button.getText()).toString()));
                    autoCompleteTextView.setSelection(autoCompleteTextView.length());
                }
                cursorPossition += 1;
                typedWord += this.button.getText();
            } catch (Exception e) {
            }
        }

    }

    class AnonymousClass21 implements View.OnClickListener {
        Button button;


        AnonymousClass21(Button button) {
            this.button = button;
        }

        @Override
        public void onClick(View v) {
            try {
                charIndex = autoCompleteTextView.getSelectionStart();
                text = autoCompleteTextView.getText().toString();
                if (charIndex >= 0) {
                    autoCompleteTextView.setText(new StringBuilder(String.valueOf((String) text.subSequence(0, charIndex))).append(button.getText()).append(text.subSequence(charIndex, text.length())).toString());
                    if (autoCompleteTextView.getSelectionStart() != autoCompleteTextView.length()) {
                        autoCompleteTextView.setSelection(charIndex + 1);
                    }
                } else {
                    autoCompleteTextView.setText(Html.fromHtml(new StringBuilder(String.valueOf(autoCompleteTextView.getText().toString())).append(button.getText()).toString()));
                    autoCompleteTextView.setSelection(autoCompleteTextView.length());
                }
                cursorPossition += 1;
                typedWord += button.getText();
            } catch (Exception e) {
            }
        }

    }

    class AnonymousClass23 implements View.OnClickListener {
        Button button;
        AnonymousClass23(Button button) {
            this.button = button;
        }

        @Override
        public void onClick(View v) {
            try {

                charIndex = autoCompleteTextView.getSelectionStart();
                text = autoCompleteTextView.getText().toString();
                if (charIndex >= 0) {
                    autoCompleteTextView.setText(new StringBuilder(String.valueOf((String) text.subSequence(0, charIndex))).append(button.getText()).append(text.subSequence(charIndex, text.length())).toString());
                    if (autoCompleteTextView.getSelectionStart() != autoCompleteTextView.length()) {
                        autoCompleteTextView.setSelection(charIndex + 1);
                    }
                } else {
                    autoCompleteTextView.setText(Html.fromHtml(new StringBuilder(String.valueOf(autoCompleteTextView.getText().toString())).append(button.getText()).toString()));
                    autoCompleteTextView.setSelection(autoCompleteTextView.length());
                }
                cursorPossition += 1;
            } catch (Exception e) {
            }

        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {

            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(getApplicationContext(), "Touch again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        relativeLayoutFeedback.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        RateThisApp.onStart(this);
        RateThisApp.showRateDialogIfNeeded(this);

    }


}



