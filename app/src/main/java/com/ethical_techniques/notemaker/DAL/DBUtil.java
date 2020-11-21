package com.ethical_techniques.notemaker.DAL;

import android.content.Context;
import android.util.Log;

import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.NoteCategory;

import java.sql.SQLException;
import java.util.List;

/**
 * The type Db util.
 *
 * @author Harry Dulaney
 */
public class DBUtil {

    private static final String TAG = "DBUtil.class";

    private DBUtil() {
        throw new IllegalArgumentException("Cannot instantiate DBUtil.class");
    }

    /**
     * Find notes list.
     *
     * @param context   the context
     * @param sortBy    the sort by
     * @param sortOrder the sort order
     * @return the list
     * @throws Exception the exception
     */
    public static List<Note> findNotes(Context context, String sortBy, String sortOrder) throws Exception {
        List<Note> notes;
        DataSource nds = new DataSource(context);
        nds.open();     //Open connection to DB
        notes = nds.getNotes(sortBy, sortOrder);        // Retrieve ArrayList of all note obj's from the DB
        nds.close();  //Close connection to the DB

        return notes;

    }
//
//    /**
//     * Find only notes in specific category.
//     *
//     * @param context   the context
//     * @param sortBy    the sort by
//     * @param sortOrder the sort order
//     * @return the list of notes in the category
//     * @throws Exception the exception
//     */
//    public static List<Note> findNotes(Context context, NoteCategory category, String sortBy, String sortOrder) throws Exception {
//        List<Note> notes;
//        DataSource nds = new DataSource(context);
//        nds.open();     //Open connection to DB
//        notes = nds.getNotes(sortBy, sortOrder);        // Retrieve ArrayList of all note obj's from the DB
//        nds.close();  //Close connection to the DB
//
//        return notes;
//
//    }

    /**
     * @return true if the user has notes in local persistent memory
     * @throws Exception the exception
     */
    public static boolean hasNotes(Context context) throws Exception {
        boolean has = Boolean.FALSE;
        DataSource nds = new DataSource(context);
        nds.open();
        has = nds.checkNotes();
        nds.close();
        return has;
    }


    /**
     * Find note note.
     *
     * @param context the Context from the Activity calling this method
     * @param noteId  the Note Id to look up
     * @return the Note
     * @throws Exception No Note matching the id was found
     */
    public static Note findNote(Context context, int noteId) throws Exception {
        DataSource nds = new DataSource(context);
        nds.open();
        Note note = nds.getSpecificNote(noteId);
        nds.close();

        if (note != null)
            return note;
        else
            throw new Exception("No saved notes exist that match the Note ID: " + noteId);
    }

    /**
     * Save note boolean.
     *
     * @param context the Context from the Activity calling this method
     * @param note    the note to save
     * @return was successful
     * @throws Exception trying to open the the datasource
     */
    public static boolean saveNote(Context context, Note note) throws Exception {
        if (note.getNoteID() == -2) {
            Log.i(TAG, "Skipped save Note because Note is the dummy for the Note List intro message");
            return true;
        }

        boolean wasSuccess = false;
        DataSource nds = new DataSource(context);
        nds.open();

        if (note.getNoteID() == -1) {
            wasSuccess = nds.insertNote(note); // Note Id is -1 means the Note does note exist in the database

            if (wasSuccess) {
                int newId = nds.getLastNoteId();
                note.setNoteID(newId);
            }

        } else {
            wasSuccess = nds.updateNote(note); // Operating on a note that is existing in the database

        }
        nds.close();
        return wasSuccess;
    }

    /**
     * Gets categories.
     *
     * @param context of the calling Activity
     * @return list of all NoteCategory objects
     * @throws SQLException the sql exception
     */
    public static List<NoteCategory> getCategories(Context context) throws SQLException {
        List<NoteCategory> categories;
        DataSource ds = new DataSource(context);
        ds.open();
        categories = ds.getCategories();
        ds.close();
        return categories;
    }

    /**
     * Find noteCategory noteCategory.
     *
     * @param context the context
     * @param id      the id
     * @return the noteCategory
     * @throws SQLException the sql exception
     */
    public static NoteCategory findCategory(Context context, int id) throws SQLException {
        DataSource ds = new DataSource(context);
        ds.open();
        NoteCategory noteCategory = ds.getSpecificCategory(id);
        ds.close();

        return noteCategory;

    }

    private static NoteCategory findCategory(Context context, String name) throws SQLException {
        DataSource ds = new DataSource(context);
        ds.open();
        NoteCategory noteCategory = ds.getCategoryByName(name);
        ds.close();
        return noteCategory;
    }

    /**
     * Save noteCategory boolean.
     *
     * @param context      the context
     * @param noteCategory the noteCategory
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean saveCategory(Context context, NoteCategory noteCategory) throws SQLException {
        DataSource ds = new DataSource(context);
        boolean wasSuccess = false;
        ds.open();
        wasSuccess = ds.insertCategory(noteCategory); //Insert the new NoteCategory
//        }
//        if (wasSuccess) {
//            int id = ds.getLastCategoryId();
//            noteCategory.setId(id);
//        } else {
//            wasSuccess = ds.update(noteCategory);
//        }

        ds.close();

        return wasSuccess;
    }

    /**
     * Delete note.
     *
     * @param context the context from which the method was called
     * @param noteId  the id of the note to delete
     */
    public static void deleteNote(Context context, int noteId) throws Exception {
        DataSource dataSource = new DataSource(context);
        dataSource.open();
        dataSource.delete(noteId);
        dataSource.close();
    }

    public static boolean updateCategory(Context context, NoteCategory noteCategory) throws SQLException {
        boolean success = false;
        DataSource ds = new DataSource(context);
        ds.open();
        success = ds.update(noteCategory);
        ds.close();
        return success;

    }
}

