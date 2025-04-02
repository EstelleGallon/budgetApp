package fr.enst.budgetapp.ui.budget;

import android.app.AlertDialog;
import android.os.Bundle;
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

import fr.enst.budgetapp.R;

public class EditSavingGoalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_saving_goal, container, false);

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
            String goalId = args.getString("goalId");
            // TODO: Fetch goal details using goalId and pre-fill fields
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
                        Toast.makeText(getContext(), "Goal Deleted!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigateUp();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            // TODO: Save the updated goal
            Toast.makeText(getContext(), "Goal Updated!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });

        return root;
    }
}