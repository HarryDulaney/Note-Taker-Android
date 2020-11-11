package com.ethical_techniques.notemaker.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A user defined category for logically grouping of Notes.
 * <p>
 * If Category.ID is equal to -1 than the Category has not saved in persistent memory.
 *
 * @author Harry Dulaney
 */
public class Category implements Parcelable {

    public static final int MAIN_ID = 1;
    public static final String MAIN_NAME = "Display All";
    public static final int MAIN_COLOR = Color.LTGRAY;

    private static final Category MAIN_CATEGORY = new Category(MAIN_ID, MAIN_NAME, MAIN_COLOR);

    /**
     * Instantiates a new Category from Parcel.
     *
     * @param in the in
     */
    protected Category(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private int id;
    private String name;
    private int color;

    public Category() {
        id = -1;
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;

    }

    public Category(int id, String name, int color) {
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

    public static Category getMain() {
        return MAIN_CATEGORY;
    }


    @NotNull
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id &&
                name.equals(category.name) &&
                color == category.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}