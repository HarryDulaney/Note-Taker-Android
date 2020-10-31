package com.ethical_techniques.notemaker;

import android.view.View;
import android.widget.ImageButton;


public interface NoteClickListener {
    void onNoteClicked(View view, int position);
}

