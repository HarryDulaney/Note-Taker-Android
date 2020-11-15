package com.ethical_techniques.notemaker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ethical_techniques.notemaker.DAL.DataSource;
import com.ethical_techniques.notemaker.R;
import com.ethical_techniques.notemaker.listeners.ListClickListener;
import com.ethical_techniques.notemaker.listeners.ListLongClickListener;
import com.ethical_techniques.notemaker.listeners.ViewListener;
import com.ethical_techniques.notemaker.model.NoteCategory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NoteCategory}.
 *
 * @author Harry Dulaney
 */
public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.CategoryViewHolder> {
    /**
     * The interface Delete click listener.
     */
    interface DeleteClickListener extends ViewListener {
        void clicked(View v, int position);
    }

    private List<NoteCategory> categories;
    private DataSource dataSource;

    /**
     * The Delete button listener.
     */
    DeleteClickListener deleteButtonListener;
    /**
     * The NoteCategory short click listener.
     */
    ListClickListener categoryShortClickListener;
    /**
     * The NoteCategory long click listener.
     */
    ListLongClickListener categoryLongClickListener;

    /**
     * Sets short click listener.
     *
     * @param categoryShortClickListener the noteCategory short click listener
     */
    public void setShortClickListener(ListClickListener categoryShortClickListener) {
        this.categoryShortClickListener = categoryShortClickListener;
    }

    /**
     * Sets long click listener.
     *
     * @param categoryLongClickListener the noteCategory long click listener
     */
    public void setLongClickListener(ListLongClickListener categoryLongClickListener) {
        this.categoryLongClickListener = categoryLongClickListener;
    }

    /**
     * Sets delete button listener.
     *
     * @param deleteButtonListener the delete button listener
     */
    public void setDeleteButtonListener(DeleteClickListener deleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener;
    }

    /**
     * Instantiates a new NoteCategory recycle adapter.
     *
     * @param items the items
     */
    public CategoryRecycleAdapter(List<NoteCategory> items) {
        categories = items;
    }

    @NotNull
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {

        holder.noteCategory = categories.get(position);
        holder.name.setText(categories.get(position).getName());
        holder.mView.setBackgroundColor(categories.get(position).getColor());
        holder.deleteButton.setVisibility(View.INVISIBLE);
        if (holder.noteCategory.getName().equals(NoteCategory.MAIN_NAME)) holder.deleteButton.setColorFilter(R.color.deactivated_link);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();

    }

    /**
     * The type NoteCategory view holder.
     */
    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        /**
         * The NoteCategory's view.
         */
        public final View mView;
        /**
         * The NoteCategory.
         */
        public NoteCategory noteCategory;
        /**
         * The Name.
         */
        public final TextView name;
        /**
         * The Delete button.
         */
        public final ImageButton deleteButton;

        /**
         * Instantiates a new NoteCategory view holder.
         *
         * @param v the v
         */
        public CategoryViewHolder(View v) {
            super(v);
            mView = v;
            name = v.findViewById(R.id.categoryName);
            deleteButton = v.findViewById(R.id.buttonDeleteCategory);

            deleteButton.setOnClickListener(vw -> {
                if (deleteButtonListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteButtonListener.clicked(vw, position);
                    }
                }
            });
            mView.setOnClickListener(view -> {
                if (categoryShortClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
//                        view.setAlpha(0.7f);
                        categoryShortClickListener.clicked(view, position);
                    }
                }
            });
            mView.setOnLongClickListener(v1 -> {
                if (categoryLongClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        categoryLongClickListener.clicked(v1, position);
                        return true;
                    }
                }
                return false;
            });

        }


        @NotNull
        @Override
        public String toString() {
            return super.toString() + "'" + noteCategory.getName() + "'" + noteCategory.getId() + "'" + noteCategory.getColor() + "'";
        }


    }
}


