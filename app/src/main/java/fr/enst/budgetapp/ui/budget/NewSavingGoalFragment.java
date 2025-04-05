package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.List;

import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.SavingGoal;

public class NewSavingGoalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_saving_goal, container, false);

        // Initialize UI elements
        EditText etGoalName = root.findViewById(R.id.etGoalName);
        EditText etGoalAmount = root.findViewById(R.id.etGoalAmount);
        EditText etGoalDeadline = root.findViewById(R.id.etGoalDeadline);
        EditText etGoalNotes = root.findViewById(R.id.etGoalNotes);

        Button btnBack = root.findViewById(R.id.btnBack);
        Button btnSave = root.findViewById(R.id.btnSave);

        // Handle Back button click
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            String name = etGoalName.getText().toString();
            String amountStr = etGoalAmount.getText().toString();
            String deadline = etGoalDeadline.getText().toString();
            String notes = etGoalNotes.getText().toString();

            if (name.isEmpty() || amountStr.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountStr);

            List<SavingGoal> goals = JsonLoader.loadSavingGoals(getContext());
            String id = String.valueOf(goals.size() + 1);

            // We'll compute the progress in BudgetFragment, so just initialize with 0
            SavingGoal newGoal = new SavingGoal(id, name, 0, deadline, amount, notes);

            goals.add(newGoal);
            JsonLoader.saveSavingGoals(getContext(), goals);

            Toast.makeText(getContext(), "New Saving Goal Added!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });

        return root;
    }
}