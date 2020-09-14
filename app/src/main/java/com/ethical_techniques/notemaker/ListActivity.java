package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.ethical_techniques.notemaker.model.Note;

import java.util.ArrayList;


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

    }

    private void initToolBar() {
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_bar_settings:
                            // User chose the "Settings" item, show the app settings UI...
                            Intent intent = new Intent(ListActivity.this, SettingsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ListActivity.this.startActivity(intent);
                            return true;

                        case R.id.action_bar_list:
                            return true;

                        case R.id.action_bar_new:
                            Intent intent2 = new Intent(ListActivity.this, NoteActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ListActivity.this.startActivity(intent2);
                            return true;

                        default:
                            return false;

                    }

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
        String sortOrder = sp.getString("sort.order.preference", getString(R.string.SORT_ORDER_HIGH_TO_LOW));

        //Instantiate 'DBO' database object
        NoteDataSource nds = new NoteDataSource(this);

        try {
            nds.open();                     //Open connection to DB
            notes = nds.getNotes(sortBy, sortOrder);         // Retrieve ArrayList of all note obj's from the DB
            nds.close();                    //Close connection to the DB

            /*If the DB returned some notes in the ArrayList, initialize ListView and set the adapter
             * else if the default Note Message is added to the ArrayList<Note>, start the NoteActivity (Create Note Screen)
             */
            if (!(notes.size() > 0)) {

                notes.add(Note.getDefaultNote());

            }

            ListView listView = findViewById(R.id.listViewNotes);
            adapter = new NoteAdapter(this, notes);
            listView.setAdapter(adapter);
            initItemClick(listView);


        } catch (Exception e) {
            Log.e(TAG, "Error in onResume, inspect the NoteDataSource. ");
            Toast.makeText(this, "Error retrieving notes, please reload. ", Toast.LENGTH_LONG).show();

        }
        initDeleteButton();
    }

    /**
     * Defines behavior for event that user long clicks on a Note in List
     * If the delete button is not active an Intent is created which stores the noteID in
     * Extra which we will use from to tell the NoteActivity which note to open
     */
    protected void initItemClick(ListView listView) {

        listView.setOnItemClickListener((parent, itemClicked, position, id) -> {
            Note selectedNote = (Note) listView.getItemAtPosition(position);
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Note selectedNote = notes.get(position);
                Intent intent = new Intent(ListActivity.this, NoteActivity.class);
                intent.putExtra("noteid", selectedNote.getNoteID());
                ListActivity.this.startActivity(intent);
                return true;
            }
        });


    }

    /**
     * Sets the event behavior for the toolbar DELETE button
     * When clicked it
     */
    protected void initDeleteButton() {
        final Button deleteButton = findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDeleting) {
                    deleteButton.setText("Delete");
                    isDeleting = false;
                    adapter.notifyDataSetChanged();

                } else {
                    deleteButton.setText("Done Deleting");
                    isDeleting = true;
                }
            }
        });
    }

}
