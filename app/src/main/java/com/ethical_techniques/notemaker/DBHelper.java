package com.ethical_techniques.notemaker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyNotes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_NOTE =
            "create table note (_id integer primary key autoincrement, "
                    + "notename text not null, subject text, notecontent text, "
                    + "datecreated text);";


    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_NOTE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will alter table data");
            db.execSQL("DROP TABLE IF EXISTS note");
        onCreate(db);


    }

}