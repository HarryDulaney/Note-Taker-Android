package com.ethical_techniques.notemaker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ethical_techniques.notemaker.DAL.DataSource;
import com.ethical_techniques.notemaker.note.Category;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Category}.
 */
public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private DataSource dataSource;

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
        holder.chooseEditCategoryButton.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();

    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public Category category;
        public final TextView name;
        public final ImageButton deleteButton;
        public Button chooseEditCategoryButton;

        public CategoryViewHolder(View v) {
            super(v);
            mView = v;
            name = v.findViewById(R.id.categoryName);
            deleteButton = v.findViewById(R.id.buttonDeleteCategory);
            chooseEditCategoryButton = v.findViewById(R.id.categoryEditOpenButton);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + "'" + category.getName() + "'" + category.getId() + "'" + category.getColor() + "'";
        }


    }
}


