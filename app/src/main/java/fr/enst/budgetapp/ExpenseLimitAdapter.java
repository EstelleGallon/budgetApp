package fr.enst.budgetapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseLimitAdapter extends RecyclerView.Adapter<ExpenseLimitAdapter.ExpenseLimitViewHolder> {

    private final List<ExpenseLimit> expenseLimits;
    private final Context context;

    public ExpenseLimitAdapter(List<ExpenseLimit> expenseLimits, Context context) {
        this.expenseLimits = expenseLimits;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenseLimitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense_limits, parent, false);
        return new ExpenseLimitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseLimitViewHolder holder, int position) {
        ExpenseLimit expenseLimit = expenseLimits.get(position);
        holder.tvCategoryName.setText(expenseLimit.getCategoryName());
        holder.llMonthsContainer.removeAllViews();

        // Add months dynamically
        String[] months = {"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"};
        for (int i = 0; i < months.length; i++) {
            TextView monthView = new TextView(context);
            monthView.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
            monthView.setGravity(android.view.Gravity.CENTER);
            monthView.setText(months[i]);
            monthView.setTextSize(16);
            monthView.setTextColor(Color.BLACK);
            monthView.setBackgroundResource(R.drawable.month_background);
            ((LinearLayout.LayoutParams) monthView.getLayoutParams()).setMargins(8, 0, 8, 0);

            // Set background color based on expense limit status
            if (expenseLimit.isExceeded(i)) {
                monthView.setBackgroundColor(Color.RED); // Exceeded
            } else {
                monthView.setBackgroundColor(Color.GREEN); // Not exceeded
            }

            holder.llMonthsContainer.addView(monthView);
        }

        holder.btnEditExpenseLimit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("EXPENSE_LIMIT_ID", String.valueOf(expenseLimit.getId()));  // 🔑 must have
            bundle.putString("CATEGORY_NAME", expenseLimit.getCategoryName());
            bundle.putStringArray("EXCEEDED_MONTHS", convertBooleanArrayToString(expenseLimit.getExceededMonths()));

            Navigation.findNavController(v).navigate(R.id.action_budgetFragment_to_editExpenseLimitFragment, bundle);
        });

    }




    private String[] convertBooleanArrayToString(Boolean[] input) {
        String[] result = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = (input[i] == null) ? "null" : input[i].toString();
        }
        return result;
    }




    @Override
    public int getItemCount() {
        return expenseLimits.size();
    }

    public static class ExpenseLimitViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        LinearLayout llMonthsContainer;
        ImageView btnEditExpenseLimit;

        public ExpenseLimitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            llMonthsContainer = itemView.findViewById(R.id.llMonthsContainer);
            btnEditExpenseLimit = itemView.findViewById(R.id.btnEditExpenseLimit);
        }
    }
}