package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.List;

import fr.enst.budgetapp.ExpenseLimit;
import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;

public class EditExpenseLimitFragment extends Fragment {

    private ExpenseLimit currentLimit;
    private EditText etEditExpenseLimitAmount;
    private EditText etEditExpenseLimitDate;
    private EditText etEditExpenseLimitNotes;
    private TextView tvCategoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_expense_limit, container, false);

        // UI references
        etEditExpenseLimitAmount = root.findViewById(R.id.etEditExpenseLimitAmount);
        etEditExpenseLimitDate = root.findViewById(R.id.etEditExpenseLimitDate);
        etEditExpenseLimitNotes = root.findViewById(R.id.etEditExpenseLimitNotes);
        tvCategoryName = root.findViewById(R.id.tvEditChooseCategory);
        Button btnBack = root.findViewById(R.id.btnBackBudget);
        Button btnDelete = root.findViewById(R.id.btnDeleteExpenseLimit);
        Button btnSave = root.findViewById(R.id.btnSaveExpenseLimit);
        ImageView ivChooseCategory = root.findViewById(R.id.ivEditChooseCategory);

        Bundle args = getArguments();

        // Load the correct limit
        if (args != null) {
            String limitId = args.getString("EXPENSE_LIMIT_ID");
            List<ExpenseLimit> limits = JsonLoader.loadExpenseLimits(requireContext());
            for (ExpenseLimit limit : limits) {
                if (limit.getId().equals(limitId)) {
                    currentLimit = limit;
                    break;
                }
            }

            // Restore values from previous state if available
            etEditExpenseLimitAmount.setText(args.getString("FORM_AMOUNT", currentLimit != null ? String.valueOf(currentLimit.getAmount()) : ""));
            etEditExpenseLimitDate.setText(args.getString("FORM_DATE", currentLimit != null ? currentLimit.getEndDate() : ""));
            etEditExpenseLimitNotes.setText(args.getString("FORM_NOTES", currentLimit != null ? currentLimit.getNotes() : ""));
            tvCategoryName.setText(args.getString("CATEGORY_NAME", currentLimit != null ? currentLimit.getCategoryName() : ""));
        }

        // --- Category selection navigation ---
        View.OnClickListener chooseCategoryListener = v -> {
            Bundle bundle = new Bundle();
            bundle.putString("PREVIOUS_FRAGMENT", "editExpenseLimit");

            // Save current form state before navigating away
            bundle.putString("EXPENSE_LIMIT_ID", currentLimit.getId());
            bundle.putString("FORM_AMOUNT", etEditExpenseLimitAmount.getText().toString());
            bundle.putString("FORM_DATE", etEditExpenseLimitDate.getText().toString());
            bundle.putString("FORM_NOTES", etEditExpenseLimitNotes.getText().toString());
            bundle.putString("CATEGORY_NAME", tvCategoryName.getText().toString());

            Navigation.findNavController(v).navigate(R.id.action_editExpenseLimitFragment_to_categoriesFragment, bundle);
        };

        ivChooseCategory.setOnClickListener(chooseCategoryListener);
        tvCategoryName.setOnClickListener(chooseCategoryListener);

        // --- Back button ---
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // --- Delete button ---
        btnDelete.setOnClickListener(v -> {
            // Optional: implement actual deletion here
            Navigation.findNavController(v).navigate(R.id.action_editExpenseLimitFragment_to_budgetFragment);
        });

        // --- Save button ---
        btnSave.setOnClickListener(v -> {
            if (currentLimit != null) {
                currentLimit.setAmount(Double.parseDouble(etEditExpenseLimitAmount.getText().toString()));
                currentLimit.setEndDate(etEditExpenseLimitDate.getText().toString());
                currentLimit.setNotes(etEditExpenseLimitNotes.getText().toString());
                currentLimit.setCategoryName(tvCategoryName.getText().toString());

                List<ExpenseLimit> allLimits = JsonLoader.loadExpenseLimits(requireContext());
                for (int i = 0; i < allLimits.size(); i++) {
                    if (allLimits.get(i).getId().equals(currentLimit.getId())) {
                        allLimits.set(i, currentLimit);
                        break;
                    }
                }

                JsonLoader.saveExpenseLimits(requireContext(), allLimits);
            }
            Navigation.findNavController(v).navigate(R.id.action_editExpenseLimitFragment_to_budgetFragment);
        });

        return root;
    }
}
