package com.ethical_techniques.notemaker.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ethical_techniques.notemaker.model.Note;

/**
 * The type {@code DialogUtil.class}
 * is a utility class containing methods for defining the behavior and appearance of Dialogs.
 *
 * @author Harry Dulaney
 */
public class DialogUtil {

    /**
     * Make and show a Dialog with a definable DialogAction.
     *
     * @param context the context
     * @param title   the title
     * @param message the message
     * @param action  the action to perform when DialogAction.onAction() is called.
     */
    public static void makeAndShow(final Context context,
                                   final String title,
                                   final String message,
                                   DialogAction action) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            Toast.makeText(context, "Cancelled delete Note operation.", Toast.LENGTH_LONG).show();
            dialog.cancel();
        });
        builder.setPositiveButton("DELETE", (dialog, usersChoice) -> {
            //Handle deleting the Note
            action.onAction();

        });
        builder.create().show();
    }

    public static void makeAndShow(final Context context,
                                   final String title,
                                   final String message,
                                   final String posButton,
                                   final String negButton,
                                   DialogAction positiveAction,
                                   DialogAction negativeAction) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(negButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                negativeAction.onAction();
            }
        });
        builder.setPositiveButton(posButton, (dialog, usersChoice) -> {

            positiveAction.onAction();

        });
        builder.create().show();
    }


}
