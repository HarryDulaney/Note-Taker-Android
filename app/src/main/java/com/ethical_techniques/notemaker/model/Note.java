package com.ethical_techniques.notemaker.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;

/**
 * A POJO to represent the Note object and its attributes:
 * noteID - Default is -1 until persisted to db and replaced with auto-incremented Id.
 * subject - Optional subject line
 * noteContent - The body of the note containing the main contents (Not Null)
 * dateCreated - Holds the calender day of note creation
 *
 * @author Harry Dulaney
 */
public class Note implements Parcelable {

    /**
     * Database Id
     */
    private int noteID;
    /**
     * The title of the note
     */
    private String noteName;
    /**
     * The default NoteCategory is "All Notes". Notes are set to NoteCategory.getMain() at creation but users can change this reference at any time.
     */
    private NoteCategory noteCategory;

    /**
     * The body or main text of the Note
     */
    private String noteContent;
    /**
     * Date the note was created (or edited last)
     */
    private Calendar dateCreated;

    /**
     * Users designate the note as important to set priority the PRIORITY.HIGH
     */
    private PRIORITY PRIORITY_LEVEL;

    /**
     * Note constructed with default values
     */
    public Note() {
        noteID = -1;
        noteCategory = NoteCategory.getMain();
        PRIORITY_LEVEL = PRIORITY.LOW;
    }

    public Note(int noteID, String noteName, NoteCategory noteCategory, String noteContent, Calendar dateCreated, String priorityLevel) {
        this.noteID = noteID;
        this.noteName = noteName;
        this.noteCategory = noteCategory;
        this.noteContent = noteContent;
        this.dateCreated = dateCreated;
        setPRIORITY_LEVEL(priorityLevel);
    }

    /**
     * Instantiates a new Note by un-flattening the Parcel.
     *
     * @param in the in
     */
    protected Note(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public NoteCategory getNoteCategory() {
        return noteCategory;
    }

    public void setNoteCategory(NoteCategory noteCategory) {
        this.noteCategory = noteCategory;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int n) {
        this.noteID = n;
    }

    public String getContent() {
        return noteContent;
    }

    public void setNoteContent(String c) {
        this.noteContent = c;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar c) {
        this.dateCreated = c;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    /**
     * @return String identifier value representing the PRIORITY enumeration.
     */
    public String getPRIORITY_LEVEL() {
        return PRIORITY_LEVEL.getString();
    }

    /**
     * @param prior_level string value representing the PRIORITY enum
     */
    public void setPRIORITY_LEVEL(String prior_level) {
        this.PRIORITY_LEVEL = prior_level.equals(PRIORITY.HIGH.getString()) ? PRIORITY.HIGH : PRIORITY.LOW;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return noteID == note.noteID &&
                PRIORITY_LEVEL == note.PRIORITY_LEVEL &&
                Objects.equals(noteName, note.noteName) &&
                Objects.equals(noteCategory, note.noteCategory) &&
                Objects.equals(noteContent, note.noteContent) &&
                Objects.equals(dateCreated, note.dateCreated);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(noteID, noteName, noteCategory, noteContent, dateCreated, PRIORITY_LEVEL);
    }

    @NotNull
    @Override
    public String toString() {
        return "Note{" +
                "noteID=" + noteID +
                ", noteName='" + noteName + '\'' +
                ", subject='" + noteCategory + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", dateCreated=" + dateCreated +
                ", priority=" + PRIORITY_LEVEL +
                '}';
    }

}