package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fr.enst.budgetapp.R;

public class EditExpenseLimitFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_expense_limit, container, false);

        // Initialize UI elements
        EditText etEditExpenseLimitAmount = root.findViewById(R.id.etEditExpenseLimitAmount);
        EditText etEditExpenseLimitDate = root.findViewById(R.id.etEditExpenseLimitDate);
        EditText etEditExpenseLimitNotes = root.findViewById(R.id.etEditExpenseLimitNotes);

        Button btnBack = root.findViewById(R.id.btnBackBudget);
        Button btnDelete = root.findViewById(R.id.btnDeleteExpenseLimit);
        Button btnSave = root.findViewById(R.id.btnSaveExpenseLimit);

        // --- Handle navigation to Category page ---




        return root;
    }
}
