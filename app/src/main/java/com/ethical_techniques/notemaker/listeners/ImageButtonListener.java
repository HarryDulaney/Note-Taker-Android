package com.ethical_techniques.notemaker.listeners;

import android.widget.ImageButton;

/**
 * The interface Image button listener.
 *
 * @author Harry Dulaney
 */
public interface ImageButtonListener {
    /**
     * Change state on clicked.
     *
     * @param imageButton the image button
     * @param position    the position
     */
    void changeStateOnClicked(ImageButton imageButton, int position);
}
