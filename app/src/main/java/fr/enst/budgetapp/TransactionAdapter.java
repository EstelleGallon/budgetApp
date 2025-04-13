package fr.enst.budgetapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final List<Transaction> transactions;
    private boolean showDate;
    private String currentFragment = "";

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
        this.showDate = true;
    }

    public TransactionAdapter(List<Transaction> transactions, boolean showDate, String currentFragment) {
        this.transactions = transactions;
        this.showDate = showDate;
        this.currentFragment = currentFragment;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.categoryNameTextView.setText(transaction.getCategoryName());

        String sign = "";
        if (transaction.getTransactionType().equalsIgnoreCase("Income")) {
            sign = "+";
        } else {
            sign = "-";
        }

        holder.moneyAmountTextView.setText( sign + transaction.getMoneyAmount()  );
        if (showDate) {
            holder.transactionDateTextView.setVisibility(View.VISIBLE);
            holder.transactionDateTextView.setText(transaction.getTransactionDate());
        } else {
            holder.transactionDateTextView.setVisibility(View.GONE);

        }



        View.OnClickListener editTransactionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_listFragment_to_editTransactionFragment);
            }
        };
        //holder.transactionArea.setOnClickListener(editTransactionClickListener);
        holder.transactionArea.setOnClickListener(v -> {
            Transaction selectedTx = transactions.get(position);

            Bundle bundle = new Bundle();
            bundle.putInt("ID", selectedTx.getId());
            Log.d("ID2", String.valueOf(selectedTx.getId()));
            bundle.putString("CATEGORY_NAME", selectedTx.getCategoryName());
            bundle.putString("TRANSACTION_TYPE", selectedTx.getTransactionType());
            bundle.putString("MONEY_AMOUNT", selectedTx.getMoneyAmount());
            bundle.putString("TRANSACTION_DATE", selectedTx.getTransactionDate());
            bundle.putString("REPEAT", selectedTx.getRepeatFrequency());
            bundle.putBoolean("NOTIFICATION", selectedTx.getNotification());
            bundle.putString("NOTES", selectedTx.getNotes());

            if (Objects.equals(currentFragment, "list")) {
                Navigation.findNavController(v).navigate(
                        R.id.action_listFragment_to_editTransactionFragment, bundle
                );
            }

        });





    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        TextView moneyAmountTextView;
        TextView transactionDateTextView;
        LinearLayout transactionArea;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.tvCategoryName);
            moneyAmountTextView = itemView.findViewById(R.id.tvMoneyAmount);
            transactionDateTextView = itemView.findViewById(R.id.tvTransactionDate);
            transactionArea = itemView.findViewById(R.id.llTransaction);
        }
    }
}