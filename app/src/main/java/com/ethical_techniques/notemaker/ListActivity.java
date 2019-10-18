package com.ethical_techniques.notemaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {


    Boolean isDeleting = false;
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
    @Override
    public void onResume() {
        super.onResume();

        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "contactname");

        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortorder","ASC");


        NoteDataSource ds = new NoteDataSource(this);

        try {
            ds.open();
            notes = ds.getContacts(sortBy,sortOrder);
            ds.close();

            if(notes.size() > 0){
                ListView listview = (ListView) findViewById(R.id.listViewNotes);
                adapter = new NoteAdapter(this,notes);
                listview.setAdapter(adapter);
            }else{
                Intent intent = new Intent(ListActivity.this,NoteActivity.class);
                startActivity(intent);
            }

        }catch (Exception e){
            Toast.makeText(this,"Error retrieving contacts",Toast.LENGTH_LONG).show();

        }
    }

    private void initItemClick() {
        ListView listview = (ListView)findViewById(R.id.listViewNotes);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Note selectedNote = notes.get(position);
                if(isDeleting){
                    adapter.showDelete(position,itemClicked,ListActivity.this,selectedNote);
                }else {

                    Intent intent = new Intent(ListActivity.this, NoteActivity.class);
                    intent.putExtra("noteid", selectedNote.getNoteID());
                    startActivity(intent);
                }

            }
        });
    }
    private void initAddNoteButton(){
        Button newContact = (Button)findViewById(R.id.buttonAdd);
        newContact.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this,NoteActivity.class);
                startActivity(intent);

            }
        });
    }
    private void initDeleteButton(){
        final Button deleteButton = (Button)findViewById(R.id.buttonDelete);
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
