package fr.enst.budgetapp.ui.calendar;

import static fr.enst.budgetapp.Transaction.getTransactionsOfTheDay;
import static fr.enst.budgetapp.Transaction.getWeekday;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.Transaction;
import fr.enst.budgetapp.databinding.FragmentCalendarBinding;
import fr.enst.budgetapp.ui.calendar.calendarViewModel;
import fr.enst.budgetapp.R;

public class calendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private List<Transaction> transactions;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(calendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView addTransaction = root.findViewById(R.id.btnAddTransaction);
        View.OnClickListener addTransactionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("PREVIOUS_MENU", "calendar");
                Navigation.findNavController(v).navigate(R.id.action_calendarFragment_to_newTransactionFragment, bundle);
            }
        };
        addTransaction.setOnClickListener(addTransactionClickListener);

        transactions = JsonLoader.loadTransactions(getContext());
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        // Add events to the calendar
        List<CalendarDay> calendarDays = new ArrayList<>();
        for (Transaction transaction : transactions) {
            Calendar calendar = Calendar.getInstance();

            // get date of transaction
            calendar.setTime(transaction.getDate());
            CalendarDay calendarDay = new CalendarDay(calendar);

            // set right icon according to type of transaction
            String transactionType = transaction.getTransactionType();

            if (Objects.equals(transactionType, "Spending")) {
                calendarDay.setImageResource(R.drawable.ic_circle_filled_red);
            } else if (Objects.equals(transactionType, "Income"))
                calendarDay.setImageResource(R.drawable.ic_circle_filled_green);
            else if (Objects.equals(transactionType, "Savings"))
                calendarDay.setImageResource(R.drawable.ic_circle_filled_yellow);

            calendarDays.add(calendarDay);

        }

        // bind calendar view
        CalendarView calendarView = root.findViewById(R.id.calendarView);
        calendarView.setCalendarDays(calendarDays);

        calendarView.setOnCalendarDayClickListener(new OnCalendarDayClickListener() {
            @Override
            public void onClick(@NonNull CalendarDay calendarDay) {
                Calendar clickedDayCalendar = calendarDay.getCalendar();
                try {
                    showDayTransactions(clickedDayCalendar);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        return root;
    }

    private void showDayTransactions(Calendar clickedDayCalendar) throws ParseException {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // TODO: connect date to transactions of the day


        // format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(clickedDayCalendar.getTime());


        builder.setTitle(getWeekday(formattedDate));

        // list the transactions
        List<Transaction> transactionsOfTheDay = getTransactionsOfTheDay(transactions, formattedDate);
        String message = "";
        for (Transaction transaction : transactionsOfTheDay) {
            message += transaction.getTransactionType() + ": ";
            message += transaction.getCategoryName() + " ";
            message += transaction.getMoneyAmount() + "\n";
        }

        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
