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
public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder> {

    private List<Category> categories;
    private DataSource dataSource;

    public CategoryRecycleAdapter(List<Category> items) {
        categories = items;
    }

    @NotNull
    @Override
    public CategoryRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new CategoryRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryRecycleAdapter.ViewHolder holder, int position) {

        holder.category = categories.get(position);
        holder.name.setText(categories.get(position).getName());
        holder.mView.setBackgroundColor(categories.get(position).getColor());

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public Category category;
        public final TextView name;
        public final ImageButton deleteButton;
        public final Button colorChooserButton;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            name = v.findViewById(R.id.categoryName);
            deleteButton = v.findViewById(R.id.buttonDeleteCategory);
            colorChooserButton = v.findViewById(R.id.categoryEditOpenButton);

        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + "'" + category.getName() + "'" + category.getId() + "'" + category.getColor() + "'";
        }


    }
}


