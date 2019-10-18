package com.ethical_techniques.notemaker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;


public class NoteDataSource {

    private final String TAG = this.getClass().getSimpleName();

    private SQLiteDatabase database;
    private DBHelper dbHelper;


    NoteDataSource(Context context){
        dbHelper = new DBHelper(context);

    }


    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

    }

    public void close() {
        dbHelper.close();
    }


    /**
     * @param note inserts new note into the database.
     * @return true for success and false for failure to insert note obj
     */
    public boolean insertNote(Note note) {
        boolean didSucceed = false;
        try {

            ContentValues initialValues = new ContentValues();

            initialValues.put("notename", note.getNoteName());
            initialValues.put("subject", note.getSubject());
            initialValues.put("noteContent",note.getContent());



            didSucceed = database.insert("note", null, initialValues) > 0;

        } catch (Exception ex) {

           Log.e(TAG,String.valueOf(ex));
           ex.printStackTrace();

        }
        return didSucceed;
    }

    /**
     * @param note update it's attributes in the database
     * @return true for success and false for failure to update
     */
    boolean updateNote(Note note) {

        boolean didSucceed = false;

        try {
            long rowId = (long) note.getNoteID();
            ContentValues updateValues = new ContentValues();

            updateValues.put("notename",note.getNoteName());
            updateValues.put("subject", note.getSubject());
            updateValues.put("notecontent",note.getContent());

            didSucceed = database.update("note", updateValues, "_id=" + rowId, null) > 0;

        } catch (Exception ex) {

            Log.e(TAG,String.valueOf(ex));
        }
        return didSucceed;
    }

    int getLastNoteId() {
        int lastId = -1;
        try {
            String query = "Select MAX(_id) from note";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();

        } catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }


    public ArrayList<String> getNoteName() {

        ArrayList<String> noteNames = new ArrayList<>();

        try {
            String query = "Select notename from note";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                noteNames.add(cursor.getString(0));
                cursor.moveToNext();

            }
            cursor.close();

        } catch (Exception ex) {
            noteNames = new ArrayList<>();
        }

        return noteNames;
    }

    public ArrayList<Note> getNotes(String sortField, String sortOrder) {
        ArrayList<Note> notes = new ArrayList<>();

        try {
            String query = "SELECT * FROM note ORDER BY " + sortField + " " + sortOrder;

            Cursor cursor = database.rawQuery(query, null);

            Note newNote;
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                newNote = new Note();

                 newNote.setNoteID(cursor.getInt(0));
                 newNote.setNoteName(cursor.getString(1));
                 newNote.setSubject(cursor.getString(2));
                 newNote.setNoteContent(cursor.getString(3));

                notes.add(newNote);
                cursor.moveToNext();
            }
            cursor.close();


        } catch (Exception e) {

            notes = new ArrayList<>();

        }
        return notes;

    }
    Note getSpecificNote(int noteID){
        Note note = new Note();
        String query = "SELECT * FROM note WHERE _id =" + noteID;
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst()) {

            note.setNoteID(cursor.getInt(0));
            note.setNoteName(cursor.getString(1));
            note.setSubject(cursor.getString(2));
            note.setNoteContent(cursor.getString(3));



            cursor.close();

        }

        return note;

    }
    boolean delete(int noteID) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("note", "_id=" + noteID, null) > 0;
        } catch (Exception e) {
            //Do nothing -return value already set to false
        }
        return didDelete;
    }

}
