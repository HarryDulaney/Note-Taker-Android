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
     * Make and show a Dialog with a definable DialogAction. Default negative button
     * triggers dialog.cancel()
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

    /**
     * Make and show.
     *
     * @param context   the context Context
     * @param title     the title String
     * @param message   the message String
     * @param yesButton the yes button String
     * @param noButton  the no button String
     * @param yesAction the yes action DialogAction
     * @param noAction  the no action DialogAction
     */
    public static void makeAndShow(final Context context,
                                   final String title,
                                   final String message,
                                   final String yesButton,
                                   final String noButton,
                                   DialogAction yesAction,
                                   DialogAction noAction) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(yesButton, (dialog, which) -> yesAction.onAction())
                .setNegativeButton(noButton, (dialog, usersChoice) -> {
                    noAction.onAction();
                })
                .create()
                .show();
    }

    /**
     * Make and show.
     *
     * @param context       the context Context
     * @param title         the title String
     * @param message       the message String
     * @param yesButton     the yes button String
     * @param noButton      the no button String
     * @param neutralButton the neutral button
     * @param yesAction     the yes action DialogAction
     * @param noAction      the no action DialogAction
     * @param neutralAction the neutral action
     */
    public static void makeAndShow(final Context context,
                                   final String title,
                                   final String message,
                                   final String yesButton,
                                   final String noButton,
                                   final String neutralButton,
                                   DialogAction yesAction,
                                   DialogAction noAction,
                                   DialogAction neutralAction) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(yesButton, (dialog, which) -> yesAction.onAction())
                .setNegativeButton(noButton, (dialog, usersChoice) -> noAction.onAction())
                .setNeutralButton(neutralButton, ((dialog, which) -> {
                            neutralAction.onAction();
                            dialog.cancel();
                        })
                )
                .create()
                .show();
    }


}
