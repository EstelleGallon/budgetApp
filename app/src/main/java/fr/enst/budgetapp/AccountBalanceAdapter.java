package fr.enst.budgetapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountBalanceAdapter extends RecyclerView.Adapter<AccountBalanceAdapter.BalanceViewHolder> {
    private final List<Pair<String, String>> balances;
    private final double upcomingTotal;

    // Constructor
    public AccountBalanceAdapter(List<Pair<String, String>> balances, double upcomingTotal) {
        this.balances = balances;
        this.upcomingTotal = upcomingTotal;
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

        if (position == 0) {
            String formattedIncoming = String.format(Locale.FRANCE, "%.2f€", upcomingTotal).replace(".", ",");
            holder.incomingAmountTextView.setText("Incoming: " + formattedIncoming);
        } else {
            holder.incomingAmountTextView.setText("");
        }


        // update circle based on position
        if (position == 0) {
            holder.circleChecking.setImageResource(R.drawable.ic_circle_filled);
            holder.circleSaving.setImageResource(R.drawable.ic_circle_unfilled);
        } else {
            holder.circleChecking.setImageResource(R.drawable.ic_circle_unfilled);
            holder.circleSaving.setImageResource(R.drawable.ic_circle_filled);
        }
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    public static class BalanceViewHolder extends RecyclerView.ViewHolder {
        TextView accountTypeTextView;
        TextView accountBalanceTextView;
        TextView incomingAmountTextView;
        ImageView circleSaving;
        ImageView circleChecking;

        public BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            accountTypeTextView = itemView.findViewById(R.id.tvAccountType);
            accountBalanceTextView = itemView.findViewById(R.id.tvAccountBalance);
            incomingAmountTextView = itemView.findViewById(R.id.tvIncomingAmount);
            circleSaving = itemView.findViewById(R.id.circleSaving);
            circleChecking = itemView.findViewById(R.id.circleChecking);
        }
    }

}
