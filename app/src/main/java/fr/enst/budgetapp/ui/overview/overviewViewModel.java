package fr.enst.budgetapp.ui.overview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class overviewViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public overviewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}