package fr.enst.budgetapp;

import static fr.enst.budgetapp.Transaction.sumMoneyAmount;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

public class DateTransactionsAdapter extends RecyclerView.Adapter<DateTransactionsAdapter.DateViewHolder> {
    private List<Pair<String, List<Transaction>>> dataList;

    public DateTransactionsAdapter(List<Pair<String, List<Transaction>>> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Pair<String, List<Transaction>> item = dataList.get(position);
        String date = item.first;
        List<Transaction> transactions = item.second;

        // Set date header
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.dateHeader.setText(date.equals(LocalDate.now().toString()) ? "Today" : date);
        }

        // set daily balance header
        String dailyAmount = sumMoneyAmount(transactions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.dailyBalanceHeader.setText(dailyAmount);
        }

        // Set up the inner RecyclerView for transactions
        TransactionAdapter transactionAdapter = new TransactionAdapter(transactions);
        holder.transactionRecyclerView.setAdapter(transactionAdapter);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeader;
        TextView dailyBalanceHeader;
        RecyclerView transactionRecyclerView;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.dateHeader);
            dailyBalanceHeader = itemView.findViewById(R.id.dailyBalanceHeader);
            transactionRecyclerView = itemView.findViewById(R.id.transactionRecyclerView);
            transactionRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}

