package fr.enst.budgetapp.ui.budget;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

public class EditSavingGoalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_saving_goal, container, false);

        final String goalId = getArguments() != null ? getArguments().getString("goalId") : null;


        // Initialize UI elements
        EditText etGoalName = root.findViewById(R.id.etGoalName);
        EditText etGoalAmount = root.findViewById(R.id.etGoalAmount);
        EditText etGoalDeadline = root.findViewById(R.id.etGoalDeadline);
        EditText etGoalNotes = root.findViewById(R.id.etGoalNotes);

        Button btnBack = root.findViewById(R.id.btnBack);
        Button btnDelete = root.findViewById(R.id.btnDelete);
        Button btnSave = root.findViewById(R.id.btnSave);

        // Get goal details from arguments
        Bundle args = getArguments();
        if (args != null) {
            //goalId = args.getString("goalId");
            List<SavingGoal> goals = JsonLoader.loadSavingGoals(requireContext());

            SavingGoal selectedGoal = null;
            for (SavingGoal goal : goals) {
                if (goal.getId().equals(goalId)) {
                    selectedGoal = goal;
                    break;
                }
            }

            if (selectedGoal != null) {
                etGoalName.setText(selectedGoal.getTitle());
                etGoalAmount.setText(String.valueOf(selectedGoal.getTotalAmount()));
                etGoalDeadline.setText(selectedGoal.getDeadline());
                etGoalNotes.setText(selectedGoal.getNotes());
            }
        }

        // Handle Back button click
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Handle Delete button click
        btnDelete.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Goal")
                    .setMessage("Are you sure you want to delete this goal?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // TODO: Delete the goal

                        if (args == null) return;

                        if (goalId == null) return;

                        List<SavingGoal> goals = JsonLoader.loadSavingGoals(requireContext());

                        goals.removeIf(goal -> goal.getId().equals(goalId));
                        Log.d("GOAL REMOVE ID", goalId);

                        for (int i = 0; i < goals.size(); i++) {
                            goals.get(i).setId(String.valueOf(i + 1));
                        }

                        JsonLoader.saveSavingGoals(requireContext(), goals);

                        Toast.makeText(getContext(), "Goal Deleted!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigateUp();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });



        btnSave.setOnClickListener(v -> {
            String updatedName = etGoalName.getText().toString();
            String updatedAmountStr = etGoalAmount.getText().toString();
            String updatedDeadline = etGoalDeadline.getText().toString();
            String updatedNotes = etGoalNotes.getText().toString();

            if (updatedName.isEmpty() || updatedAmountStr.isEmpty() || updatedDeadline.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            int updatedAmount = Integer.parseInt(updatedAmountStr);

            List<SavingGoal> goals = JsonLoader.loadSavingGoals(requireContext());
            for (int i = 0; i < goals.size(); i++) {
                if (goals.get(i).getId().equals(goalId)) {
                    SavingGoal updatedGoal = new SavingGoal(goalId, updatedName, 0, updatedDeadline, updatedAmount, updatedNotes);
                    goals.set(i, updatedGoal);
                    break;
                }
            }

            JsonLoader.saveSavingGoals(requireContext(), goals);
            Toast.makeText(getContext(), "Goal Updated!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });


        return root;
    }
}