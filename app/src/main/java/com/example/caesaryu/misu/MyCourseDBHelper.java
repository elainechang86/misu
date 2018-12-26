package com.example.caesaryu.misu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyCourseDBHelper extends SQLiteOpenHelper {
    private static final String name = "misuDB.db";
    private static final int version = 1;
    String tableName;

    MyCourseDBHelper(Context context, String tablename) {
        super(context, name, null, version);
        tableName = tablename;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create TABLE IF NOT EXISTS " + tableName + "" +
                "(id integer PRIMARY KEY AUTOINCREMENT," +
                "name text NO NULL UNIQUE," +
                "teacher text NO NULL," +
                "credit integer NO NULL," +
                "time text NO NULL," +
                "classroom text NO NULL," +
                "color text NO NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }
    public static void resetCourseDB(SQLiteDatabase db) {
        Cursor c = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type IS 'table'" +
                        " AND name NOT IN ('sqlite_master', 'sqlite_sequence')",
                null
        );
        if (c.moveToFirst()) {
            do {
                Log.d("TEST", "dropAllUserTables: 0" + c.getString(c.getColumnIndex("name")));
                db.execSQL("DROP TABLE " + c.getString(c.getColumnIndex("name")));
            } while (c.moveToNext());
        }
    }

}
