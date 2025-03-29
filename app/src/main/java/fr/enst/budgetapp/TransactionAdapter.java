package fr.enst.budgetapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
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
        holder.moneyAmountTextView.setText(transaction.getMoneyAmount());
        holder.transactionDateTextView.setText(transaction.getTransactionDate());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        TextView moneyAmountTextView;
        TextView transactionDateTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.tvCategoryName);
            moneyAmountTextView = itemView.findViewById(R.id.tvMoneyAmount);
            transactionDateTextView = itemView.findViewById(R.id.tvTransactionDate);
        }
    }
}