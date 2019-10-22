package com.ethical_techniques.notemaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ListActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();
    Boolean isDeleting = false;
    Boolean isExpanded = false;
    ArrayList<Note> notes;
    NoteAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initItemClick();
        initAddNoteButton();
        initDeleteButton();
        initListButton();
        initNotesButton();
        initSettingsButton();
    }

    /**
     * Loads user preferences from SharedPreferences.
     * Connects to the DB, if no notes exist it creates an Intent to open
     * NoteActivity.
     */
    @Override
    public void onResume() {
        super.onResume();

        String sortBy = getSharedPreferences("NoteMakerPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "notename");

        String sortOrder = getSharedPreferences("NoteMakerPreferences",
                Context.MODE_PRIVATE).getString("sortorder","ASC");

        //Instantiate 'DBO' database object
        NoteDataSource nds = new NoteDataSource(this);

        try {
            nds.open();                     //Open connection to DB
            notes = nds.getNotes();         // Retrieve ArrayList of all note obj's from the DB
            nds.close();                    //Close connection to the DB

            /*If the DB returned some notes in the ArrayList, initialize ListView and set the adapter
            * else if the notes ArrayList is empty, start the NoteActivity
            */
            if(notes.size() > 0){
                ListView listview = findViewById(R.id.listViewNotes);
                adapter = new NoteAdapter(this,notes);
                listview.setAdapter(adapter);
            }else{
                Intent intent = new Intent(ListActivity.this,NoteActivity.class);
                startActivity(intent);
            }

        }catch (Exception e){
            Log.e(TAG,"Error in onResume, inspect the NoteDataSource. ");
            Toast.makeText(this,"Error retrieving notes, please reload. ",Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Defines behavior for event that user double clicks on a Note in List
     * If the delete button is not active an Intent is created which stores the noteID in
     * Extra which we will use from to tell the NoteActivity which note to open
     */
    private void initItemClick() {
        ListView listview = findViewById(R.id.listViewNotes);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Note selectedNote = notes.get(position);

                Intent intent = new Intent(ListActivity.this, NoteActivity.class);
                intent.putExtra("noteid", selectedNote.getNoteID());
                startActivity(intent);


                return false;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Note selectedNote = notes.get(position);
                if(isDeleting){
                    adapter.showDelete(position,itemClicked,ListActivity.this,selectedNote);

                }else if(!selectedNote.getExpanded()){
                    adapter.showExpandedNote(itemClicked);
                    selectedNote.setExpanded(true);

                }else{
                    adapter.closeExpandedNote(itemClicked);
                    selectedNote.setExpanded(false);

                }

            }
        });
    }

    /**
     * Opens the NoteActivity(Blank) when the user clicks on the 'New Note' Button
     */
    private void initAddNoteButton(){
        Button newContact = findViewById(R.id.buttonAdd);
        newContact.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this,NoteActivity.class);
                startActivity(intent);

            }
        });
    }

    /**
     * Sets the event behavior for the toolbar DELETE button
     * When clicked it
     */
    private void initDeleteButton(){
        final Button deleteButton = findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(isDeleting){
                    deleteButton.setText("Delete");
                    isDeleting = false;
                    adapter.notifyDataSetChanged();

                }else{
                    deleteButton.setText("Done Deleting");
                    isDeleting = true;
                }
            }
        });
    }

    private void initNotesButton() {
        ImageButton noteButton = findViewById(R.id.imageButtonNote);
        noteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, NoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

    }
    private void initListButton(){
        ImageButton listButton = findViewById(R.id.imageButtonList);
        listButton.setEnabled(false);

    }
    private void initSettingsButton(){
        ImageButton settingButton = findViewById(R.id.imageButtonSettings);
        settingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this,SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }


}
