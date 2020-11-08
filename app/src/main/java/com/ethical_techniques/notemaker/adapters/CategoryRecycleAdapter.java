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
import com.ethical_techniques.notemaker.model.Category;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Category}.
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

    private List<Category> categories;
    private DataSource dataSource;

    /**
     * The Delete button listener.
     */
    DeleteClickListener deleteButtonListener;
    /**
     * The Category short click listener.
     */
    ListClickListener categoryShortClickListener;
    /**
     * The Category long click listener.
     */
    ListLongClickListener categoryLongClickListener;

    /**
     * Sets short click listener.
     *
     * @param categoryShortClickListener the category short click listener
     */
    public void setShortClickListener(ListClickListener categoryShortClickListener) {
        this.categoryShortClickListener = categoryShortClickListener;
    }

    /**
     * Sets long click listener.
     *
     * @param categoryLongClickListener the category long click listener
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
     * Instantiates a new Category recycle adapter.
     *
     * @param items the items
     */
    public CategoryRecycleAdapter(List<Category> items) {
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

        holder.category = categories.get(position);
        holder.name.setText(categories.get(position).getName());
        holder.mView.setBackgroundColor(categories.get(position).getColor());
        holder.deleteButton.setVisibility(View.INVISIBLE);
        if (position == 0) holder.deleteButton.setColorFilter(R.color.deactivated_link);

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
     * The type Category view holder.
     */
    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        /**
         * The Category's view.
         */
        public final View mView;
        /**
         * The Category.
         */
        public Category category;
        /**
         * The Name.
         */
        public final TextView name;
        /**
         * The Delete button.
         */
        public final ImageButton deleteButton;

        /**
         * Instantiates a new Category view holder.
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
            return super.toString() + "'" + category.getName() + "'" + category.getId() + "'" + category.getColor() + "'";
        }


    }
}


