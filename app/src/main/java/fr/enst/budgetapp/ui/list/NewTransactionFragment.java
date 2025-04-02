package fr.enst.budgetapp.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import fr.enst.budgetapp.R;

public class NewTransactionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        // Initialize UI elements
        EditText etTransactionAmount = root.findViewById(R.id.etTransactionAmount);
        EditText etTransactionDate = root.findViewById(R.id.etTransactionDate);
        EditText etTransactionNotes = root.findViewById(R.id.etTransactionNotes);

        Button btnBack = root.findViewById(R.id.btnBackToList);
        Button btnSave = root.findViewById(R.id.btnSaveTransaction);

        RadioGroup rgCategory = root.findViewById(R.id.rgCategory);
        RadioButton rbSpending = root.findViewById(R.id.rbSpending);
        RadioButton rbIncome = root.findViewById(R.id.rbIncome);
        RadioButton rbSavings = root.findViewById(R.id.rbSavings);

        RadioGroup rgNotifications = root.findViewById(R.id.rgNotifications);
        RadioButton rbNotificationsOff = root.findViewById(R.id.rbNotificationsOff);
        RadioButton rbNotificationsOn = root.findViewById(R.id.rbNotificationsOn);

        // Handle navigation to Category Page
        ImageView btnChooseCategory = root.findViewById(R.id.ivChooseCategory);
        btnChooseCategory.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_categoriesFragment);
        });

        TextView tvChooseCategory = root.findViewById(R.id.tvChooseCategory);
        tvChooseCategory.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_categoriesFragment);
        });

        // Handle Back button click
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_listFragment));

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            String amount = etTransactionAmount.getText().toString();
            String date = etTransactionDate.getText().toString();
            String notes = etTransactionNotes.getText().toString();
            // TODO: check other fields

            if (date.isEmpty() || amount.isEmpty() || notes.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Save the new transaction

            // Navigate back to the List page
            Navigation.findNavController(v).navigateUp();
        });

        // retrieve chosen category name from bundle
        if (getArguments() != null) {
            String categoryName = getArguments().getString("CATEGORY_NAME");
            if (categoryName != null)
                tvChooseCategory.setText(categoryName);


        }
        return root;
    }

}