package com.ethical_techniques.notemaker.DAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NOTES_DATABASE.db";
    private static final int DATABASE_VERSION = 5;

    public static final String NOTE_TABLE = "note";
    public static final String ID = "_id";
    public static final String NOTE_NAME = "notename";
    public static final String NOTE_CONTENT = "notecontent";
    public static final String REF_CATEGORY_ID = "notecategory";
    public static final String DATE = "datecreated";
    public static final String PRIORITY = "priority";

    public static final String CATEGORY_TABLE = "category";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_COLOR_INT = "category_color_id";

    private static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE " + CATEGORY_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CATEGORY_NAME + " TEXT NOT NULL, " + CATEGORY_COLOR_INT + " INTEGER)";

    /**
     * A Note that isn't explicitly assigned a Category remains assigned to the Dephault type Category and
     * is grouped with other Notes of this Category.
     *
     * The Note holds a reference INTEGER to the _id of the Category of which it was assigned. This dependency is implemented
     * programmatically instead of through DB constraints
     */
    private static final String CREATE_TABLE_NOTE =
            "CREATE TABLE " + NOTE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NOTE_NAME + " TEXT NOT NULL, " + NOTE_CONTENT + " TEXT, " + DATE + " TEXT, " + PRIORITY + " TEXT, "
                    + REF_CATEGORY_ID + " INTEGER)";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_NOTE);
        database.execSQL(CREATE_TABLE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will alter table data");

        db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);

        onCreate(db);


    }

}