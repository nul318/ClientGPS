package com.example.nul01.clientgps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블을 생성한다.
        // create table 테이블명 (컬럼명 타입 옵션);
        db.execSQL("CREATE TABLE member( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, pw TEXT);");
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        Log.i("DB", "insert success");
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public String PrintData(String name, String pw) {
        SQLiteDatabase db = getReadableDatabase();
        String str = "null";

        Cursor cursor = db.rawQuery("select * from member where name = '" + name + "'and pw = '" + pw + "';", null);
        while (cursor.moveToNext()) {
            str = cursor.getInt(0) + "";
        }

        return str;
    }
}