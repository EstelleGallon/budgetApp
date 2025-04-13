package fr.enst.budgetapp.ui.list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.enst.budgetapp.Category;
import fr.enst.budgetapp.CategoryAdapter;
import fr.enst.budgetapp.EditCategoryAdapter;
import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;

public class EditCategoryFragment extends Fragment {

    private int getRandomColor() {
        float hue = new java.util.Random().nextInt(360);
        float saturation = 0.5f + new java.util.Random().nextFloat() * 0.5f;
        float brightness = 0.7f + new java.util.Random().nextFloat() * 0.3f;
        return Color.HSVToColor(new float[]{hue, saturation, brightness});
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_categories, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewEditCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Category> categoryList = JsonLoader.loadCategories(getContext());

        View.OnClickListener nameClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Rename Category");
                final EditText input = new EditText(getContext());
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString();
                        System.out.println(newName);
                        // TODO: save the new name
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        };

        View.OnClickListener colorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:
                //  - 2) color palette appears
                //  - 3) clicking on the color changes color of category
            }
        };


        /*
        View.OnClickListener deleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: remove category from list

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
*/

        View.OnClickListener deleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RecyclerView recyclerView = ((RecyclerView) ((View) v.getParent().getParent()));
                                RecyclerView.ViewHolder viewHolder = recyclerView.findContainingViewHolder(v);
                                if (viewHolder != null) {
                                    int position = viewHolder.getAdapterPosition();
                                    categoryList.remove(position);
                                    recyclerView.getAdapter().notifyItemRemoved(position);
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };

        View.OnClickListener addCatClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add New Category");

                final EditText input = new EditText(getContext());
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString();
                        if (!newName.isEmpty()) {
                            int randomColor = getRandomColor();
                            Category newCategory = new Category(newName, randomColor, R.drawable.ic_circle_filled);
                            System.out.println("add new category??");
                            categoryList.add(newCategory);
                            // TODO: "add new category";
                            recyclerView.getAdapter().notifyItemInserted(categoryList.size() - 1);
                            recyclerView.scrollToPosition(categoryList.size() - 1);

                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        };
        LinearLayout addNewCategory = rootView.findViewById(R.id.llAddCategory);
        addNewCategory.setOnClickListener(addCatClickListener);



        Button saveBtn = rootView.findViewById(R.id.btnSaveCategories);
        View.OnClickListener saveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:
                //  - 6) save all the changes
                JsonLoader.saveCategories(getContext(), categoryList);

                // retrieve previous fragment name
                String previousFragment = "";
                String previousMenu = "";
                if (getArguments() != null) {
                    previousFragment = getArguments().getString("PREVIOUS_FRAGMENT");
                    previousMenu = getArguments().getString("PREVIOUS_MENU");
                }

                Bundle bundle = new Bundle();
                bundle.putString("PREVIOUS_FRAGMENT", previousFragment);
                bundle.putString("PREVIOUS_MENU", previousMenu);
                Navigation.findNavController(v).navigate(R.id.action_EditCategoryFragment_to_categoriesFragment, bundle);

            }
        };
        saveBtn.setOnClickListener(saveClickListener);


        EditCategoryAdapter adapter = new EditCategoryAdapter(
                categoryList,
                nameClickListener,
                colorClickListener,
                deleteClickListener,
                addCatClickListener);
        recyclerView.setAdapter(adapter);



        return rootView;
    }


}
