package com.ethical_techniques.notemaker;

import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.ethical_techniques.notemaker.DAL.DataSource;
import com.ethical_techniques.notemaker.note.Note;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Note}.
 */
public class NoteRecycleAdapter extends RecyclerView.Adapter<NoteRecycleAdapter.ViewHolder> {

    private List<Note> notes;
    private DataSource dataSource;


    public NoteRecycleAdapter(List<Note> items) {
        notes = items;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.note = notes.get(position);
        holder.title.setText(notes.get(position).getNoteName());
        holder.date.setText(DateFormat.format("MM/dd/yyyy", notes.get(position).getDateCreated()));

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).getNoteID();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public Note note;
        public final TextView title;
        public final TextView date;
        public final ImageButton deleteButton;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            title = v.findViewById(R.id.textNoteTitle);
            date = v.findViewById(R.id.dateCreatedText);
            deleteButton = v.findViewById(R.id.buttonDeleteNote);

        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'" + " '" + date.getText() + "'";
        }


    }
}