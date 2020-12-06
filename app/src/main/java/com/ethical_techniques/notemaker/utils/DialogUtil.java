package com.ethical_techniques.notemaker.utils;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import static android.widget.FrameLayout.LayoutParams.MATCH_PARENT;
import static androidx.constraintlayout.widget.ConstraintProperties.WRAP_CONTENT;

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
     * @param context        the context
     * @param title          the title
     * @param message        the message
     * @param positiveAction the positiveAction to perform when DialogAction.onAction() is called.
     */
    public static void makeAndShow(final Context context,
                                   final String title,
                                   final String message,
                                   final String positiveButtonString,
                                   final String negativeButtonString,
                                   DialogAction positiveAction) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(negativeButtonString, (dialog, which) -> {
            Toast.makeText(context, "Cancelled delete Note operation.", Toast.LENGTH_LONG).show();
            dialog.cancel();
        });
        builder.setPositiveButton(positiveButtonString, (dialog, usersChoice) -> {
            //Handle deleting the Note
            positiveAction.onAction();

        });
        builder.create().show();
    }

    /**
     * Make and show.
     *
     * @param context              the context Context
     * @param title                the title String
     * @param message              the message String
     * @param positiveButtonString the yes button String
     * @param negativeButtonString the no button String
     * @param positiveAction       the yes action DialogAction
     * @param negativeAction       the no action DialogAction
     */
    public static void makeAndShow(final Context context,
                                   final String title,
                                   final String message,
                                   final String positiveButtonString,
                                   final String negativeButtonString,
                                   DialogAction positiveAction,
                                   DialogAction negativeAction) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonString, (dialog, which) -> positiveAction.onAction())
                .setNegativeButton(negativeButtonString, (dialog, usersChoice) -> {
                    negativeAction.onAction();
                })
                .create()
                .show();
    }

    /**
     * Make and show.
     *
     * @param context        the context Context
     * @param title          the title String
     * @param message        the message String
     * @param positiveString the yes button String
     * @param negativeString the no button String
     * @param neutralButton  the neutral button
     * @param positiveAction the yes action DialogAction
     * @param negativeAction the no action DialogAction
     * @param neutralAction  the neutral action
     */
    public static void makeAndShow(final Context context,
                                   final String title,
                                   final String message,
                                   final String positiveString,
                                   final String negativeString,
                                   final String neutralButton,
                                   DialogAction positiveAction,
                                   DialogAction negativeAction,
                                   DialogAction neutralAction) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveString, (dialog, which) -> positiveAction.onAction())
                .setNegativeButton(negativeString, (dialog, usersChoice) -> negativeAction.onAction())
                .setNeutralButton(neutralButton, ((dialog, which) -> {
                            neutralAction.onAction();
                            dialog.cancel();
                        })
                )
                .create()
                .show();
    }

    public static void makeAndShowBasicError(final Context context, final String explain) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error")
                .setMessage(explain)
                .setNeutralButton("Acknowledge", ((dialog, which) -> dialog.dismiss()))
                .create()
                .show();
    }

    public static void makeAndShowCustom(final View customContent, final Context context,
                                         FrameLayout frameLayout) {
        frameLayout.addView(customContent, new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();
        alertDialog.setView(frameLayout);
        alertDialog.show();
    }


    private DialogUtil() {
    }

}
