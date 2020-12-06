package com.ethical_techniques.notemaker.frags;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ethical_techniques.notemaker.NoteActivity;
import com.ethical_techniques.notemaker.R;
import com.ethical_techniques.notemaker.model.Note;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class NotePopUpFragment extends DialogFragment {

    MaterialButton editButton;
    MaterialButton doneButton;

    MaterialTextView title;
    MaterialTextView date;
    MaterialTextView noteBody;

    private Note note;

    public static NotePopUpFragment newInstance(Note note) {

        return new NotePopUpFragment(note);
    }
    public NotePopUpFragment(){}


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private NotePopUpFragment(Note note) {
        this.note = note;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_pop_up_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editButton = view.findViewById(R.id.editButton);
        doneButton = view.findViewById(R.id.doneButton);
        title = view.findViewById(R.id.notepopup_title);
        date = view.findViewById(R.id.dateCreated);
        noteBody = view.findViewById(R.id.noteTextBody);

        editButton.setOnClickListener(v -> openNoteForEditing());
        doneButton.setOnClickListener(v -> dismiss());

        setNote();


    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        dialog.dismiss();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setNote() {
        if (note.getNoteName() != null)
            title.setText(note.getNoteName());
        if (note.getDateCreated() != null)
            date.setText(note.getDateCreated().toString());
        if (note.getContent() != null)
            noteBody.setText(note.getContent());

    }

    private void openNoteForEditing() {
        Intent i = new Intent(getContext(), NoteActivity.class);
        i.putExtra(getString(R.string.NOTE_ID_KEY), note.getNoteID());
        startActivity(i);

    }

}