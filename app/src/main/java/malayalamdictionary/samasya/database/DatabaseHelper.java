//package malayalamdictionary.samasya.database;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.MatrixCursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//
//    private static final int DATABASE_VERSION = 1;
//
//    public static final String DATABASE_NAME ="samasya";
//    private static String DATABASE_PATH;
//    Context myContext;
//    private SQLiteDatabase myDataBase;
//
//    public static final String TABLE_SUGGESTION= "samasya_suggestion";
//    public static final String TABLE_ENG_MAL= "samasya_eng_mal";
//    public static final String TABLE_ENG_HISTORY= "samasya_eng_history";
//    public static final String TABLE_ENG_FAVOURITE= "samasya_eng_favourite";
//    public static final String TABLE_MAL_HISTORY= "samasya_mal_history";
//    public static final String TABLE_MAL_FAVOURITE= "samasya_mal_favourite";
//
//
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        myContext = context;
//        DATABASE_PATH = context.getFilesDir().getParentFile().getPath()
//                + "/databases/";
//    }
//
//
//    public void createDb() throws IOException {
//        boolean check = checkDataBase();
//
//        SQLiteDatabase db_Read = null;
//
//        // Creates empty database default system path
//        db_Read = this.getWritableDatabase();
//        db_Read.close();
//        try {
//            if (!check) {
//                copyDataBase();
//            }
//
//        } catch (Exception e) {
//            throw new Error("Error copying database");
//        }
//    }
//
//    private boolean checkDataBase() {
//
//
//        SQLiteDatabase checkDB = null;
//        try {
//            String myPath = DATABASE_PATH + DATABASE_NAME;
//            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
//        } catch (SQLiteException e) {
//            // database does't exist yet.
//        }
//
//        if (checkDB != null) {
//            checkDB.close();
//        }
//        return checkDB != null ? true : false;
//    }
//
//    private void copyDataBase() throws IOException {
//
//        InputStream myInput = myContext.getAssets().open(DATABASE_NAME+".db");
//
//        String outFileName = DATABASE_PATH + DATABASE_NAME;
//
//        OutputStream myOutput = new FileOutputStream(outFileName);
//
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = myInput.read(buffer)) > 0) {
//            myOutput.write(buffer, 0, length);
//        }
//
//        myOutput.flush();
//        myOutput.close();
//        myInput.close();
//
//        Log.i("copydb","success");
//    }
//
//
//    public void open() throws SQLException {
//        String myPath = DATABASE_PATH + DATABASE_NAME;
//        myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUGGESTION);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENG_MAL);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENG_FAVOURITE);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENG_HISTORY);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAL_HISTORY);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAL_FAVOURITE);
//
//        onCreate(db);
//    }
//
//    public Cursor getSuggestion(String word){
//
//        String getSuggestion = "Select distinct ENG From samasya_suggestion where ENG like '"+word+ "' LIMIT 20";
//        return myDataBase.rawQuery(getSuggestion,null);
//    }
//
//    public ArrayList<Cursor> getData(String Query){
//        SQLiteDatabase sqlDB = this.getWritableDatabase();
//        String[] columns = new String[] { "mesage" };
//        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
//        MatrixCursor Cursor2= new MatrixCursor(columns);
//        alc.add(null);
//        alc.add(null);
//
//
//        try{
//            String maxQuery = Query ;
//            Cursor c = sqlDB.rawQuery(maxQuery, null);
//
//
//            Cursor2.addRow(new Object[] { "Success" });
//
//            alc.set(1,Cursor2);
//            if (null != c && c.getCount() > 0) {
//
//
//                alc.set(0,c);
//                c.moveToFirst();
//
//                return alc ;
//            }
//            return alc;
//        } catch(SQLException sqlEx){
//            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
//            alc.set(1,Cursor2);
//            return alc;
//        } catch(Exception ex){
//
//
//            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
//            alc.set(1,Cursor2);
//            return alc;
//        }
//
//
//    }
//}
