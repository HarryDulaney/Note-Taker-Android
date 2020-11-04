package com.ethical_techniques.notemaker.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.ethical_techniques.notemaker.DAL.DataSource;
import com.ethical_techniques.notemaker.listeners.NoteClickListener;
import com.ethical_techniques.notemaker.R;
import com.ethical_techniques.notemaker.listeners.NoteLongClickListener;
import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.PRIORITY;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Note}.
 */
public class NoteRecycleAdapter extends RecyclerView.Adapter<NoteRecycleAdapter.ViewHolder> {

    private List<Note> notes;
    private DataSource dataSource;
    private NoteClickListener noteListener;
    private NoteClickListener deleteButtonListener;
    private NoteLongClickListener noteLongClickListener;

    private NoteClickListener priorityStarListener;


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
        final Note note = notes.get(position);
        holder.title.setText(note.getNoteName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        holder.date.setText(dateFormat.format(note.getDateCreated().getTime()));

        if (note.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString())) {
            holder.priorityStar.setColorFilter(R.color.colorPriorityHigh);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).getNoteID();

    }

    public void setPriorityStarListener(NoteClickListener priorityStarListener) {
        this.priorityStarListener = priorityStarListener;
    }

    public void setNoteClickListener(NoteClickListener noteListener) {
        this.noteListener = noteListener;

    }

    public void setDeleteButtonListener(NoteClickListener deleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener;
    }

    public void setNoteLongClickListener(NoteLongClickListener listener) {
        this.noteLongClickListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView title;
        public final TextView date;
        public final ImageButton deleteButton;
        public final ImageButton priorityStar;

        public ViewHolder(final View v) {
            super(v);
            mView = v;
            v.setOnClickListener(vw -> {
                if (noteListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        noteListener.clicked(vw, pos);
                    }
                }
            });

            v.setOnLongClickListener(view -> {
                if (noteLongClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        noteLongClickListener.clicked(view, position);
                        return true;
                    }
                }
                return false;
            });
            title = v.findViewById(R.id.textNoteTitle);
            date = v.findViewById(R.id.dateCreatedText);
            deleteButton = v.findViewById(R.id.buttonDeleteNote);
            deleteButton.setOnClickListener(v1 -> {
                if (deleteButtonListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteButtonListener.clicked(v1, position);
                    }
                }
            });
            priorityStar = v.findViewById(R.id.highPriorityStar);
            priorityStar.setOnClickListener(star -> {
                if (priorityStarListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        priorityStarListener.clicked(star, position);
                    }
                }
            });
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'" + " '" + date.getText() + "'";
        }
    }
}