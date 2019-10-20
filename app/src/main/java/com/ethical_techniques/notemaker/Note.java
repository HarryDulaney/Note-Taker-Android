package com.ethical_techniques.notemaker;

import java.util.Calendar;

/**
 *     A POJO to represent the Note object and its attributes:
 *     noteID - Default is -1 to check instantiation. DB will auto-increment
 *     subject - Optional subject line
 *     noteContent - The body of the note containing the main contents (Not Null)
 *     dateCreated - Holds the calender day of note creation
 *
 */
public class Note {

    private int noteID;
    private String noteName;
    private String subject;
    private String noteContent;
    private Calendar dateCreated;


    public Note(){
        noteID = -1;
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
        this.dateCreated = c; }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

}
