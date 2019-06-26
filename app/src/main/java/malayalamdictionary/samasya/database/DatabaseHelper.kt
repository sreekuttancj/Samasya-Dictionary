package malayalamdictionary.samasya.database

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class DatabaseHelper(var context: Context) : SQLiteOpenHelper(context, "samasya", null, 1) {

    private val DATABASE_VERSION = 1

    val DATABASE_NAME = "samasya"
    private val DATABASE_PATH: String
    private lateinit var myDataBase: SQLiteDatabase

    val TABLE_SUGGESTION = "samasya_suggestion"
    val TABLE_ENG_MAL = "samasya_eng_mal"
    val TABLE_ENG_HISTORY = "samasya_eng_history"
    val TABLE_ENG_FAVOURITE = "samasya_eng_favourite"
    val TABLE_MAL_HISTORY = "samasya_mal_history"
    val TABLE_MAL_FAVOURITE = "samasya_mal_favourite"

    init {
        DATABASE_PATH = context.filesDir.parentFile.path + "/databases/"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUGGESTION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENG_MAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENG_FAVOURITE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENG_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAL_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAL_FAVOURITE")

        onCreate(db)
    }


    @Throws(IOException::class)
    fun createDb() {
        val check = checkDataBase()

        var db_Read: SQLiteDatabase? = null

        // Creates empty database default system path
        db_Read = this.writableDatabase
        db_Read!!.close()
        try {
            if (!check) {
                copyDataBase()
            }

        } catch (e: Exception) {
            throw Error("Error copying database")
        }

    }

    private fun checkDataBase(): Boolean {


        var checkDB: SQLiteDatabase? = null
        try {
            val myPath = DATABASE_PATH + DATABASE_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)
        } catch (e: SQLiteException) {
            // database does't exist yet.
        }

        checkDB?.close()
        return checkDB != null
    }

    @Throws(IOException::class)
    private fun copyDataBase() {

        val myInput = context.assets.open("$DATABASE_NAME.db")

        val outFileName = DATABASE_PATH + DATABASE_NAME

        val myOutput = FileOutputStream(outFileName)

        val buffer = ByteArray(1024)
        val length: Int =myInput.read(buffer)
        while (length > 0) {
            myOutput.write(buffer, 0, length)
        }

        myOutput.flush()
        myOutput.close()
        myInput.close()

        Log.i("copydb", "success")
    }


    @Throws(SQLException::class)
    fun open() {
        val myPath = DATABASE_PATH + DATABASE_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS)
    }
    fun getSuggestion(word: String): Cursor {

        val getSuggestion = "Select distinct ENG From samasya_suggestion where ENG like '$word' LIMIT 20"
        return myDataBase.rawQuery(getSuggestion, null)
    }

    }
