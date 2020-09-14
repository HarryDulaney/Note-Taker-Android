package com.ethical_techniques.notemaker.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Objects;

/**
 * A POJO to represent the Note object and its attributes:
 * noteID - Default is -1 to check instantiation. DB will auto-increment
 * subject - Optional subject line
 * noteContent - The body of the note containing the main contents (Not Null)
 * dateCreated - Holds the calender day of note creation
 */
public class Note {
    private static final String DEFAULT_NOTE = "This current screen is your Note List! ";
    private static final String DEFAULT_TITLE = "Welcome to The Note App!!";
    /**
     * Database Id
     */
    private int noteID;
    /**
     * The title of the note
     */
    private String noteName;
    /**
     * Optional Subject
     */
    private String subject;
    /**
     * The body or main text of the Note
     */
    private String noteContent;
    /**
     * Date the note was created (or edited last)
     */
    private Calendar dateCreated;
    /**
     * The state of the Note as its displayed in the list
     */
    private Boolean isExpanded;
    /**
     * Users designates the priority level as high, med, or low
     */
    private int priorityLevel;

    public Note() {
        noteID = -1;
        isExpanded = false;
        priorityLevel = 0;
    }

    public static Note getDefaultNote() {
        Note note = new Note();
        note.setNoteName(DEFAULT_TITLE);
        note.setNoteContent(DEFAULT_NOTE);
        note.setPriorityLevel(0);

        return note;

    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }


    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return noteID == note.noteID &&
                priorityLevel == note.priorityLevel &&
                Objects.equals(noteName, note.noteName) &&
                Objects.equals(subject, note.subject) &&
                Objects.equals(noteContent, note.noteContent) &&
                Objects.equals(dateCreated, note.dateCreated) &&
                Objects.equals(isExpanded, note.isExpanded);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(noteID, noteName, subject, noteContent, dateCreated, isExpanded, priorityLevel);
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteID=" + noteID +
                ", noteName='" + noteName + '\'' +
                ", subject='" + subject + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", dateCreated=" + dateCreated +
                ", isExpanded=" + isExpanded +
                ", priorityLevel=" + priorityLevel +
                '}';
    }

}
