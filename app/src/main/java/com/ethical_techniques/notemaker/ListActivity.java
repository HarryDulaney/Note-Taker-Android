package com.ethical_techniques.notemaker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmStore;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;


import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.ethical_techniques.notemaker.model.Note;

import java.util.ArrayList;

import static android.view.View.*;


public class ListActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();
    Boolean isDeleting = false;
    Boolean isExpanded = false;
    ArrayList<Note> notes;
    NoteAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = findViewById(R.id.action_bar_top);
        initToolBar();

        initItemClick();
        initDeleteButton();
    }

    private void initToolBar() {
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_bar_settings:
                        // User chose the "Settings" item, show the app settings UI...
                        Intent intent = new Intent(ListActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;

                    case R.id.action_bar_list:
                        return true;

                    case R.id.action_bar_new:
                        Intent intent2 = new Intent(ListActivity.this, NoteActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        return true;

                    default:
                        return false;

                }

            });

        }
    }

    /**
     * Loads user preferences from SharedPreferences.
     * Connects to the DB, if no notes exist it creates an Intent to open
     * NoteActivity.
     */
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        //Retrieve Sort By from sp
        String sortBy = sp.getString("sort.by.preference", getString(R.string.SORT_BY_DATE));

        //Retrieves Sort Order from sp
        String sortOrder = sp.getString("sort.order.preference", getString(R.string.SORT_ORDER_LOW_TO_HIGH));

        //Instantiate 'DBO' database object
        NoteDataSource nds = new NoteDataSource(this);

        try {
            nds.open();                     //Open connection to DB
            notes = nds.getNotes(sortBy, sortOrder);         // Retrieve ArrayList of all note obj's from the DB
            nds.close();                    //Close connection to the DB

            /*If the DB returned some notes in the ArrayList, initialize ListView and set the adapter
             * else if the notes ArrayList is empty, start the NoteActivity
             */
            if (notes.size() > 0) {
                ListView listview = findViewById(R.id.listViewNotes);
                adapter = new NoteAdapter(this, notes);
                listview.setAdapter(adapter);
            } else {
                Intent intent = new Intent(ListActivity.this, NoteActivity.class);
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onResume, inspect the NoteDataSource. ");
            Toast.makeText(this, "Error retrieving notes, please reload. ", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Defines behavior for event that user long clicks on a Note in List
     * If the delete button is not active an Intent is created which stores the noteID in
     * Extra which we will use from to tell the NoteActivity which note to open
     */
    private void initItemClick() {
        ListView listview = findViewById(R.id.listViewNotes);

        listview.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {

            Note selectedNote = notes.get(position);
            Intent intent = new Intent(ListActivity.this, NoteActivity.class);
            intent.putExtra("noteid", selectedNote.getNoteID());
            startActivity(intent);
            return true;
        });

        listview.setOnItemClickListener((AdapterView<?> parent, View itemClicked, int position, long id) -> {
            Note selectedNote = notes.get(position);
            if (isDeleting) {
                adapter.showDelete(position, itemClicked, ListActivity.this, selectedNote);

            } else if (!selectedNote.getExpanded()) {
                adapter.showExpandedNote(itemClicked);
                selectedNote.setExpanded(true);

            } else {
                adapter.closeExpandedNote(itemClicked);
                selectedNote.setExpanded(false);

            }

        });
    }

    /**
     * Sets the event behavior for the toolbar DELETE button
     * When clicked it
     */
    private void initDeleteButton() {
        final Button deleteButton = findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener((View v) -> {

            if (isDeleting) {
                deleteButton.setText("Delete");
                isDeleting = false;
                adapter.notifyDataSetChanged();

            } else {
                deleteButton.setText("Done Deleting");
                isDeleting = true;
            }
        });
    }

}
