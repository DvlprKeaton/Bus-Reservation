package com.example.bus21;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "emg.db";
    public static final String TABLE_NAME = "usertbl";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "email";
    public static final String COL_3 = "password";
    public static final String COL_4 = "status";
    public static final String TABLE_NAME2 = "reporttbl";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usertbl (ID INTEGER PRIMARY KEY AUTOINCREMENT, email VARCHAR, password VARCHAR, address VARCHAR, lat VARCHAR, lang VARCHAR, comments VARCHAR, status TINYINT(0))");
        db.execSQL("CREATE TABLE reporttbl (ID INTEGER PRIMARY KEY AUTOINCREMENT, address VARCHAR,lat VARCHAR, lang VARCHAR, comments VARCHAR, status TINYINT(0))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(sqLiteDatabase);

    }

    public boolean addReport(String address, String lat, String lang, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("address", address);
        contentValues.put("lat", lat);
        contentValues.put("lang", lang);
        contentValues.put("comments", comments);
        contentValues.put("status", 0);

        db.rawQuery("SELECT * FROM reporttbl WHERE lat=?", new String[]{lat});
        long res = db.insert("reporttbl", null, contentValues);
        db.close();

        if(res > 0)
        {
            return true;
        }
        else {
            return false;
        }

    }

    public boolean addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put(COL_4, 0);

        db.rawQuery("SELECT * FROM usertbl WHERE email=?", new String[]{email});
        long res = db.insert("usertbl", null, contentValues);
        db.close();

        if(res > 0)
        {
            return true;
        }
        else {
            return false;
        }


    }

    public boolean updateReport() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", 1);
        db.rawQuery("SELECT * FROM reporttbl WHERE status=0",  null);
        long res = db.update("reporttbl", contentValues, "status=0", null);
        db.close();

        if(res > 0)
        {
            return true;
        }
        else {
            return false;
        }


    }

    public boolean updateUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", username);
        contentValues.put("password", password);
        contentValues.put(COL_4, 0);
        db.rawQuery("SELECT * FROM usertbl WHERE username=?", new String[]{username});
        long res = db.update("usertbl", contentValues, "email=?", new String[]{username});
        db.close();

        if(res > 0)
        {
            return true;
        }
        else {
            return false;
        }


    }

    public Cursor getdata3()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from usertbl where status=1", null);
        return cursor;

    }
    public boolean updateData(String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);


        long res = db.update("usertbl", contentValues, "status=1",null);
        if(res > 0)
        {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String[] columns = {COL_1};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?" + " and " + COL_3 + "=?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        contentValues.put(COL_4, 1);


        if (count > 0) {
            DB.update("usertbl", contentValues, "email=?", new String[]{email});
            return true;
        } else {
            return false;
        }
    }

    public Cursor getLoc ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from reporttbl WHERE status = 1", null);
        return cursor;
    }

    public Cursor getdata ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from usertbl WHERE status = 1", null);
        return cursor;
    }
    public Cursor getData (String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select name from usertbl WHERE status = 1", null);
        return cursor;
    }

    public boolean checkUser2() {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = DB.rawQuery("Select * from usertbl WHERE status = 1", null);
        contentValues.put(COL_4, 0);
        int count = cursor.getCount();
        DB.update("usertbl", contentValues, "status = 1",null);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    public String getData3()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] columns = {COL_2, COL_4};
        Cursor cursor =DB.query(DatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String name =cursor.getString(cursor.getColumnIndex(COL_2));
            String fname =cursor.getString(cursor.getColumnIndex(COL_4));
            buffer.append(name + "   " + fname +" \n");
        }
        return buffer.toString();
    }


}
