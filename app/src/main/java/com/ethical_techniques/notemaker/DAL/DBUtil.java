package com.ethical_techniques.notemaker.DAL;

import android.content.Context;

import com.ethical_techniques.notemaker.note.Category;
import com.ethical_techniques.notemaker.note.Note;

import java.sql.SQLException;
import java.util.List;

public class DBUtil {

    private static final String TAG = "DBUtil.class";

    private DBUtil() {
        throw new IllegalArgumentException("Cannot instantiate DBUtil");
    }

    public static List<Note> findNotes(Context context, String sortBy, String sortOrder) throws Exception {
        List<Note> notes;
        DataSource nds = new DataSource(context);
        nds.open();     //Open connection to DB
        notes = nds.getNotes(sortBy, sortOrder);        // Retrieve ArrayList of all note obj's from the DB
        nds.close();  //Close connection to the DB

        return notes;

    }

    /**
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
     * @param context the Context from the Activity calling this method
     * @param note    the note to save
     * @return was successful
     * @throws Exception trying to open the the datasource
     */
    public static boolean saveNote(Context context, Note note) throws Exception {
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
     * @param context of the calling Activity
     * @return list of all Category objects
     */
    public static List<Category> getCategories(Context context) throws SQLException {
        List<Category> categories;
        DataSource ds = new DataSource(context);
        ds.open();
        categories = ds.getCategories();
        ds.close();
        return categories;
    }

    public static Category findCategory(Context context, int id) throws SQLException {
        DataSource ds = new DataSource(context);
        ds.open();
        Category category = ds.getSpecificCategory(id);
        ds.close();

        return category;

    }

    public static boolean saveCategory(Context context, Category category) throws SQLException {
        DataSource ds = new DataSource(context);
        boolean wasSuccess = false;
        ds.open();
        if (category.getId() == -5) {
            wasSuccess = ds.insertCategory(category);

            if (wasSuccess) {
                int id = ds.getLastCategoryId();
                category.setId(id);
            }
        } else {
            wasSuccess = ds.update(category);
        }

        ds.close();
        return wasSuccess;
    }

}

