package com.android.tony.notex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Notes",TABLE_1="Note",COL_1="ID",COL_2="Title",COL_3="Content",COL_4="Location";

    DatabaseHelper(Context context)
    {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT,%s TEXT,%s TEXT)",DatabaseHelper.TABLE_1,DatabaseHelper.COL_1,DatabaseHelper.COL_2,DatabaseHelper.COL_3,DatabaseHelper.COL_4));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE %s", DatabaseHelper.TABLE_1));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT,%s TEXT,%s TEXT)",DatabaseHelper.TABLE_1,DatabaseHelper.COL_1,DatabaseHelper.COL_2,DatabaseHelper.COL_3,DatabaseHelper.COL_4));
    }

    Boolean ipnutData(String title,String note)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_2,title);
        contentValues.put(DatabaseHelper.COL_3,note);
        contentValues.put(DatabaseHelper.COL_4,"Notes");
        long res = sqLiteDatabase.insert(DatabaseHelper.TABLE_1,null,contentValues);
        return res > 0;
    }
    Boolean updateData(String Id,String title,String note,String location)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_2,title);
        contentValues.put(DatabaseHelper.COL_3,note);
        contentValues.put(DatabaseHelper.COL_4,location);
        long res = sqLiteDatabase.update(DatabaseHelper.TABLE_1,contentValues,DatabaseHelper.COL_1 + " = ?" ,new String[]{Id});
        return res > 0;
    }

    Cursor getAllData()
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s ORDER BY %s", DatabaseHelper.TABLE_1, DatabaseHelper.COL_1),null);
    }

    Boolean deleteData(String Id)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.delete(DatabaseHelper.TABLE_1,DatabaseHelper.COL_1 + "=?", new String[]{Id}) > 0;
    }
}
