package fr.enst.budgetapp.ui.list;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.enst.budgetapp.Category;
import fr.enst.budgetapp.CategoryAdapter;
import fr.enst.budgetapp.R;

public class CategoryFragment  extends Fragment {
    private String previousFragment = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewCategories);
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
                String categoryName = ((TextView) v).getText().toString();
                System.out.println(categoryName);

                // Create a bundle to send data to NewTransactionFragment
                Bundle bundle = new Bundle();
                bundle.putString("CATEGORY_NAME", categoryName);

                // retrieve previous fragment name
                if (getArguments() != null) {
                    previousFragment = getArguments().getString("PREVIOUS_FRAGMENT");
                }
                if (Objects.equals(previousFragment, "new")) {
                    Navigation.findNavController(v).navigate(R.id.action_categoriesFragment_to_newTransactionFragment, bundle);
                } else if (Objects.equals(previousFragment, "edit")) {
                    Navigation.findNavController(v).navigate(R.id.action_categoriesFragment_to_editTransactionFragment, bundle);
                }
                System.out.println("previous fragment: " + previousFragment);
            }
        };

        ImageView editCategory = rootView.findViewById(R.id.ivEditCategory);
        View.OnClickListener editClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null) {
                    previousFragment = getArguments().getString("PREVIOUS_FRAGMENT");
                }
                Bundle bundleEdit = new Bundle();
                bundleEdit.putString("PREVIOUS_FRAGMENT", previousFragment);
                Navigation.findNavController(v).navigate(R.id.action_categoriesFragment_to_EditCategoryFragment, bundleEdit);
            }
        };
        editCategory.setOnClickListener(editClickListener);


        // Set the adapter
        CategoryAdapter adapter = new CategoryAdapter(categoryList, nameClickListener);
        recyclerView.setAdapter(adapter);

        return rootView;
    }


}
