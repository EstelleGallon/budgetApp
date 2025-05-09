package fr.enst.budgetapp;

import android.graphics.PorterDuff;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final View.OnClickListener nameClickListener;

    public CategoryAdapter(List<Category> categories, View.OnClickListener nameClickListener) {
        this.categories = categories;
        this.nameClickListener = nameClickListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.categoryName.setText(category.getName());
        holder.categoryIcon.setImageResource(category.getIconResId());
        holder.categoryIcon.setColorFilter(category.getColor(), PorterDuff.Mode.SRC_IN); // Apply the color to the icon

        // click listener on category name
        holder.categoryName.setOnClickListener(v -> {
            if (nameClickListener != null) {
                nameClickListener.onClick(v);
            }

        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryIcon;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tvCategoryName);
            categoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
        }
    }
}

