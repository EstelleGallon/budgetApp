package fr.enst.budgetapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavingGoalAdapter extends RecyclerView.Adapter<SavingGoalAdapter.SavingGoalViewHolder> {

    private final List<SavingGoal> savingGoals;

    public SavingGoalAdapter(List<SavingGoal> savingGoals) {
        this.savingGoals = savingGoals;
    }

    @NonNull
    @Override
    public SavingGoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saving_goals, parent, false);
        return new SavingGoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavingGoalViewHolder holder, int position) {
        SavingGoal savingGoal = savingGoals.get(position);
        holder.titleTextView.setText(savingGoal.getTitle());
        holder.progressBar.setProgress(savingGoal.getProgressPercentage());
        holder.percentageTextView.setText(savingGoal.getProgressPercentage() + "%");
        holder.deadlineTextView.setText("Deadline: " + savingGoal.getDeadline());
        holder.totalAmountTextView.setText("€" + savingGoal.getTotalAmount());

        // Handle edit icon click
        holder.editIcon.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("goalId", savingGoal.getId()); // Pass the goal ID or details
            Navigation.findNavController(v).navigate(R.id.action_budgetFragment_to_editSavingGoalFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return savingGoals.size();
    }

    public static class SavingGoalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ProgressBar progressBar;
        TextView percentageTextView;
        TextView deadlineTextView;
        TextView totalAmountTextView;
        ImageView editIcon;

        public SavingGoalViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvSavingGoalTitle);
            progressBar = itemView.findViewById(R.id.progressBarSavingGoal);
            percentageTextView = itemView.findViewById(R.id.tvSavingGoalPercentage);
            deadlineTextView = itemView.findViewById(R.id.tvSavingGoalDeadline);
            totalAmountTextView = itemView.findViewById(R.id.tvSavingGoalTotalAmount);
            editIcon = itemView.findViewById(R.id.ivEditSavingGoal);
        }
    }
}