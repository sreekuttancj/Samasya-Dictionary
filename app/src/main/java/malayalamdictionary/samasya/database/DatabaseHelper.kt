package malayalamdictionary.samasya.database

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val DATABASE_PATH: String = context.filesDir.parentFile.path + "/databases/"
    private lateinit var myDataBase: SQLiteDatabase
    private val mContext=context


    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "samasya.db"
    }
    override fun onCreate(p0: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DBContract.UserEntry}.TABLE_SUGGESTION")
        db.execSQL("DROP TABLE IF EXISTS ${DBContract.UserEntry}.TABLE_ENG_MAL")
        db.execSQL("DROP TABLE IF EXISTS ${DBContract.UserEntry}.TABLE_ENG_FAVOURITE")
        db.execSQL("DROP TABLE IF EXISTS ${DBContract.UserEntry}.TABLE_ENG_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS ${DBContract.UserEntry}.TABLE_MAL_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS ${DBContract.UserEntry}.TABLE_MAL_FAVOURITE")

        onCreate(db)
    }

    @Throws(IOException::class)
    fun createDb() {
        val check = checkDataBase()

        // Creates empty database default system path
        val db_Read: SQLiteDatabase = this.writableDatabase

        db_Read.close()
        try {
            if (!check) {
                copyDataBase()
            }

        } catch (e: Exception) {
            throw Error("Error copying database")
        }

    }

    private fun checkDataBase(): Boolean {
        val dbFilePath=mContext.getDatabasePath(DATABASE_NAME)
        return dbFilePath.exists().also { Log.i("check_db",dbFilePath.exists().toString()+ "path: $dbFilePath") }
    }


    @Throws(IOException::class)
    private fun copyDataBase() {

        val myInput = mContext.assets.open(DATABASE_NAME)

        val outFileName = DATABASE_PATH + DATABASE_NAME

        val myOutput = FileOutputStream(outFileName)

        val buffer = ByteArray(1024)
        while (myInput.read(buffer) > 0) {
            myOutput.write(buffer)
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
}
