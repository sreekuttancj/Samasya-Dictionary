package malayalamdictionary.samasya.app.home.view

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kobakei.ratethisapp.RateThisApp
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.keyboad.*
import malayalamdictionary.samasya.BuildConfig
import malayalamdictionary.samasya.app.MyApplication
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.app.home.view.adapter.MeaningListAdapter
import malayalamdictionary.samasya.app.home.view.adapter.SuggestionListAdapter
import malayalamdictionary.samasya.app.database.DatabaseHelper
import malayalamdictionary.samasya.app.helper.Common
import malayalamdictionary.samasya.app.helper.ConnectionDetector
import malayalamdictionary.samasya.app.helper.FlipAnimation
import malayalamdictionary.samasya.app.helper.OnSwipeTouchListener
import malayalamdictionary.samasya.app.util.FireBaseHandler
import malayalamdictionary.samasya.app.about.view.AboutUsActivity
import malayalamdictionary.samasya.app.favorite.view.FavouriteActivity
import malayalamdictionary.samasya.app.translator.view.GoogleTranslateActivity
import malayalamdictionary.samasya.app.history.view.HistoryActivity
import malayalamdictionary.samasya.app.util.updateText
import malayalamdictionary.samasya.domain.firebase.AppUpdate
import malayalamdictionary.samasya.domain.firebase.RemoteConfig
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var suggestionLisAdapter: SuggestionListAdapter
    private lateinit var meaningListAdapter: MeaningListAdapter
    private lateinit var textToSpeech: TextToSpeech

    private val REQ_CODE_SPEECH_INPUT = 1
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    //    NativeExpressAdView adView;
    private var fromFav = "null"
    private var fromHis = "null"

    private lateinit var type: Typeface
    private lateinit var typeButton: Typeface

    private lateinit var symLetters: Array<String>
    private lateinit var consLetters: Array<String>
    private lateinit var vowLetters: Array<String>
    private lateinit var chilluLetters: Array<String>
    private var isShowKeyboard: Boolean = false
    var charIndex: Int = 0
    private lateinit var text: String
    var cursorPossition: Int = 0
    private var isConsonants: Int = 0
    private var isChillu: Int = 0
    lateinit var typedWord: String
    var longPressed: Boolean = false

    var doubleBackToExitPressedOnce = false
    lateinit var databaseHelper: DatabaseHelper
    lateinit var myRef: DatabaseReference

    @Inject
    lateinit var fireBaseHandler: FireBaseHandler

    @Inject
    lateinit var preference: SharedPreferences

    @Inject
    lateinit var connectionDetector: ConnectionDetector

    @Inject
    lateinit var remoteConfig: RemoteConfig

    lateinit var recyclerViewSuggestion: RecyclerView
    lateinit var recyclerViewMeaning: RecyclerView
    lateinit var recyclerViewMeaningBack: RecyclerView
    lateinit var recyclerViewSuggestionBack: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        initDagger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = RateThisApp.Config(3, 5)
        RateThisApp.init(config)

        initObserver()

        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("English")

        this.type = Typeface.createFromAsset(assets, "fonts/mal.ttf")
        typeButton = Typeface.createFromAsset(assets, "fonts/mlwttkarthika.ttf")
        this.isShowKeyboard = false
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

