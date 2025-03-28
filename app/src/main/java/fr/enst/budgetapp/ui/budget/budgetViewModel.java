package fr.enst.budgetapp.ui.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class budgetViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public budgetViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}