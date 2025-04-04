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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ComputableLiveData;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;

public class NewTransactionFragment extends Fragment {



    private Bundle collectCurrentFormValues(View root) {
        Bundle bundle = new Bundle();
        bundle.putString("PREVIOUS_FRAGMENT", "newTransaction");

        EditText etTransactionAmount = root.findViewById(R.id.etTransactionAmount);
        EditText etTransactionDate = root.findViewById(R.id.etTransactionDate);
        EditText etTransactionNotes = root.findViewById(R.id.etTransactionNotes);
        RadioGroup rgCategory = root.findViewById(R.id.rgCategory);
        RadioGroup rgNotifications = root.findViewById(R.id.rgNotifications);

        bundle.putString("AMOUNT", etTransactionAmount.getText().toString());
        bundle.putString("DATE", etTransactionDate.getText().toString());
        bundle.putString("NOTES", etTransactionNotes.getText().toString());
        bundle.putInt("TYPE", rgCategory.getCheckedRadioButtonId());
        bundle.putInt("NOTIF", rgNotifications.getCheckedRadioButtonId());

        return bundle;
    }


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
            Bundle bundleFrag = collectCurrentFormValues(root);
            Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_categoriesFragment, bundleFrag);
        });

        // Save current form values in a bundle before navigating to the Categories screen,
        // so that we can restore them when coming back to this fragment

        TextView tvChooseCategory = root.findViewById(R.id.tvChooseCategory);
        tvChooseCategory.setOnClickListener(v -> {
            Bundle bundleFrag = collectCurrentFormValues(root);
            Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_categoriesFragment, bundleFrag);
        });

        // Handle Back button click
        btnBack.setOnClickListener(v -> {
            if (getArguments() != null) {
                String previousMenu = getArguments().getString("PREVIOUS_MENU");
                System.out.println("previous page: " + previousMenu);
                if (Objects.equals(previousMenu, "calendar"))
                    Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_calendarFragment);
                else if (Objects.equals(previousMenu, "list"))
                    Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_listFragment);
            }

        });

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            String amount = etTransactionAmount.getText().toString();
            String date = etTransactionDate.getText().toString();
            String notes = etTransactionNotes.getText().toString();

            String transactionType="";
            int selectedTypeId = rgCategory.getCheckedRadioButtonId();
            if (selectedTypeId == rbSpending.getId()) {
                transactionType = "Spending";
            } else if (selectedTypeId == rbIncome.getId()) {
                transactionType = "Income";
            } else if (selectedTypeId == rbSavings.getId()) {
                transactionType = "Savings";
            }

            Spinner spinnerRepeat = root.findViewById(R.id.spinnerRepeat);
            String repeatFrequency = spinnerRepeat.getSelectedItem().toString();

            boolean notification = rbNotificationsOn.isChecked();

            String category = tvChooseCategory.getText().toString();

            if (!amount.endsWith("€")) {
                amount += "€";
            }

            if (date.isEmpty()) {
                date = android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString();
            }

            if (category.isEmpty() || amount.isEmpty() || repeatFrequency.isEmpty() || rgNotifications.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }


            // TODO: Save the new transaction

            List<Transaction> transactions = fr.enst.budgetapp.JsonLoader.loadTransactions(getContext());
            if (transactions == null) transactions = new ArrayList<>();


            int newId = transactions.size() + 1;
            fr.enst.budgetapp.Transaction newTx = new fr.enst.budgetapp.Transaction(
                    newId,
                    transactionType,
                    category,
                    amount,
                    date,
                    repeatFrequency,
                    notification,
                    notes
            );

            transactions.add(newTx);
            boolean saved = fr.enst.budgetapp.JsonLoader.saveTransactions(getContext(), transactions);

            fr.enst.budgetapp.JsonLoader.recomputeBalancesFromTransactions(getContext());

            if (saved) {
                Toast.makeText(getContext(), "Transaction saved!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_listFragment);
            } else {
                Toast.makeText(getContext(), "Failed to save transaction", Toast.LENGTH_SHORT).show();
            }


            // Navigate back to the List page
           // Navigation.findNavController(v).navigateUp();
        });



        Bundle args = getArguments();
        if (args != null) {

            String category = args.getString("CATEGORY_NAME");
            if (category != null) {
                tvChooseCategory.setText(category);
            }

            int selectedTypeId = args.getInt("TYPE", -1);
            if (selectedTypeId != -1) rgCategory.check(selectedTypeId);

            int notifId = args.getInt("NOTIF", -1);
            if (notifId != -1) rgNotifications.check(notifId);


            String amount = args.getString("AMOUNT");
            if (amount != null) {
                etTransactionAmount.setText(amount);
            }

            String date = args.getString("DATE");
            if (date != null) {
                etTransactionDate.setText(date);
            }

            String notes = args.getString("NOTES");
            if (notes != null) {
                etTransactionNotes.setText(notes);
            }

            String repeat = args.getString("REPEAT");
            Spinner spinnerRepeat = root.findViewById(R.id.spinnerRepeat);
            if (repeat != null) {
                for (int i = 0; i < spinnerRepeat.getCount(); i++) {
                    if (spinnerRepeat.getItemAtPosition(i).toString().equals(repeat)) {
                        spinnerRepeat.setSelection(i);
                        break;
                    }
                }
            }


        }


        /*
        // retrieve chosen category name from bundle
        if (getArguments() != null) {
            String categoryName = getArguments().getString("CATEGORY_NAME");
            if (categoryName != null)
                tvChooseCategory.setText(categoryName);
        }

         */


        return root;
    }

}