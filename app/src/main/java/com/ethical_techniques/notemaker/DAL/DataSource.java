package com.ethical_techniques.notemaker.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ethical_techniques.notemaker.model.Category;
import com.ethical_techniques.notemaker.model.Note;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * The type Data source.
 */
public class DataSource {

    private final String TAG = this.getClass().getSimpleName();
    private SQLiteDatabase database;
    private DBHelper dbHelper;


    /**
     * Instantiates a new Data source.
     *
     * @param context the context
     */
    protected DataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * Open.
     *
     * @throws SQLException the sql exception
     */
    protected void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

    }

    /**
     * Close.
     */
    protected void close() {
        dbHelper.close();
    }

    /* ************************************** Note DataSource Methods ***************************************  */

    /**
     * Insert note boolean.
     *
     * @param note inserts new note into the database.
     * @return true for success and false for failure to insert note obj
     */
    protected boolean insertNote(Note note) {
        boolean didSucceed = false;
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.NOTE_NAME, note.getNoteName());
            contentValues.put(DBHelper.NOTE_CONTENT, note.getContent());
            contentValues.put(DBHelper.DATE, String.valueOf(note.getDateCreated().getTimeInMillis()));
            contentValues.put(DBHelper.PRIORITY, note.getPRIORITY_LEVEL());

            contentValues.put(DBHelper.REF_CATEGORY_ID, note.getCategory());

            didSucceed = database.insert(DBHelper.NOTE_TABLE, null, contentValues) > 0;


        } catch (Exception ex) {

            Log.e(TAG, "@ insertNote() ", ex);
            ex.printStackTrace();

        }
        return didSucceed;
    }

    /**
     * Update note boolean.
     *
     * @param note the note object to persist
     * @return true for success and false for failure to update
     */
    protected boolean updateNote(Note note) {

        boolean didSucceed = false;

        try {
            long rowId = (long) note.getNoteID();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.NOTE_NAME, note.getNoteName());
            contentValues.put(DBHelper
                    .NOTE_CONTENT, note.getContent());
            contentValues.put(DBHelper.DATE, String.valueOf(note.getDateCreated().getTimeInMillis()));
            contentValues.put(DBHelper.PRIORITY, note.getPRIORITY_LEVEL());
            contentValues.put(DBHelper.REF_CATEGORY_ID, note.getCategory());

            didSucceed = database.update(DBHelper.NOTE_TABLE, contentValues, DBHelper.ID + "=" + rowId, null) > 0;

        } catch (Exception ex) {

            Log.e(TAG, "@ updateNote()", ex);
        }
        return didSucceed;
    }

    /**
     * Gets last note id.
     *
     * @return the ID aka.'noteID', of the last note to be inserted into the database
     */
    protected int getLastNoteId() {
        int lastId = -1;
        try {
            String query = "Select MAX(" + DBHelper.ID + ") from " + DBHelper.NOTE_TABLE;
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();

        } catch (Exception e) {
            lastId = -1;
            Log.e(TAG, "@ getLastNoteId()");
        }
        return lastId;
    }

    /**
     * Gets notes.
     *
     * @param sortField the sort field
     * @param sortOrder the sort order
     * @return the notes
     */
    protected ArrayList<Note> getNotes(String sortField, String sortOrder) {
        ArrayList<Note> notes = new ArrayList<>();

        try {
            String noteQuery = "SELECT * FROM " + DBHelper.NOTE_TABLE + " ORDER BY " + sortField + " " + sortOrder;
            Cursor noteCursor = database.rawQuery(noteQuery, null);
            Note note;
            int category;
            noteCursor.moveToFirst(); //Move to first

            while (!noteCursor.isAfterLast()) {

                note = new Note();
                int noteID = noteCursor.getInt(0);
                note.setNoteID(noteID);
                note.setNoteName(noteCursor.getString(1));
                note.setNoteContent(noteCursor.getString(2));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(noteCursor.getString(3)));
                note.setDateCreated(calendar);
                note.setPRIORITY_LEVEL(noteCursor.getString(4));
                note.setCategory(noteCursor.getInt(5));

                notes.add(note);
                noteCursor.moveToNext();
            }
            noteCursor.close();

        } catch (Exception e) {
            notes = new ArrayList<>();
            Log.e(TAG, "@ getNote() ", e);


        }
        return notes;

    }

    /**
     * Gets specific note.
     *
     * @param noteID the note id
     * @return the specific note
     */
    protected Note getSpecificNote(int noteID) {
        Note note = new Note();

        String query = "SELECT * FROM " + DBHelper.NOTE_TABLE + " WHERE " + DBHelper.ID + "=" + noteID;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            note.setNoteID(cursor.getInt(0));
            note.setNoteName(cursor.getString(1));
            note.setNoteContent(cursor.getString(2));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(cursor.getString(3)));
            note.setDateCreated(calendar);
            note.setPRIORITY_LEVEL(cursor.getString(4));
            note.setCategory(cursor.getInt(5));

            cursor.close();
        }

        return note;

    }

    /* ************************************** Category DataSource Methods ***************************************  */

    /**
     * Gets specific category.
     *
     * @param categoryId the category id
     * @return the specific category
     */
    protected Category getSpecificCategory(int categoryId) {
        Category category = new Category();

        String query = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.ID + "=" + categoryId;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setColor(cursor.getInt(2));

            cursor.close();
        }

        return category;

    }

    /**
     * Gets categories.
     *
     * @return the categories
     */
    protected List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        try {
            String categoryQuery = "SELECT * FROM " + DBHelper.CATEGORY_TABLE;
            Cursor catCursor = database.rawQuery(categoryQuery, null);
            catCursor.moveToFirst();

            while (!catCursor.isAfterLast()) {
                Category category = new Category();
                category.setId(catCursor.getInt(0));
                category.setName(catCursor.getString(1));
                category.setColor(catCursor.getInt(2));

                categories.add(category);
                catCursor.moveToNext();
            }

            catCursor.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Exception @DataSource.getCategories()");
        }
        return categories;
    }

    protected Category getCategoryByName(String name) {
        Category category = new Category();

        String query = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.CATEGORY_NAME + "=" + name;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setColor(cursor.getInt(2));

            cursor.close();
        }

        return category;

    }

    /**
     * Delete Note.
     * @param noteId the id of the note to delete
     */
    protected void delete(int noteId) throws Exception {
        boolean didDelete = false;
        didDelete = database.delete(DBHelper.NOTE_TABLE, DBHelper.ID + "=" + noteId, null) > 0;
        if (!didDelete) {
            throw new Exception("Something went wrong while trying to delete the note from the database :(");
        }
    }

    /**
     * DataSource.delete(Category category)
     * <p>
     * Deletes the Category from persistent memory
     *
     * @param category to be deleted
     * @return true if it was a success
     * @throws Exception while trying to delete the Category
     */
    protected boolean delete(Category category) throws Exception {
        boolean deleted = false;

        String query = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.ID + "=" + category.getId();

        try {
            deleted = database.delete(DBHelper.CATEGORY_TABLE, DBHelper.ID + "=" + category.getId(), null) > 0;

        } catch (Exception e) {
            Log.e(TAG, "Exception @ delete(Category c) where Category ID is "
                    + category.getId() + " and Category Name is " + category.getName(), e.getCause());
        }
        return deleted;
    }

    /**
     * Insert category boolean.
     *
     * @param category the category
     * @return the boolean
     */
    public boolean insertCategory(Category category) {
        boolean didSucceed = false;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.CATEGORY_NAME, category.getName());
            contentValues.put(DBHelper.CATEGORY_COLOR_INT, category.getColor());

            didSucceed = database.insert(DBHelper.CATEGORY_TABLE, null, contentValues) > 0;

        } catch (Exception ex) {

            Log.e(TAG, "@ DataSource.insertCategory(Category c) ", ex);
            ex.printStackTrace();

        }
        return didSucceed;

    }

    /**
     * Update Category in DB.
     *
     * @param category the category
     * @return the boolean
     */
    protected boolean update(Category category) {

        boolean didSucceed = false;

        try {
            int categoryId = category.getId(); // Get the ID
            ContentValues contentValues = new ContentValues();

            contentValues.put(DBHelper.CATEGORY_NAME, category.getName());
            contentValues.put(DBHelper.CATEGORY_COLOR_INT, category.getColor());

            didSucceed = database.update(DBHelper.CATEGORY_TABLE, contentValues, DBHelper.ID + "=" + categoryId, null) > 0;

        } catch (Exception ex) {

            Log.e(TAG, "@ DataSource.updateNote(Category c)", ex);
        }
        return didSucceed;
    }

    /**
     * Gets last note id.
     *
     * @return the ID aka.'noteID', of the last note to be inserted into the database
     */
    protected int getLastCategoryId() {
        int id = Category.NONE;
        try {
            String query = "Select MAX(" + DBHelper.ID + ") from " + DBHelper.CATEGORY_TABLE;
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            id = cursor.getInt(0);
            cursor.close();

        } catch (Exception e) {
            id = Category.NONE;
            Log.e(TAG, "@ getLastCategoryId()");
        }
        return id;
    }

}