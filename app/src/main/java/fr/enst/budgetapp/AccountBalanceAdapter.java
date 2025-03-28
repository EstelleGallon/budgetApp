package fr.enst.budgetapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccountBalanceAdapter extends RecyclerView.Adapter<AccountBalanceAdapter.BalanceViewHolder> {
    private final List<Pair<String, String>> balances;

    // Constructor
    public AccountBalanceAdapter(List<Pair<String, String>> balances) {
        this.balances = balances;
    }

    // This method returns the layout
    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account_balance, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        Pair<String, String> balance = balances.get(position);
        holder.accountTypeTextView.setText(balance.first); // Account type
        holder.accountBalanceTextView.setText(balance.second); // Account balance
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    public static class BalanceViewHolder extends RecyclerView.ViewHolder {
        TextView accountTypeTextView;
        TextView accountBalanceTextView;

        public BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            accountTypeTextView = itemView.findViewById(R.id.tvAccountType);
            accountBalanceTextView = itemView.findViewById(R.id.tvAccountBalance);
        }
    }

}
