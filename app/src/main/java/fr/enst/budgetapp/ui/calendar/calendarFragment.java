package fr.enst.budgetapp.ui.calendar;

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

import fr.enst.budgetapp.databinding.FragmentCalendarBinding;
import fr.enst.budgetapp.ui.calendar.calendarViewModel;
import fr.enst.budgetapp.R;

public class calendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(calendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView addTansaction = root.findViewById(R.id.btnAddTransaction);
        View.OnClickListener addTransactionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("PREVIOUS_MENU", "calendar");
                Navigation.findNavController(v).navigate(R.id.action_calendarFragment_to_newTransactionFragment, bundle);
            }
        };
        addTansaction.setOnClickListener(addTransactionClickListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
