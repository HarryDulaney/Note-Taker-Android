package com.ethical_techniques.notemaker.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

/**
 * The type Db helper.
 *
 *
 * @author Harry Dulaney
 *
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NOTES_DATABASE.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * The constant NOTE_TABLE.
     */
    public static final String NOTE_TABLE = "note";
    /**
     * The constant ID.
     */
    public static final String ID = "_id";
    /**
     * The constant NOTE_NAME.
     */
    public static final String NOTE_NAME = "notename";
    /**
     * The constant NOTE_CONTENT.
     */
    public static final String NOTE_CONTENT = "notecontent";
    /**
     * The constant REF_CATEGORY_ID.
     */
    public static final String REF_CATEGORY_ID = "notecategory";
    /**
     * The constant DATE.
     */
    public static final String DATE = "datecreated";
    /**
     * The constant PRIORITY.
     */
    public static final String PRIORITY = "priority";

    /**
     * The constant CATEGORY_TABLE.
     */
    public static final String CATEGORY_TABLE = "category";
    /**
     * The constant CATEGORY_NAME.
     */
    public static final String CATEGORY_NAME = "category_name";
    /**
     * The constant CATEGORY_COLOR_INT.
     */
    public static final String CATEGORY_COLOR_INT = "category_color_id";

    private static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE " + CATEGORY_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CATEGORY_NAME + " TEXT NOT NULL, " + CATEGORY_COLOR_INT + " INTEGER)";

    /**
     * A Note that isn't explicitly assigned a NoteCategory remains assigned to the default noteCategory i.e.("All") and
     * is grouped with other Notes of this NoteCategory util reassigned.
     *
     * The Note holds a reference INTEGER to the _id of the NoteCategory of which it was assigned. This dependency is implemented
     * programmatically instead of through DB constraints
     */
    private static final String CREATE_TABLE_NOTE =
            "CREATE TABLE " + NOTE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NOTE_NAME + " TEXT NOT NULL, " + NOTE_CONTENT + " TEXT, " + DATE + " TEXT, " + PRIORITY + " TEXT, "
                    + REF_CATEGORY_ID + " INTEGER)";


    /**
     * Instantiates a new Db helper.
     *
     * @param context the context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_NOTE);
        database.execSQL(CREATE_TABLE_CATEGORY);
        database.insert(CATEGORY_TABLE,null,DataSource.getDefaultCategory());
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