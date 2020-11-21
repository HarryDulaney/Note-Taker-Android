package com.ethical_techniques.notemaker.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A user defined noteCategory for logically grouping of Notes.
 * <p>
 * If NoteCategory.ID is equal to -1 than the NoteCategory has not saved in persistent memory.
 *
 * @author Harry Dulaney
 */
public class NoteCategory implements Parcelable {

    public static final int MAIN_ID = 1;
    public static final String MAIN_NAME = "All Notes";
    public static final int MAIN_COLOR = Color.LTGRAY;

    private static final NoteCategory MAIN_NOTE_CATEGORY = new NoteCategory(MAIN_ID, MAIN_NAME, MAIN_COLOR);

    /**
     * Instantiates a new NoteCategory from Parcel.
     *
     * @param in the in
     */
    protected NoteCategory(Parcel in) {

    }

    public static int getPosition(String name, List<String> catDnames) {
        return catDnames.indexOf(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public static final Creator<NoteCategory> CREATOR = new Creator<NoteCategory>() {
        @Override
        public NoteCategory createFromParcel(Parcel in) {
            return new NoteCategory(in);
        }

        @Override
        public NoteCategory[] newArray(int size) {
            return new NoteCategory[size];
        }
    };

    private int id;
    private String name;
    private int color;

    public NoteCategory() {
        id = -1;
        color = Color.GRAY;
    }

    public NoteCategory(int id, String name) {
        this.id = id;
        this.name = name;
        color = Color.GRAY;

    }

    public NoteCategory(int id, String name, int color) {
        this(id, name);
        this.color = color;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static NoteCategory getMain() {
        return MAIN_NOTE_CATEGORY;
    }

    public static List<String> getDisplayNames(List<NoteCategory> categories) {
        List<String> categoryStrings = new ArrayList<>();
        // Add all Categories names to the list for the dropdown spinner
        for (NoteCategory noteCategory : categories) {
            categoryStrings.add(noteCategory.toString());
        }
        return categoryStrings;
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteCategory noteCategory = (NoteCategory) o;
        return id == noteCategory.id &&
                name.equals(noteCategory.name) &&
                color == noteCategory.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}