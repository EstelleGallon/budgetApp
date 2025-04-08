package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.enst.budgetapp.ExpenseLimit;
import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;

public class NewExpenseLimitFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_expense_limit, container, false);

        EditText etTransactionAmount = root.findViewById(R.id.etTransactionAmount);
        EditText etTransactionDate = root.findViewById(R.id.etTransactionDate);
        EditText etTransactionNotes = root.findViewById(R.id.etTransactionNotes);
        Spinner spinnerRepeat = root.findViewById(R.id.spinnerRepeat);
        TextView tvCategory = root.findViewById(R.id.tvChooseCategory);
        ImageView ivCategory = root.findViewById(R.id.ivChooseCategory);

        // --- Back button ---
        Button btnBack = root.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // --- Save button ---
        Button btnSave = root.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String categoryName = tvCategory.getText().toString();
            String amountStr = etTransactionAmount.getText().toString();
            String endDate = etTransactionDate.getText().toString();
            String repeatFrequency = spinnerRepeat.getSelectedItem().toString();
            String notes = etTransactionNotes.getText().toString();

            if (categoryName.isEmpty() || amountStr.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
            int id = JsonLoader.getNextExpenseLimitId(requireContext());

            Boolean[] exceededMonths = new Boolean[12];
            Arrays.fill(exceededMonths, null);

            ExpenseLimit newLimit = new ExpenseLimit(
                    String.valueOf(id),
                    categoryName,
                    amount,
                    startDate,
                    endDate,
                    repeatFrequency,
                    notes,
                    exceededMonths
            );

            List<ExpenseLimit> existingLimits = JsonLoader.loadExpenseLimits(requireContext());
            existingLimits.add(newLimit);
            JsonLoader.saveExpenseLimits(requireContext(), existingLimits);

            Toast.makeText(getContext(), "Expense Limit Added!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.action_newExpenseLimitFragment_to_budgetFragment);
        });

        // --- Navigate to categories while saving state ---
        View.OnClickListener categoryNavHandler = v -> {
            Bundle bundle = new Bundle();
            bundle.putString("PREVIOUS_FRAGMENT", "newExpenseLimit");
            bundle.putString("AMOUNT", etTransactionAmount.getText().toString());
            bundle.putString("DATE", etTransactionDate.getText().toString());
            bundle.putString("NOTES", etTransactionNotes.getText().toString());
            bundle.putInt("REPEAT_INDEX", spinnerRepeat.getSelectedItemPosition());
            bundle.putString("CATEGORY_NAME", tvCategory.getText().toString());

            Navigation.findNavController(v).navigate(R.id.action_newExpenseLimitFragment_to_categoriesFragment, bundle);
        };

        ivCategory.setOnClickListener(categoryNavHandler);
        tvCategory.setOnClickListener(categoryNavHandler);

        // --- Restore values if returning from categories ---
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey("CATEGORY_NAME")) tvCategory.setText(args.getString("CATEGORY_NAME"));
            if (args.containsKey("AMOUNT")) etTransactionAmount.setText(args.getString("AMOUNT"));
            if (args.containsKey("DATE")) etTransactionDate.setText(args.getString("DATE"));
            if (args.containsKey("NOTES")) etTransactionNotes.setText(args.getString("NOTES"));
            if (args.containsKey("REPEAT_INDEX")) spinnerRepeat.setSelection(args.getInt("REPEAT_INDEX"));
        }

        return root;
    }
}