//        Log.d("admob_id",getString(R.string.ad_unit_id));
//        adView = (NativeExpressAdView)findViewById(R.id.adView_native);
//        if (pref.getInt(AdblockActivity.COUNT,5)>0) {
//
//            adView.loadAd(new AdRequest.Builder().build());
//        }

        button_google_translate.setOnClickListener {

        }
        recyclerViewSuggestion = list_view_suggestion as RecyclerView
        recyclerViewSuggestionBack = list_view_suggestion_back as RecyclerView
        recyclerViewMeaning = list_view_meaning as RecyclerView
        recyclerViewMeaningBack = list_view_meaning_back as RecyclerView

        databaseHelper = DatabaseHelper(this)
        suggestionLisAdapter = SuggestionListAdapter()
        meaningListAdapter = MeaningListAdapter()
        recyclerViewSuggestion.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        recyclerViewSuggestion.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recyclerViewSuggestion.itemAnimator = null
        recyclerViewSuggestion.adapter = suggestionLisAdapter
        recyclerViewSuggestionBack.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL, false)
        recyclerViewSuggestionBack.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recyclerViewSuggestionBack.itemAnimator = null
        recyclerViewSuggestionBack.adapter = suggestionLisAdapter
        recyclerViewMeaning.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerViewMeaning.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recyclerViewMeaning.adapter = meaningListAdapter
        recyclerViewMeaningBack.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL, false)
        recyclerViewMeaningBack.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recyclerViewMeaningBack.adapter = meaningListAdapter
        adapterObservers()

        suggestionLisAdapter.registerAdapterDataObserver(suggestionAdapterObserver)

        search_word.setOnClickListener(this)
        re_first.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeTop() {}

            override fun onSwipeRight() {
                flipCard()
            }

            override fun onSwipeLeft() {
                flipCard()
            }

            override fun onSwipeBottom() {}

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })

        re_second.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeTop() {}
            override fun onSwipeRight() {
                flipCard()
            }

            override fun onSwipeLeft() {
                flipCard()
            }

            override fun onSwipeBottom() {}

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })

        buttonSpace.setOnClickListener { autoCompleteTextView.dispatchKeyEvent(KeyEvent(0, 62)) }

        backspace.setOnClickListener {
            if (autoCompleteTextView.length() > 0) {
                cursorPossition = autoCompleteTextView.selectionStart
                if (cursorPossition != 0) {
                    val str1 = autoCompleteTextView.text.subSequence(0, cursorPossition - 1).toString()
                    autoCompleteTextView.updateText(StringBuilder(str1).append(autoCompleteTextView.text.subSequence(cursorPossition, autoCompleteTextView.length()).toString()).toString())
                    autoCompleteTextView.setSelection(cursorPossition - 1)

                }
            }
        }

        toggleButton.typeface = type
        toggleButton.setOnClickListener { toggleButton() }

        chilluButton.typeface = type
        chilluButton.setOnClickListener { toggleChilluButton() }

        moreToSymbol2.setOnClickListener {
            hide(R.id.fixedRow2)
            show(R.id.fixedRow3)
        }

        backToSymbol1.setOnClickListener {
            show(R.id.fixedRow2)
            hide(R.id.fixedRow3)
        }

        moreTo2.setOnClickListener {
            hide(R.id.vowRow1)
            hide(R.id.vowRow2)
            hide(R.id.vowRow3)
            show(R.id.vowRow4)
            show(R.id.vowRow5)
            show(R.id.vowRow6)
        }

        moreToCons.setOnClickListener {
            hide(R.id.consRow1)
            hide(R.id.consRow2)
            hide(R.id.consRow3)
            show(R.id.consRow4)
            show(R.id.consRow5)
            show(R.id.consRow6)
        }

        moreToCons2.setOnClickListener {
            hide(R.id.consRow4)
            hide(R.id.consRow5)
            hide(R.id.consRow6)
            show(R.id.consRow7)
            show(R.id.consRow8)
            show(R.id.consRow9)
        }

        backToCons.setOnClickListener {
            hide(R.id.consRow4)
            hide(R.id.consRow5)
            hide(R.id.consRow6)
            show(R.id.consRow1)
            show(R.id.consRow2)
            show(R.id.consRow3)
        }

        backToCons2.setOnClickListener {
            hide(R.id.consRow7)
            hide(R.id.consRow8)
            hide(R.id.consRow9)
            show(R.id.consRow4)
            show(R.id.consRow5)
            show(R.id.consRow6)
        }

        backTo1.setOnClickListener {
            hide(R.id.vowRow4)
            hide(R.id.vowRow5)
            hide(R.id.vowRow6)
            show(R.id.vowRow1)
            show(R.id.vowRow2)
            show(R.id.vowRow3)
        }

        this.symLetters = arrayOf("m", "n", "o", "p", "q", "s", "t", "v", "y", "r", "u", "z", "{", "w", "x", ".")
        this.consLetters = arrayOf("\u00f1", "\u00a1", "\u00f3", "\u00a7", "\u00af", "\u00e2", "\u00e4", "\u00bd", "\u00a8", "\u00ef", "\u00c5", "\u00ab", "\u00b8", "\u00bc", "\u00ae", "\u00b4", "\u00bf", "\u00f4", "\u00aa", "\u00b5", "\u00a3", "\u00d6", "\u00d1", "\u00da", "\u00d4", "\u00e3", "\u00d0", "\u00d5", "\u00de", "\u00e0", "\u00a6", "\u00d8", "\u00dd", "\u00df", "\u00b2", "\u00b0", "\u00b1", "\u00c8", "\u00ca", "\u00ba", "\u00c6", "\u00cd", "\u00a4", "\u00a2", "\u00a5", "\u00b9", "\u00be", "\u00c7", "\u00bb", "\u00c9", "\u00ce", "\u00cf", "\u00d7", "\u00d9", "\u00db", "\u00d3", "\u00e1", "\u00d2", "\u00dc")
        this.vowLetters = arrayOf("A", "\\", "h", "a", "b", "k", "I", "c", "X", "S", "]", "e", "W", "d", "F", "C", "i", "N", "G", "H", "B", "R", "j", "D", "f", "Z", "K", "l", "g", "_", "U", "^", "P", "M", "`", "[", "L", "J", "Y", "V", "O", "T", "Q", "E")
        this.chilluLetters = arrayOf("\u00c0", "\u00f0", "\u00b3", "\u00c4", "\u00ac")
        this.isConsonants = 0
        this.isChillu = 0
        this.typedWord = ""
        this.longPressed = false

        rebuildDatabase()
        setViews()
    }

    private fun adapterObservers(){
        suggestionLisAdapter.getSuggestionLiveData().observe(this, Observer {word ->
            fillData(word)
        })
    }

    private fun toggleChilluButton() {
        if (this.isChillu == 0) {
            hideAll()
            show(R.id.chilluRow)
            this.isChillu = 1
            chilluButton.text = "m"
        } else {
            hideAll()
            show(R.id.fixedRow2)
            this.isChillu = 0
            chilluButton.text = "\u00c0"
        }
        show(R.id.vowRow1)
        show(R.id.vowRow2)
        show(R.id.vowRow3)
        this.isConsonants = 0
        toggleButton.text = "\u00a1"
    }

    private fun hide(id: Int) {
        findViewById<View>(id).visibility = View.GONE
    }

    private fun hideAll() {
        hide(R.id.fixedRow2)
        hide(R.id.fixedRow3)
        hide(R.id.vowRow1)
        hide(R.id.vowRow2)
        hide(R.id.vowRow3)
        hide(R.id.vowRow4)
        hide(R.id.vowRow5)
        hide(R.id.vowRow6)
        hide(R.id.consRow1)
        hide(R.id.consRow2)
        hide(R.id.consRow3)
        hide(R.id.consRow4)
        hide(R.id.consRow5)
        hide(R.id.consRow6)
        hide(R.id.consRow7)
        hide(R.id.consRow8)
        hide(R.id.consRow9)
        hide(R.id.chilluRow)
        hide(R.id.numSymRow1)
        hide(R.id.numSymRow2)
        hide(R.id.numSymRow3)
        hide(R.id.numSymRow4)
    }

    private fun flipCard() {

        val flipAnimation = FlipAnimation(re_first, re_second)

        if (re_first.visibility == View.GONE) {
            autoCompleteTextView.typeface = Typeface.DEFAULT
            autoCompleteTextView.updateText("")
            autoCompleteTextView.visibility = View.GONE
            flipAnimation.reverse()
            Common.englishToMayalayam = true
            textViewHint.visibility = View.VISIBLE
            imageButtonMic.visibility = View.VISIBLE
            imageButton_close.visibility = View.GONE
            search_word.visibility = View.GONE
            hideKey()

            hideMalayalam(R.id.keyBoardLayout)
            textViewHint.text = getString(R.string.eng_mal)
            fab_swip.setImageResource(R.drawable.mala)

            //track fire base event for fab click`
            fireBaseHandler.logFirebaseEvents(FireBaseHandler.FAB_M, null)
        } else {
            val font = Typeface.createFromAsset(assets, "fonts/mlwttkarthika.ttf")
            autoCompleteTextView.typeface = font
            autoCompleteTextView.updateText("")
            Common.englishToMayalayam = false
            textViewHint.visibility = View.VISIBLE
            autoCompleteTextView.visibility = View.GONE
            imageButton_close.visibility = View.GONE
            search_word.visibility = View.VISIBLE
            hideMalayalam(R.id.keyBoardLayout)
            hideKey()

            imageButtonMic.visibility = View.GONE
            textViewHint.text = getString(R.string.mal_eng)
            fab_swip.setImageResource(R.drawable.e)

            //track fire base event for fab click`
            fireBaseHandler.logFirebaseEvents(FireBaseHandler.FAB_E, null)
        }

        drawer_layout.startAnimation(flipAnimation)
    }

    fun onCardClick(view: View) {
        flipCard()
    }

    private fun initObserver(){
        remoteConfig.getAppUpdateLiveData().observe(this, Observer {
            if (BuildConfig.VERSION_NAME < it.version ?:"" && it.forceUpdate)
            {
                showAppUpdateDialog(it)
            }
        })
    }

    private fun showAppUpdateDialog(appUpdate: AppUpdate){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(appUpdate.title)
        alertDialog.setMessage(appUpdate.description)
        alertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton(getString(R.string.update)){dialog, which ->
            rateApp()
        }
        alertDialog.show()
    }

    lateinit var textEmitter: ObservableEmitter<String>
    private fun setViews() {

        initNavigationDrawer()
        textToSpeech = TextToSpeech(this.applicationContext, TextToSpeech.OnInitListener { })

        setSymbolButtons()
        setConsonantButtons()
        setVowelButtons()
        setChilluButtons()

        autoCompleteTextView.setOnClickListener(this)
        imageButtonMic.setOnClickListener(this)
        imageButton_close.setOnClickListener(this)
        textViewHint.setOnClickListener(this)
        tv_favorite.setOnClickListener(this)
        tv_pronounce.setOnClickListener(this)
        button_google_translate.setOnClickListener(this)
        buttonFeedBack.setOnClickListener(this)


        Observable.create(ObservableOnSubscribe<String> { subscriber ->
            textEmitter = subscriber
            autoCompleteTextView.addTextChangedListener(textWatcher)
        }).map{ text -> text.toLowerCase().trim() }
                .debounce(350, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter { text -> text.isNotBlank() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { text ->
                    Log.i("check_debounce", "text: $text")
                    if (text.length > 1) {

                        if (Common.englishToMayalayam) {
                            val suggestionList = getWords(text.toString())
                            suggestionLisAdapter.submitList(suggestionList)
                            Log.i("check_suggestion","size: ${suggestionList.size} words: ${suggestionList.size}")

                            if (suggestionList.isEmpty()) {
                                card_view_list.visibility = View.GONE
                                textView_word.typeface = Typeface.DEFAULT
                                textView_word.text = autoCompleteTextView.text.toString().trim { it <= ' ' }
                                relayout_feedback.visibility = View.VISIBLE
                                card_view_list_meaning.visibility = View.GONE
                                card_view_list_meaning_back.visibility = View.GONE
                            } else {
                                relayout_feedback.visibility = View.GONE
                            }
                            search_word.visibility = View.GONE
                            toolbar.visibility = View.GONE
                            //adView.setVisibility(View.GONE);
                        } else {

                            val suggestionList = getWordsMalayalam(text.toString())
                            suggestionLisAdapter.submitList(suggestionList)
                            Log.i("check_mal_suggestion","size: ${suggestionList.size} words: ${suggestionList.size}")

                            search_word.visibility = View.VISIBLE
                            if (suggestionList.isEmpty()) {
                                card_view_list_back.visibility = View.GONE
                                textView_word.typeface = type
                                textView_word.text = autoCompleteTextView.text.toString().trim { it <= ' ' }
                                relayout_feedback.visibility = View.VISIBLE
                                card_view_list_meaning.visibility = View.GONE
                                card_view_list_meaning_back.visibility = View.GONE
                            } else {
                                relayout_feedback.visibility = View.GONE
                            }
                        }
                        imageButtonMic.visibility = View.GONE
                        fab_swip.visibility = View.GONE
                    } else {

                        fab_swip.visibility = View.VISIBLE
                        if (Common.englishToMayalayam) {
                            imageButtonMic.visibility = View.VISIBLE
                        }
                        imageButton_close.visibility = View.GONE
                        card_view_list.visibility = View.GONE
                        card_view_list_back.visibility= View.GONE
                        card_view_list_meaning_back.visibility = View.GONE
                        card_view_list_meaning.visibility = View.GONE
                        relayout_feedback.visibility = View.GONE
                        toolbar.visibility = View.VISIBLE
                    }
                }

        autoCompleteTextView.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                fillData(autoCompleteTextView.text.toString().trim { it <= ' ' })
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private val textWatcher = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (autoCompleteTextView.hasFocus()) {
                textEmitter.onNext(s.toString())
            }
        }
    }

    private fun rateApp() {
        val intent = Intent(Intent.ACTION_VIEW)

        intent.data = Uri.parse("https://goo.gl/TltKno")
        if (!MyStartActivity(intent)) {
            //Market (Google play) app seems not installed, let's try to open a webbrowser
            intent.data = Uri.parse("https://goo.gl/TltKno")
            if (!MyStartActivity(intent)) {
                //Well if this also fails, we have run out of options, inform the user.
                Toast.makeText(this@MainActivity, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun MyStartActivity(aIntent: Intent): Boolean {
        try {

            startActivity(aIntent)
            return true
        } catch (e: ActivityNotFoundException) {
            return false
        }

    }

    private fun initNavigationDrawer() {
        navigation_view.itemIconTintList = null
        navigation_view.setNavigationItemSelectedListener { item ->
            val id = item.itemId
            when (id) {
                R.id.home -> {
                    finish()
                    val intentHome = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(intentHome)
                    drawer_layout.closeDrawers()
                }
                R.id.favourite -> {

                    val intentFavourite = Intent(this@MainActivity, FavouriteActivity::class.java)
                    startActivity(intentFavourite)
                    drawer_layout.closeDrawers()
                }
                R.id.history -> {
                    val intentHistory = Intent(this@MainActivity, HistoryActivity::class.java)
                    startActivity(intentHistory)
                    drawer_layout.closeDrawers()
                    drawer_layout.closeDrawers()
                }
//                R.id.block_add -> {
//
//                    val intentAdBlock = Intent(this@MainActivity, AdblockActivity::class.java)
//                    startActivity(intentAdBlock)
//                    drawerLayout.closeDrawers()
//                }
                R.id.google_translate -> {
                    //track fire base event for google translate click
                    fireBaseHandler.logFirebaseEvents(FireBaseHandler.GOOGLE_TRANSLATE_CLICK, null)

                    val intentTranslate = Intent(this@MainActivity, GoogleTranslateActivity::class.java)
                    startActivity(intentTranslate)
                    drawer_layout.closeDrawers()
                }

                R.id.rate -> {
                    fireBaseHandler.logFirebaseEvents(FireBaseHandler.RATE_US, null)

                    rateApp()
                    drawer_layout.closeDrawers()
                }
                R.id.share -> {
                    fireBaseHandler.logFirebaseEvents(FireBaseHandler.SHARE_APP, null)

                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Samasya - English Malayalam Dictionary :https://goo.gl/TltKno")
                    sendIntent.type = "text/*"
                    startActivity(sendIntent)
                }
                R.id.about -> {
                    fireBaseHandler.logFirebaseEvents(FireBaseHandler.ABOUT, null)

                    val intentAbout = Intent(this@MainActivity, AboutUsActivity::class.java)
                    startActivity(intentAbout)
                    drawer_layout.closeDrawers()
                }
            }
            true
        }

        actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                toolbar.visibility = View.VISIBLE
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                hideKey()
                toolbar.visibility = View.GONE
            }
        }
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

    }

    private fun rebuildDatabase() {


        try {
            // check if database exists in app path, if not copy it from assets
            databaseHelper.createDb()
        } catch (ioe: IOException) {
            throw Error("Unable to create database")
        }

    }

    private fun getWords(query: String):List<String>  {

        val myDbHelper2 = DatabaseHelper(this)

        try {
            myDbHelper2.open()
            val c2 = myDbHelper2.readableDatabase.rawQuery("Select distinct ENG From samasya_suggestion where ENG like  '" + query.trim { it <= ' ' }.replace("''", "").replace("'", "''") + "%'  LIMIT 10", null)
            val strings = mutableListOf<String>()
            c2.moveToFirst()
            while (!c2.isAfterLast) {
                strings.add(c2.getString(0).toLowerCase())
                c2.moveToNext()
            }

            c2.close()
            if (strings.size != 0) {
                card_view_list.visibility = View.VISIBLE
                card_view_list_meaning.visibility = View.GONE
            }
            list_view_suggestion.visibility = View.VISIBLE

            databaseHelper.close()
            return strings
        } catch (sqle: SQLException) {
            throw sqle
        }
    }

    private fun getWordsMalayalam(query: String): List<String>{
        val myDbHelper2 = DatabaseHelper(this)
        try {

            myDbHelper2.open()
            val c2 = myDbHelper2.readableDatabase.rawQuery("Select distinct MAL From samasya_eng_mal where MAL GLOB  '" + query.trim { it <= ' ' }.replace("''", "").replace("'", "''") + "*'  LIMIT 10", null)
            val strings = mutableListOf<String>()
            c2.moveToFirst()
            while (!c2.isAfterLast) {
                strings.add(c2.getString(0))
                c2.moveToNext()
            }

            c2.close()
            if (strings.size != 0) {
                card_view_list_back.visibility = View.VISIBLE
                card_view_list_meaning_back.visibility = View.GONE
            }
            list_view_suggestion_back.visibility = View.VISIBLE
            databaseHelper.close()
            return strings
        } catch (sqle: SQLException) {
            throw sqle
        }
    }

    private fun historyUpdate() {
        val myDbHelper2 = DatabaseHelper(this)
        val bk_txt = autoCompleteTextView.text.toString().trim { it <= ' ' }
        try {
            myDbHelper2.open()
            try {
                if (bk_txt.length > REQ_CODE_SPEECH_INPUT) {
                    var cursor: Cursor? = null

                    if (Common.englishToMayalayam) {

                        cursor = myDbHelper2.writableDatabase.rawQuery("INSERT INTO samasya_eng_history (ENG) values('" + bk_txt.replace("'", "''") + "')", null)
                    } else {
                        cursor = myDbHelper2.writableDatabase.rawQuery("INSERT INTO samasya_mal_history (MAL) values('" + bk_txt.replace("'", "''") + "')", null)

                    }
                    cursor!!.moveToFirst()
                    while (!cursor.isAfterLast) {
                        cursor.moveToNext()
                    }
                    cursor.close()
                }
                myDbHelper2.close()
            } catch (sqle: SQLException) {

                throw sqle
            }

        } catch (sqle2: SQLException) {

            throw sqle2
        }

    }

    private fun fillData(word: String) {
        imageButton_close.visibility = View.VISIBLE
        autoCompleteTextView.visibility = View.VISIBLE
        textViewHint.visibility = View.GONE
        autoCompleteTextView.updateText(word)
        if (!Common.englishToMayalayam) {
            //track firebase event searched malayalam word
            val bundle = Bundle()
            bundle.putString(FireBaseHandler.SEARCH_MALAYALAM_WORD,word)
            fireBaseHandler.logFirebaseEvents(FireBaseHandler.SEARCH_MALAYALAM, bundle)

            autoCompleteTextView.typeface = type
        } else {
            //track firebase event searched english word
            val bundle = Bundle()
            bundle.putString(FireBaseHandler.SEARCH_ENGLISH_WORD,word)
            fireBaseHandler.logFirebaseEvents(FireBaseHandler.SEARCH_ENGLISH, bundle)

            autoCompleteTextView.typeface = Typeface.DEFAULT
        }

        imageButtonMic.visibility = View.GONE
        search_word.visibility = View.GONE
        fab_swip.visibility = View.GONE

        hideKey()
        val myDbHelper1 = DatabaseHelper(this)
        try {
            myDbHelper1.open()
            var c1: Cursor? = null

            if (Common.englishToMayalayam) {
                historyUpdate()
                c1 = myDbHelper1.readableDatabase.rawQuery("Select * From samasya_eng_mal where ENG like '" + word.trim { it <= ' ' }.replace("''", "").replace("'", "''") + "'", null)
            } else {
                historyUpdate()
                c1 = myDbHelper1.readableDatabase.rawQuery("Select * From samasya_eng_mal where MAL like '" + word.trim { it <= ' ' }.replace("''", "").replace("'", "''") + "'", null)
            }

            val strings = ArrayList<String>()
            c1!!.moveToFirst()
            if (Common.englishToMayalayam) {
                while (!c1.isAfterLast) {
                    strings.add(c1.getString(1).replace("\u00ad", "\u00ef"))
                    c1.moveToNext()
                }
            } else {
                while (!c1.isAfterLast) {
                    strings.add(c1.getString(0).replace("\u00ad", "\u00ef"))

                    c1.moveToNext()
                }
            }

            val mString = strings.toList()

            list_view_meaning.visibility = View.VISIBLE
            meaningListAdapter = MeaningListAdapter()
            recyclerViewMeaning.adapter = meaningListAdapter
            recyclerViewMeaningBack.adapter = meaningListAdapter

            //            adView.setVisibility(View.GONE);
            if (Common.englishToMayalayam) {
                toolbar.visibility = View.VISIBLE
                meaningListAdapter.submitList(mString)
            } else {
                toolbar.visibility = View.VISIBLE
                hideMalayalam(R.id.keyBoardLayout)
                meaningListAdapter.submitList(mString)
            }

            if (mString.isNotEmpty()) {
                card_view_list_meaning.visibility = View.VISIBLE
                card_view_list_meaning_back.visibility = View.VISIBLE
                relayout_feedback.visibility = View.GONE
            }else{
                card_view_list_meaning.visibility = View.GONE
                card_view_list_meaning_back.visibility = View.GONE
                textView_word.text = autoCompleteTextView.text.toString().trim { it <= ' ' }
                relayout_feedback.visibility = View.VISIBLE
            }
            card_view_list.visibility = View.GONE
            card_view_list_back.visibility = View.GONE

            myDbHelper1.close()
        } catch (sqle: SQLException) {
            throw sqle
        }

    }


    private fun hideKey() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(autoCompleteTextView.windowToken, 0)
    }

    private fun speak() {

        hideKey()
        val word = autoCompleteTextView.text.toString().trim { it <= ' ' }
        if (word != "") {
            textToSpeech.language = Locale.getDefault()
            textToSpeech.isLanguageAvailable(Locale.getDefault())
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null)
        } else {
            Toast.makeText(applicationContext, "Please choose a word", Toast.LENGTH_SHORT).show()
        }
        //track firebase event
        val bundle = Bundle()
        bundle.putString(FireBaseHandler.PRONUNCIATION_WORD, word)
        fireBaseHandler.logFirebaseEvents(FireBaseHandler.PRONUNCIATION, bundle)
    }

    private fun speakToText() {
        val intentSpeak = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intentSpeak.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intentSpeak.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intentSpeak.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt))
        try {
            //track fire base event for search mic
            fireBaseHandler.logFirebaseEvents(FireBaseHandler.SEARCH_USING_MIC, null)
            startActivityForResult(intentSpeak, REQ_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            //track fire base event for search mic not support
            fireBaseHandler.logFirebaseEvents(FireBaseHandler.SEARCH_USING_MIC_NOT_SUPPORT, null)
            Toast.makeText(applicationContext, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> if (resultCode == Activity.RESULT_OK && null != data) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                autoCompleteTextView.updateText(result[0])
                autoCompleteTextView.setSelection(autoCompleteTextView.text.length)
                imageButtonMic.visibility = View.GONE
                textViewHint.visibility = View.GONE
            }
        }
    }

    private fun updateFavourite() {

        if (autoCompleteTextView.text.toString().trim { it <= ' ' } != "") {

            val databaseHelper = DatabaseHelper(this)
            val favourite = autoCompleteTextView.text.toString().trim { it <= ' ' }
            try {
                databaseHelper.open()
                try {
                    if (favourite.length > REQ_CODE_SPEECH_INPUT) {

                        val c444: Cursor?
                        if (Common.englishToMayalayam) {
                            c444 = databaseHelper.writableDatabase.rawQuery("INSERT INTO samasya_eng_favourite (ENG) values('" + favourite.replace("'", "''") + "')", null)
                            Toast.makeText(this, "$favourite added to favourites", Toast.LENGTH_SHORT).show()
                            val bundle = Bundle()
                            bundle.putString(FireBaseHandler.SAVED_WORD, favourite)
                            fireBaseHandler.logFirebaseEvents(FireBaseHandler.SAVE_WORD_ENGLISH,bundle)
                        } else {
                            Toast.makeText(this, " Word added to favourites", Toast.LENGTH_SHORT).show()
                            c444 = databaseHelper.writableDatabase.rawQuery("INSERT INTO samasya_mal_favourite (MAL) values('" + favourite.replace("'", "''") + "')", null)
                            val bundle = Bundle()
                            bundle.putString(FireBaseHandler.SAVED_WORD, favourite)
                            fireBaseHandler.logFirebaseEvents(FireBaseHandler.SAVE_WORD_MALAYALAM,bundle)
                        }

                        c444!!.moveToFirst()
                        while (!c444.isAfterLast) {
                            c444.moveToNext()
                        }
                        c444.close()
                    }
                    databaseHelper.close()
                } catch (sqle: SQLException) {
                    throw sqle
                }

            } catch (sqle2: SQLException) {
                throw sqle2
            }

        } else {
            Toast.makeText(applicationContext, "Please choose a word", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.autoCompleteTextView -> {
                //adView.setVisibility(View.GONE);
//                toolbar.visibility = View.GONE
                if (!Common.englishToMayalayam) {
                    showKeyboard()
                    hideKey()
                    relayout_feedback.visibility = View.GONE
                } else {
                    relayout_feedback.visibility = View.GONE
                    hideMalayalam(R.id.keyBoardLayout)
                    showKey()
                }
            }

            R.id.imageButtonMic -> {
                hideKey()
                imageButtonMic.visibility = View.VISIBLE
                card_view_list_meaning.visibility = View.GONE
                speakToText()
            }

            R.id.imageButton_close -> {

                autoCompleteTextView.updateText("")
                card_view_list.visibility = View.GONE
                card_view_list_meaning.visibility = View.GONE
                card_view_list_back.visibility = View.GONE
                card_view_list_meaning_back.visibility = View.GONE
                if (preference.getInt(Common.COUNT, 5) > 0) {
                    //                adView.setVisibility(View.VISIBLE);
                }
                relayout_feedback.visibility = View.GONE
                toolbar.visibility = View.VISIBLE
                if (Common.englishToMayalayam){
                    showKey()
                }else{
                    showKeyboard()
                }
            }

            R.id.textViewHint -> {

                textViewHint.visibility = View.GONE
                //            adView.setVisibility(View.GONE);
                autoCompleteTextView.visibility = View.VISIBLE
                autoCompleteTextView.requestFocus()
//                toolbar.visibility = View.GONE
                if (Common.englishToMayalayam) {
                    showKey()
                } else {
//                    toolbar.visibility = View.GONE
                    showKeyboard()
                }
            }

            R.id.search_word -> {
                val malayalamWord = autoCompleteTextView.text.toString().trim { it <= ' ' }
                //track fire base event for malayalam word search using search button
                val bundle = Bundle()
                bundle.putString(FireBaseHandler.SEARCH_MALAYALAM_IMAGE_BUTTON_WORD,malayalamWord)
                fireBaseHandler.logFirebaseEvents(FireBaseHandler.SEARCH_MALAYALAM_IMAGE_BUTTON, bundle)

                hideMalayalam(R.id.keyBoardLayout)
                suggestionLisAdapter.submitList(emptyList())
                fillData(malayalamWord)
            }

            R.id.tv_favorite -> {
                updateFavourite()
            }

            R.id.tv_pronounce -> {
                if (Common.englishToMayalayam) {
                    speak()
                } else {
                    Toast.makeText(applicationContext, "Pronunciation is not available for malayalam words", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.button_google_translate -> {
                fireBaseHandler.logFirebaseEvents(FireBaseHandler.USE_GOOGLE_TRANSLATE, null)

                if (connectionDetector.isConnectingToInternet()) {
                    val copyText = autoCompleteTextView.text.toString().trim { it <= ' ' }
                    val label = "copy"
                    var clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(label, copyText)
                    clipboard.setPrimaryClip(clip)

                    if (Common.englishToMayalayam) {
                        Toast.makeText(applicationContext, "Word copied to clipboard Long press to paste it into translator", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(applicationContext, "Copy word from search box and paste it into translator", Toast.LENGTH_LONG).show()

                    }

                    val intentTranslate = Intent(this@MainActivity, GoogleTranslateActivity::class.java)
                    startActivity(intentTranslate)
                } else {
                    Snackbar.make(relayout_feedback, "No network connection", Snackbar.LENGTH_SHORT).show()
                }
            }

            R.id.buttonFeedBack -> {
                fireBaseHandler.logFirebaseEvents(FireBaseHandler.SEND_TO_DB, null)

                if (connectionDetector.isConnectingToInternet()) {
                    hideKey()
                    hideMalayalam(R.id.keyBoardLayout)
                    if (Common.englishToMayalayam) {
                        myRef.child(textView_word.text.toString().trim { it <= ' ' }).setValue(textView_word.text.toString().trim { it <= ' ' })
                    }
                    Toast.makeText(applicationContext, "Thank you for your suggestion, we will update it soon", Toast.LENGTH_LONG).show()
                    relayout_feedback.visibility = View.GONE
                    card_view_list.visibility = View.GONE
                    card_view_list_back.visibility = View.GONE
                    card_view_list_meaning.visibility = View.GONE
                    card_view_list_meaning_back.visibility = View.GONE
                    autoCompleteTextView.updateText("")

                } else {
                    hideKey()
                    Toast.makeText(applicationContext, "No internet connection", Toast.LENGTH_LONG).show()
                }
            }

        }
    }


    fun showKey() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(autoCompleteTextView, 0)
    }

    override fun onPause() {
        super.onPause()
        fromFav = "null"
    }


    private fun toggleButton() {
        if (this.isConsonants == 0) {
            hideAll()
            show(R.id.consRow1)
            show(R.id.consRow2)
            show(R.id.consRow3)
            this.isConsonants = 1
            toggleButton.text = "A"
        } else {
            hideAll()
            show(R.id.vowRow1)
            show(R.id.vowRow2)
            show(R.id.vowRow3)
            this.isConsonants = 0
            toggleButton.text = "\u00a1"
        }
        show(R.id.fixedRow2)
        this.isChillu = 0
        chilluButton.text = "\u00c0"
    }


    private fun setVowelButtons() {
        val BUTTON_IDS = intArrayOf(R.id.vowButton1, R.id.vowButton2, R.id.vowButton3, R.id.vowButton4, R.id.vowButton5, R.id.vowButton6, R.id.vowButton7, R.id.vowButton8, R.id.vowButton9, R.id.vowButton10, R.id.vowButton11, R.id.vowButton12, R.id.vowButton13, R.id.vowButton14, R.id.vowButton15, R.id.vowButton16, R.id.vowButton17, R.id.vowButton18, R.id.vowButton19, R.id.vowButton20, R.id.vowButton21, R.id.vowButton22, R.id.vowButton23, R.id.vowButton24, R.id.vowButton25, R.id.vowButton26, R.id.vowButton27, R.id.vowButton28, R.id.vowButton29, R.id.vowButton30, R.id.vowButton31, R.id.vowButton32, R.id.vowButton33, R.id.vowButton34, R.id.vowButton35, R.id.vowButton36, R.id.vowButton37, R.id.vowButton38, R.id.vowButton39, R.id.vowButton40, R.id.vowButton41, R.id.vowButton42, R.id.vowButton43, R.id.vowButton44)
        var i = 0
        val length = BUTTON_IDS.size
        var i2 = 0
        while (i2 < length) {
            val theButton = findViewById<View>(BUTTON_IDS[i2]) as Button
            theButton.setTextAppearance(applicationContext, R.style.buttonStyle)
            theButton.setBackgroundResource(R.drawable.button_style)
            theButton.typeface = typeButton
            theButton.textSize = 20.0f
            theButton.text = this.vowLetters[i]
            theButton.setOnClickListener(AnonymousClass21(theButton))
            i += 1
            i2 += 1
        }
    }

    private fun setChilluButtons() {
        val BUTTON_IDS = intArrayOf(R.id.chilluButton1, R.id.chilluButton2, R.id.chilluButton3, R.id.chilluButton4, R.id.chilluButton5)
        var i = 0
        val length = BUTTON_IDS.size
        var i2 = 0
        while (i2 < length) {
            val theButton = findViewById<View>(BUTTON_IDS[i2]) as Button
            theButton.setTextAppearance(applicationContext, R.style.buttonStyle)
            theButton.setBackgroundResource(R.drawable.button_style)
            theButton.typeface = typeButton
            theButton.textSize = 20.0f
            theButton.text = this.chilluLetters[i]
            theButton.setOnClickListener(AnonymousClass24(theButton))
            i += 1
            i2 += 1
        }
    }


    private fun setConsonantButtons() {
        val CONS_BUTTON_IDS = intArrayOf(R.id.consButton1, R.id.consButton2, R.id.consButton3, R.id.consButton4, R.id.consButton5, R.id.consButton6, R.id.consButton7, R.id.consButton8, R.id.consButton9, R.id.consButton10, R.id.consButton11, R.id.consButton12, R.id.consButton13, R.id.consButton14, R.id.consButton15, R.id.consButton16, R.id.consButton17, R.id.consButton18, R.id.consButton19, R.id.consButton20, R.id.consButton21, R.id.consButton22, R.id.consButton23, R.id.consButton24, R.id.consButton25, R.id.consButton26, R.id.consButton27, R.id.consButton28, R.id.consButton29, R.id.consButton30, R.id.consButton31, R.id.consButton32, R.id.consButton33, R.id.consButton34, R.id.consButton35, R.id.consButton36, R.id.consButton37, R.id.consButton38, R.id.consButton39, R.id.consButton40, R.id.consButton41, R.id.consButton42, R.id.consButton43, R.id.consButton44, R.id.consButton45, R.id.consButton46, R.id.consButton47, R.id.consButton48, R.id.consButton49, R.id.consButton50, R.id.consButton51, R.id.consButton52, R.id.consButton53, R.id.consButton54, R.id.consButton55, R.id.consButton56, R.id.consButton57, R.id.consButton58, R.id.consButton59)
        var i = 0
        val length = CONS_BUTTON_IDS.size
        var i2 = 0
        while (i2 < length) {
            val theButton = findViewById<View>(CONS_BUTTON_IDS[i2]) as Button
            theButton.setTextAppearance(applicationContext, R.style.buttonStyle)
            theButton.setBackgroundResource(R.drawable.button_style)
            theButton.typeface = typeButton
            theButton.textSize = 20.0f
            theButton.text = this.consLetters[i]
            theButton.setOnClickListener(AnonymousClass22(theButton))
            i += 1
            i2 += 1
        }
    }


    private fun setSymbolButtons() {
        val SYM_BUTTON_IDS = intArrayOf(R.id.symButton1, R.id.symButton2, R.id.symButton3, R.id.symButton4, R.id.symButton5, R.id.symButton6, R.id.symButton7, R.id.symButton8, R.id.symButton9, R.id.symButton10, R.id.symButton11, R.id.symButton12, R.id.symButton13, R.id.symButton14, R.id.symButton15)
        var i = 0
        val length = SYM_BUTTON_IDS.size
        var i2 = 0
        while (i2 < length) {
            val theButton = findViewById<View>(SYM_BUTTON_IDS[i2]) as Button
            theButton.setTextAppearance(this@MainActivity, R.style.buttonStyle)
            theButton.setBackgroundResource(R.drawable.button_style)
            theButton.typeface = typeButton
            theButton.textSize = 20.0f
            theButton.text = this.symLetters[i]
            theButton.setOnClickListener(AnonymousClass23(theButton))
            i += 1
            i2 += 1
        }
    }

    private fun showKeyboard() {
        if (!this.isShowKeyboard) {
            show(R.id.keyBoardLayout)
            //            if (this.lvHeight == 0) {
            //                this.lvHeight = this.listWords.getHeight();
            //            }
            //            LayoutParams params = this.listWords.getLayoutParams();
            //            params.height = this.lvHeight - ((int) ((208.0f * getResources().getDisplayMetrics().density) + 0.5f));
            //            this.listWords.setLayoutParams(params);
            //            this.isShowKeyboard = true;
        }
    }

    fun show(id: Int) {
        findViewById<View>(id).visibility = View.VISIBLE

    }

    private fun hideMalayalam(id: Int) {
        findViewById<View>(id).visibility = View.GONE

    }


    internal inner class AnonymousClass24(var button: Button) : View.OnClickListener {


        override fun onClick(v: View) {

            try {
                charIndex = autoCompleteTextView.selectionStart
                text = autoCompleteTextView.text.toString()

                if (charIndex >= 0) {
                    autoCompleteTextView.updateText(StringBuilder((text.subSequence(0, charIndex)).toString()).append(this.button.text).append(text.subSequence(charIndex, text.length)).toString())
                    if (autoCompleteTextView.selectionStart != autoCompleteTextView.length()) {
                        autoCompleteTextView.setSelection(charIndex + 1)
                    }
                } else {
                    autoCompleteTextView.setText(Html.fromHtml(StringBuilder(autoCompleteTextView.text.toString()).append(this.button.text).toString()))
                    autoCompleteTextView.setSelection(autoCompleteTextView.length())
                }
                cursorPossition += 1
            } catch (e: Exception) {
            }

        }

    }

    internal inner class AnonymousClass22(var button: Button) : View.OnClickListener {

        override fun onClick(v: View) {

            try {
                charIndex = autoCompleteTextView.selectionStart
                text = autoCompleteTextView.text.toString()
                if (charIndex >= 0) {
                    autoCompleteTextView.updateText(StringBuilder((text.subSequence(0, charIndex)).toString()).append(this.button.text).append(text.subSequence(charIndex, text.length)).toString())
                    if (autoCompleteTextView.selectionStart != autoCompleteTextView.length()) {
                        autoCompleteTextView.setSelection(charIndex + 1)
                    }
                } else {
                    autoCompleteTextView.setText(Html.fromHtml(StringBuilder(autoCompleteTextView.text.toString()).append(this.button.text).toString()))
                    autoCompleteTextView.setSelection(autoCompleteTextView.length())
                }
                cursorPossition += 1
                typedWord += this.button.text
            } catch (e: Exception) {
            }

        }

    }

    internal inner class AnonymousClass21(var button: Button) : View.OnClickListener {

        override fun onClick(v: View) {
            try {
                charIndex = autoCompleteTextView.selectionStart
                text = autoCompleteTextView.text.toString()
                if (charIndex >= 0) {
                    autoCompleteTextView.updateText(StringBuilder((text.subSequence(0, charIndex)).toString()).append(button.text).append(text.subSequence(charIndex, text.length)).toString())
                    if (autoCompleteTextView.selectionStart != autoCompleteTextView.length()) {
                        autoCompleteTextView.setSelection(charIndex + 1)
                    }
                } else {
                    autoCompleteTextView.setText(Html.fromHtml(StringBuilder(autoCompleteTextView.text.toString()).append(button.text).toString()))
                    autoCompleteTextView.setSelection(autoCompleteTextView.length())
                }
                cursorPossition += 1
                typedWord += button.text
            } catch (e: Exception) {
            }

        }

    }

    internal inner class AnonymousClass23(var button: Button) : View.OnClickListener {

        override fun onClick(v: View) {
            try {

                charIndex = autoCompleteTextView.selectionStart
                text = autoCompleteTextView.text.toString()
                if (charIndex >= 0) {
                    autoCompleteTextView.updateText(StringBuilder((text.subSequence(0, charIndex)).toString()).append(button.text).append(text.subSequence(charIndex, text.length)).toString())
                    if (autoCompleteTextView.selectionStart != autoCompleteTextView.length()) {
                        autoCompleteTextView.setSelection(charIndex + 1)
                    }
                } else {
                    autoCompleteTextView.setText(Html.fromHtml(StringBuilder(autoCompleteTextView.text.toString()).append(button.text).toString()))
                    autoCompleteTextView.setSelection(autoCompleteTextView.length())
                }
                cursorPossition += 1
            } catch (e: Exception) {
            }

        }
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {

            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(applicationContext, "Touch again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onRestart() {
        super.onRestart()
        relayout_feedback.visibility = View.GONE
    }

    public override fun onStart() {
        super.onStart()
        RateThisApp.onStart(this)
        RateThisApp.showRateDialogIfNeeded(this)
    }

    private fun initDagger(){
        (applicationContext as MyApplication)
                .applicationComponent
                .inject(this)
    }

    private val suggestionAdapterObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            recyclerViewSuggestion.scrollToPosition(0)
            recyclerViewSuggestionBack.scrollToPosition(0)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            recyclerViewSuggestion.scrollToPosition(0)
            recyclerViewSuggestionBack.scrollToPosition(0)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            recyclerViewSuggestion.scrollToPosition(0)
            recyclerViewSuggestionBack.scrollToPosition(0)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            recyclerViewSuggestion.scrollToPosition(0)
            recyclerViewSuggestionBack.scrollToPosition(0)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            recyclerViewSuggestion.scrollToPosition(0)
            recyclerViewSuggestionBack.scrollToPosition(0)
        }
    }
}