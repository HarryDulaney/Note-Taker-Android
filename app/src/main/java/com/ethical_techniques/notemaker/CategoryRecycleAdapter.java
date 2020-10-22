package com.ethical_techniques.notemaker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ethical_techniques.notemaker.DAL.DataSource;
import com.ethical_techniques.notemaker.model.Category;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Category}.
 */
public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.CategoryViewHolder> {
    interface DeleteClickListener {
        void onDeleteClicked(View v, int position);
    }

    private List<Category> categories;
    private DataSource dataSource;

    DeleteClickListener deleteButtonListener;
    ListClickListener categoryShortClickListener;
    ListLongClickListener categoryLongClickListener;

    public void setShortClickListener(ListClickListener categoryShortClickListener) {
        this.categoryShortClickListener = categoryShortClickListener;
    }

    public void setLongClickListener(ListLongClickListener categoryLongClickListener) {
        this.categoryLongClickListener = categoryLongClickListener;
    }

    public void setDeleteButtonListener(DeleteClickListener deleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener;
    }

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

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();

    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public Category category;
        public final TextView name;
        public final ImageButton deleteButton;

        public CategoryViewHolder(View v) {
            super(v);
            mView = v;
            name = v.findViewById(R.id.categoryName);
            deleteButton = v.findViewById(R.id.buttonDeleteCategory);

            deleteButton.setOnClickListener(vw -> {
                if (deleteButtonListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteButtonListener.onDeleteClicked(vw, position);
                    }
                }
            });
            mView.setOnClickListener(view -> {
                if (categoryShortClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        categoryShortClickListener.onListClicked(view, position);
                    }
                }
            });
            mView.setOnLongClickListener(v1 -> {
                if (categoryLongClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        categoryLongClickListener.onListLongClicked(v1, position);
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


