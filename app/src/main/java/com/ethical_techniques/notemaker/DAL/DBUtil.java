package com.ethical_techniques.notemaker.DAL;

import android.content.Context;
import android.provider.ContactsContract;

import com.ethical_techniques.notemaker.ListActivity;
import com.ethical_techniques.notemaker.model.Category;
import com.ethical_techniques.notemaker.model.Note;

import java.sql.SQLException;
import java.util.List;

/**
 * The type Db util.
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
     * @return list of all Category objects
     * @throws SQLException the sql exception
     */
    public static List<Category> getCategories(Context context) throws SQLException {
        List<Category> categories;
        DataSource ds = new DataSource(context);
        ds.open();
        categories = ds.getCategories();
        ds.close();
        return categories;
    }

    /**
     * Find category category.
     *
     * @param context the context
     * @param id      the id
     * @return the category
     * @throws SQLException the sql exception
     */
    public static Category findCategory(Context context, int id) throws SQLException {
        DataSource ds = new DataSource(context);
        ds.open();
        Category category = ds.getSpecificCategory(id);
        ds.close();

        return category;

    }

    private static Category findCategory(Context context, String name) throws SQLException {
        DataSource ds = new DataSource(context);
        ds.open();
        Category category = ds.getCategoryByName(name);
        ds.close();
        return category;
    }

    /**
     * Save category boolean.
     *
     * @param context  the context
     * @param category the category
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean saveCategory(Context context, Category category) throws SQLException {
        DataSource ds = new DataSource(context);
        boolean wasSuccess = false;
        ds.open();
        if (category.getId() == -2) {
            Category checkExists = findCategory(context, category.getName());
            if (checkExists.getId() != -2) { //Category name clashes with existing Category;
                //TODO: throw new DataNameClashException();
            } else {
                wasSuccess = ds.insertCategory(category); //Insert the new Category
            }
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
}

