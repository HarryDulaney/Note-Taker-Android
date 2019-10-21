package com.ethical_techniques.notemaker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class NoteAdapter extends ArrayAdapter<Note> {

        private ArrayList<Note> items;
        private Context adapterContext;


        public NoteAdapter(Context context, ArrayList<Note> items){
            super(context, R.layout.list_item,items);
            adapterContext = context;
            this.items = items;
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent){
            View v = convertView;

            try {
                Note note = items.get(position);

                if (v ==  null){
                    LayoutInflater vi = (LayoutInflater)
                            adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    assert vi != null;
                    v = vi.inflate(R.layout.list_item, null);
                }

                TextView noteName = v.findViewById(R.id.textNoteName);
                TextView noteSubject = v.findViewById(R.id.textNoteSubject);
                TextView noteBody = v.findViewById(R.id.textNoteBody);
                TextView dateCreated = v.findViewById(R.id.dateCreatedText);

                noteName.setText(note.getNoteName());
                noteSubject.setText(note.getSubject());
                noteBody.setText(note.getContent());
                dateCreated.setText(DateFormat.format("MM/dd/yyyy", note.getDateCreated()));

                Button b = v.findViewById(R.id.buttonDeleteNote);
                b.setVisibility(View.INVISIBLE);


            }catch(Exception e3){
                e3.printStackTrace();
                e3.getCause();
            }
            return v;
        }
    public void showDelete(final int position, final View convertView,
                           final Context context, final Note note){
        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteNote);
        if(b.getVisibility() == View.INVISIBLE) {
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDelete(position,convertView,context);
                    items.remove(note);
                    deleteOption(note.getNoteID(),context);


                }
            });
        }else{
            hideDelete(position,convertView,context);
        }

    }

        private void deleteOption(int noteToDelete, Context context){
            NoteDataSource db = new NoteDataSource(context);
            try{
                db.open();
                db.delete(noteToDelete);
                db.close();
            }catch (Exception e){
                Toast.makeText(adapterContext,"Delete note failed", Toast.LENGTH_LONG).show();
            }
            this.notifyDataSetChanged();

        }
        public void hideDelete(int position,View convertView, Context context) {
            View v = convertView;
            final Button b = (Button) v.findViewById(R.id.buttonDeleteNote);
            b.setVisibility(View.INVISIBLE);
            b.setOnClickListener(null);
        }
    }

