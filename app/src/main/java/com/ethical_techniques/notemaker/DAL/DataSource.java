package com.ethical_techniques.notemaker.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.NoteCategory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * The type Data source.
 *
 * @author Harry Dulaney
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
     * @return defCategory, the ContentValues to persist for the fallback/ default NoteCategory
     */
    public static ContentValues getDefaultCategory() {
        ContentValues defCategory = new ContentValues();
        defCategory.put(DBHelper.CATEGORY_NAME, NoteCategory.MAIN_NAME);
        defCategory.put(DBHelper.CATEGORY_COLOR_INT, NoteCategory.MAIN_COLOR);
        return defCategory;
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

            contentValues.put(DBHelper.REF_CATEGORY_ID, note.getNoteCategory().getId());

            didSucceed = database.insert(DBHelper.NOTE_TABLE, null, contentValues) > 0;


        } catch (Exception ex) {

            Log.e(TAG, "@ insertNote() ", ex);
            ex.printStackTrace();

        }
        return didSucceed;
    }

    protected boolean checkNotes() {
        int count = 0;
        try {
            String query = "SELECT _id FROM " + DBHelper.NOTE_TABLE;
            Cursor noteCursor = database.rawQuery(query, null);
            count = noteCursor.getCount();
            noteCursor.close();

        } catch (Exception e) {
            Log.e(TAG, "@ checkNotes() ", e);
        }
        return count > 0;

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
            contentValues.put(DBHelper.REF_CATEGORY_ID, note.getNoteCategory().getId());

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

                NoteCategory noteCategory = getSpecificCategory(noteCursor.getInt(5));
                note.setNoteCategory(noteCategory);

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

            NoteCategory noteCategory = getSpecificCategory(cursor.getInt(5));
            note.setNoteCategory(noteCategory);

            cursor.close();
        }

        return note;

    }

    /* ************************************** NoteCategory DataSource Methods ***************************************  */

    /**
     * Gets specific noteCategory.
     *
     * @param categoryId the noteCategory id
     * @return the specific noteCategory
     */
    protected NoteCategory getSpecificCategory(int categoryId) {
        NoteCategory noteCategory = new NoteCategory();

        String query = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.ID + "=" + categoryId;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            noteCategory.setId(cursor.getInt(0));
            noteCategory.setName(cursor.getString(1));
            noteCategory.setColor(cursor.getInt(2));

            cursor.close();
        }

        return noteCategory;

    }

    /**
     * Gets categories.
     *
     * @return the categories
     */
    protected List<NoteCategory> getCategories() {
        List<NoteCategory> categories = new ArrayList<>();
        try {
            String categoryQuery = "SELECT * FROM " + DBHelper.CATEGORY_TABLE;
            Cursor catCursor = database.rawQuery(categoryQuery, null);
            catCursor.moveToFirst();

            while (!catCursor.isAfterLast()) {
                NoteCategory noteCategory = new NoteCategory();
                noteCategory.setId(catCursor.getInt(0));
                noteCategory.setName(catCursor.getString(1));
                noteCategory.setColor(catCursor.getInt(2));

                categories.add(noteCategory);
                catCursor.moveToNext();
            }

            catCursor.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Exception @DataSource.getCategories()");
        }
        if (categories.size() == 0) {
            insertCategory(NoteCategory.getMain());
            return getCategories();
        }

        return categories;
    }

    protected NoteCategory getCategoryByName(String name) {
        NoteCategory noteCategory = new NoteCategory();

        String query = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.CATEGORY_NAME + " = " + name;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            noteCategory.setId(cursor.getInt(0));
            noteCategory.setName(cursor.getString(1));
            noteCategory.setColor(cursor.getInt(2));

            cursor.close();
        }

        return noteCategory;

    }

    /**
     * Delete Note.
     *
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
     * DataSource.delete(NoteCategory noteCategory)
     * <p>
     * Deletes the NoteCategory from persistent memory
     *
     * @param noteCategory to be deleted
     * @return true if it was a success
     * @throws Exception while trying to delete the NoteCategory
     */
    protected boolean delete(NoteCategory noteCategory) throws Exception {
        boolean deleted = false;

        String query = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.ID + "=" + noteCategory.getId();

        try {
            deleted = database.delete(DBHelper.CATEGORY_TABLE, DBHelper.ID + "=" + noteCategory.getId(), null) > 0;

        } catch (Exception e) {
            Log.e(TAG, "Exception @ delete(NoteCategory c) where NoteCategory ID is "
                    + noteCategory.getId() + " and NoteCategory Name is " + noteCategory.getName(), e.getCause());
        }
        return deleted;
    }

    /**
     * Insert noteCategory boolean.
     *
     * @param noteCategory the noteCategory
     * @return the boolean
     */
    public boolean insertCategory(NoteCategory noteCategory) {
        boolean didSucceed = false;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.CATEGORY_NAME, noteCategory.getName());
            contentValues.put(DBHelper.CATEGORY_COLOR_INT, noteCategory.getColor());

            didSucceed = database.insert(DBHelper.CATEGORY_TABLE, null, contentValues) > 0;

        } catch (Exception ex) {

            Log.e(TAG, "@ DataSource.insertCategory(NoteCategory c) ", ex);
            ex.printStackTrace();

        }
        return didSucceed;

    }

    /**
     * Update NoteCategory in DB.
     *
     * @param noteCategory the noteCategory
     * @return the boolean
     */
    protected boolean update(NoteCategory noteCategory) {

        boolean didSucceed = false;

        try {
            int categoryId = noteCategory.getId(); // Get the ID
            ContentValues contentValues = new ContentValues();

            contentValues.put(DBHelper.CATEGORY_NAME, noteCategory.getName());
            contentValues.put(DBHelper.CATEGORY_COLOR_INT, noteCategory.getColor());

            didSucceed = database.update(DBHelper.CATEGORY_TABLE, contentValues, DBHelper.ID + "=" + categoryId, null) > 0;

        } catch (Exception ex) {

            Log.e(TAG, "@ DataSource.updateNote(NoteCategory c)", ex);
        }
        return didSucceed;
    }

    /**
     * Gets last note category id to set a newly created category in real time with its correct id;
     *
     * @return the ID aka.'categoryId', of the last noteCategory to be inserted into the database, the highest autoIncremented
     * ID assigned to a noteCategory;
     */
    protected int getLastCategoryId() {
        int id;
        try {
            String query = "Select MAX(" + DBHelper.ID + ") from " + DBHelper.CATEGORY_TABLE;
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            id = cursor.getInt(0);
            cursor.close();

        } catch (Exception e) {
            id = NoteCategory.MAIN_ID;
            Log.e(TAG, "@ getLastCategoryId()");
        }
        return id;
    }

}