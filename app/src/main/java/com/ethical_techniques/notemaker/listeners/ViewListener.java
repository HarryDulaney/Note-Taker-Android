package com.ethical_techniques.notemaker.listeners;

import android.view.View;

/**
 * The interface View listener.
 *
 * @author Harry Dulaney
 */
public interface ViewListener {
    /**
     * Clicked.
     *
     * @param view     the view
     * @param position the position
     */
    void clicked(View view, int position);
}
