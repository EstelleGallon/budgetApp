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

public class EditTransactionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_transaction, container, false);

        // Initialize UI elements
        EditText etTransactionAmount = root.findViewById(R.id.etEditTransactionAmount);
        EditText etTransactionDate = root.findViewById(R.id.etEditTransactionDate);
        EditText etTransactionNotes = root.findViewById(R.id.etEditTransactionNotes);

        Button btnBack = root.findViewById(R.id.btnBackToList);
        Button btnSave = root.findViewById(R.id.btnSaveEditTransaction);
        Button btnDelete = root.findViewById(R.id.btnDeleteTransaction);

        // TODO: put default values
        RadioGroup rgCategory = root.findViewById(R.id.rgEditCategory);
        RadioButton rbSpending = root.findViewById(R.id.rbEditSpending);
        RadioButton rbIncome = root.findViewById(R.id.rbEditIncome);
        RadioButton rbSavings = root.findViewById(R.id.rbEditSavings);

        RadioGroup rgNotifications = root.findViewById(R.id.rgEditNotifications);
        RadioButton rbNotificationsOff = root.findViewById(R.id.rbEditNotificationsOff);
        RadioButton rbNotificationsOn = root.findViewById(R.id.rbEditNotificationsOn);

        // Handle navigation to Category Page
        // TODO: check the going back from category page
        ImageView btnChooseCategory = root.findViewById(R.id.ivEditChooseCategory);
        btnChooseCategory.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_categoriesFragment);
        });

        TextView tvChooseCategory = root.findViewById(R.id.tvEditChooseCategory);
        tvChooseCategory.setOnClickListener(v -> {
            Bundle bundleFrag = new Bundle();
            bundleFrag.putString("PREVIOUS_FRAGMENT", "edit");
            Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_categoriesFragment, bundleFrag);
        });

        // Handle Back button click
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_listFragment));

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            String amount = etTransactionAmount.getText().toString();
            String date = etTransactionDate.getText().toString();
            String notes = etTransactionNotes.getText().toString();
            // TODO: check other fields
            // TODO: Save modifications on transaction

            // Navigate back to the List page
            //Navigation.findNavController(v).navigateUp();
            Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_listFragment);
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