package fr.enst.budgetapp.ui.list;

import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;


public class EditTransactionFragment extends Fragment {



    private Bundle collectCurrentFormValues(View root) {
        Bundle bundle = new Bundle();
        bundle.putString("PREVIOUS_FRAGMENT", "editTransaction");

        EditText etTransactionAmount = root.findViewById(R.id.etEditTransactionAmount);
        EditText etTransactionDate = root.findViewById(R.id.etEditTransactionDate);
        EditText etTransactionNotes = root.findViewById(R.id.etEditTransactionNotes);
        RadioGroup rgCategory = root.findViewById(R.id.rgEditCategory);
        RadioGroup rgNotifications = root.findViewById(R.id.rgEditNotifications);
        Spinner spinnerRepeat = root.findViewById(R.id.spinnerEditRepeat);

        bundle.putString("MONEY_AMOUNT", etTransactionAmount.getText().toString());
        bundle.putString("TRANSACTION_DATE", etTransactionDate.getText().toString());
        bundle.putString("NOTES", etTransactionNotes.getText().toString());
        bundle.putInt("NOTIF", rgNotifications.getCheckedRadioButtonId());
        bundle.putString("REPEAT", spinnerRepeat.getSelectedItem().toString());

        int selectedTypeId = rgCategory.getCheckedRadioButtonId();
        if (selectedTypeId == -1 && getArguments() != null) {
            String type = getArguments().getString("TRANSACTION_TYPE");
            if (type != null) {
                if (type.equals("Spending")) selectedTypeId = R.id.rbEditSpending;
                else if (type.equals("Income")) selectedTypeId = R.id.rbEditIncome;
                else if (type.equals("Savings")) selectedTypeId = R.id.rbEditSavings;
            }
        }
        bundle.putInt("TYPE", selectedTypeId);

        if (getArguments() != null && getArguments().containsKey("ID")) {
            bundle.putInt("ID", getArguments().getInt("ID"));
        }


        return bundle;
    }



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
            Bundle bundleFrag = collectCurrentFormValues(root);
            Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_categoriesFragment);
        });

        TextView tvChooseCategory = root.findViewById(R.id.tvEditChooseCategory);
        tvChooseCategory.setOnClickListener(v -> {
            Bundle bundleFrag = collectCurrentFormValues(root);
            Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_categoriesFragment, bundleFrag);
        });




        // Retrieve transaction's information to fill
        Bundle args = getArguments();
        int transactionId = -1;
        if (args != null) {
            transactionId = args.getInt("ID", -1);
            Log.d("IDDDD", String.valueOf(transactionId));
            tvChooseCategory.setText(args.getString("CATEGORY_NAME", ""));
            etTransactionAmount.setText(args.getString("MONEY_AMOUNT", ""));
            etTransactionDate.setText(args.getString("TRANSACTION_DATE", ""));
            etTransactionNotes.setText(args.getString("NOTES", ""));

            String type = args.getString("TRANSACTION_TYPE");
            if (type != null) {
                switch (type) {
                    case "Spending": rgCategory.check(rbSpending.getId()); break;
                    case "Income":   rgCategory.check(rbIncome.getId());   break;
                    case "Savings":  rgCategory.check(rbSavings.getId());  break;
                }
            }

            String repeat = args.getString("REPEAT");
            Spinner spinnerRepeat = root.findViewById(R.id.spinnerEditRepeat);
            if (repeat != null) {
                for (int i = 0; i < spinnerRepeat.getCount(); i++) {
                    if (spinnerRepeat.getItemAtPosition(i).toString().equals(repeat)) {
                        spinnerRepeat.setSelection(i);
                        break;
                    }
                }
            }

            boolean notification = args.getBoolean("NOTIFICATION", false);
            if (notification) rbNotificationsOn.setChecked(true);
            else rbNotificationsOff.setChecked(true);


            if (args.containsKey("TYPE")) {
                int selectedId = args.getInt("TYPE", -1);
                if (selectedId != -1) {
                    rgCategory.check(selectedId);
                }
            }

        }


        // Handle Back button click
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_listFragment));


        //Save btn
        int finalTransactionId = transactionId;
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

            Spinner spinnerRepeat = root.findViewById(R.id.spinnerEditRepeat);
            String repeatFrequency = spinnerRepeat.getSelectedItem().toString();

            boolean notification = rbNotificationsOn.isChecked();

            String category = tvChooseCategory.getText().toString();


            amount = amount.replace("€", "").replace(",", ".").trim();

            try {
                double parsed = Double.parseDouble(amount);
                amount = String.format(Locale.getDefault(), "%.2f", parsed);
                amount = amount.replace(".", ",");
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid amount entered", Toast.LENGTH_SHORT).show();
                return;
            }

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


            List<Transaction> transactions = JsonLoader.loadTransactions(getContext());
            if (transactions == null) transactions = new ArrayList<>();

            for (Transaction tx : transactions) {
                if (tx.getId() == finalTransactionId) {
                    tx.setMoneyAmount(amount);
                    tx.setTransactionDate(date);
                    tx.setNotes(notes);
                    tx.setTransactionType(transactionType);
                    tx.setCategoryName(category);
                    Log.d("EditTransaction", "Saving category: " + category);
                    tx.setRepeatFrequency(repeatFrequency);
                    tx.setNotification(notification);
                    break;
                }
            }

            boolean saved = JsonLoader.saveTransactions(getContext(), transactions);
            JsonLoader.recomputeBalancesFromTransactions(getContext());

            if (saved) {
                Toast.makeText(getContext(), "Transaction updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to save changes.", Toast.LENGTH_SHORT).show();
            }

            //Navigation.findNavController(v).navigateUp();
            Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_listFragment);
        });




        btnDelete.setOnClickListener(v ->{
            List<Transaction> transactions = JsonLoader.loadTransactions(getContext());
            if (transactions == null) transactions = new ArrayList<>();


            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getId() == finalTransactionId) {
                    transactions.remove(i);
                    break;
                }
            }


            for (int i = 0; i < transactions.size(); i++) {
                transactions.get(i).setId(i + 1);
            }

            JsonLoader.saveTransactions(getContext(), transactions);
            JsonLoader.recomputeBalancesFromTransactions(getContext());

            Toast.makeText(getContext(), "Transaction deleted!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.action_editTransactionFragment_to_listFragment);



        });

        return root;
    }

}