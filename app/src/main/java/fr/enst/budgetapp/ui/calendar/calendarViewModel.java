package fr.enst.budgetapp.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class calendarViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public calendarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is calendar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
