package fr.enst.budgetapp;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EditCategoryAdapter extends RecyclerView.Adapter<EditCategoryAdapter.EditCategoryViewHolder> {

    private final List<Category> categories;
    private final View.OnClickListener nameClickListener;
    private final View.OnClickListener colorClickListener;
    private final View.OnClickListener deleteClickListener;
    private final View.OnClickListener addCatClickListener;

    public EditCategoryAdapter(List<Category> categories,
                               View.OnClickListener nameClickListener,
                               View.OnClickListener colorClickListener,
                               View.OnClickListener deleteClickListener,
                               View.OnClickListener addCatClickListener) {
        this.categories = categories;
        this.nameClickListener = nameClickListener;
        this.colorClickListener = colorClickListener;
        this.deleteClickListener = deleteClickListener;
        this.addCatClickListener = addCatClickListener;
    }

    @Override
    public EditCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_edit, parent, false);
        return new EditCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EditCategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.categoryName.setText(category.getName());
        holder.categoryColor.setImageResource(category.getIconResId());
        holder.categoryColor.setColorFilter(category.getColor(), PorterDuff.Mode.SRC_IN); // Apply the color to the icon

        // set click listeners on holders
        holder.categoryName.setOnClickListener(v -> {
            if (nameClickListener != null)
                nameClickListener.onClick(v);
        });

        holder.categoryColor.setOnClickListener(v -> {
            if (colorClickListener != null)
                colorClickListener.onClick(v);
        });

        holder.categoryDelete.setOnClickListener(v -> {
            if (deleteClickListener != null)
                deleteClickListener.onClick(v);
        });


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public static class EditCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryColor;
        ImageView categoryDelete;

        public EditCategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.etEditCategoryName);
            categoryColor = itemView.findViewById(R.id.ivEditCategoryColor);
            categoryDelete = itemView.findViewById(R.id.ivDeleteCategory);
        }
    }
}
