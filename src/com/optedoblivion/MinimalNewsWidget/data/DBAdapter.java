package com.optedoblivion.MinimalNewsWidget.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper{
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "mnmlnwswdgt.db";
    private static final String FEED_TABLE = "feeds";
    private static final int DATABASE_VERSION = 4;
    private static SQLiteDatabase sqliteDb;
    private static DBAdapter instance;
    private static Cursor mCursor;
    private static final String SQL_CREATE_FEED_TABLE = "CREATE TABLE " 
    + FEED_TABLE
    + " ( "
    + "_id integer primary key autoincrement, "
    + "title VARCHAR(255), "
    + "link VARCHAR(255), "
    + "updated DATE"
    + ")";
    
    private Context mContext;

    public DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(SQL_CREATE_FEED_TABLE);
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("DROP TABLE " + FEED_TABLE);
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }
    }

    public Cursor getFeeds(){
        String[] columns = {"title", "link"};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null; //"updated DESC";
        try{
            sqliteDb = this.getWritableDatabase();
            Cursor result = sqliteDb.query(FEED_TABLE, columns, selection, 
                                      selectionArgs, groupBy, having, orderBy);
            return result;
        }catch(Exception e){
            Log.e(TAG, e.toString());
            return null;
        }
    }
    
    public void insertFeeds(ContentValues values){
        try{
            sqliteDb = this.getWritableDatabase();
            sqliteDb.insert(FEED_TABLE, null, values);
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }finally{
            sqliteDb.close();
        }
    }
    
    public void clearFeeds(){
        try{
            sqliteDb = this.getWritableDatabase();
            sqliteDb.execSQL("DELETE FROM " + FEED_TABLE);
            sqliteDb.execSQL("VACUUM");
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }finally{
            sqliteDb.close();
        }
    }

    public void closeDb(){
        try{
            sqliteDb.close();
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }
    }

}
