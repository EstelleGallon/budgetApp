package fr.enst.budgetapp.ui.list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import fr.enst.budgetapp.R;

public class EditCategoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_categories, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewEditCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create a list of categories
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Groceries", Color.parseColor("#FAA9D8"), R.drawable.ic_circle_filled));
        categoryList.add(new Category("Entertainment", Color.parseColor("#8FD4FA"), R.drawable.ic_circle_filled));
        categoryList.add(new Category("Transportation", Color.parseColor("#A8FA91"), R.drawable.ic_circle_filled));
        categoryList.add(new Category("Healthcare", Color.parseColor("#F9FA97"), R.drawable.ic_circle_filled));
        categoryList.add(new Category("Utilities", Color.parseColor("#CCAAFA"), R.drawable.ic_circle_filled));


        View.OnClickListener nameClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 1) user can type new name for category
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

        Button saveBtn = rootView.findViewById(R.id.btnSaveCategories);
        View.OnClickListener saveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:
                //  - 6) save all the changes

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
                deleteClickListener);
        recyclerView.setAdapter(adapter);

        // TODO: handle new category

        return rootView;
    }


}
