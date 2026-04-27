package ph.edu.uscDCISMCatcha.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignupViewModel extends ViewModel {
    private final MutableLiveData<String> university = new MutableLiveData<>();
    private final MutableLiveData<String> role = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();

    public void setUniversity(String value) { university.setValue(value); }
    public void setRole(String value) { role.setValue(value); }
    public void setEmail(String value) { email.setValue(value); }
    public void setFirstName(String value) { firstName.setValue(value); }
    public void setLastName(String value) { lastName.setValue(value); }
    public void setPassword(String value) { password.setValue(value); }
    public void setUsername(String value) { username.setValue(value); }

    public LiveData<String> getUniversity() { return university; }
    public LiveData<String> getRole() { return role; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getFirstName() { return firstName; }
    public LiveData<String> getLastName() { return lastName; }
    public LiveData<String> getPassword() { return password; }
    public LiveData<String> getUsername() { return username; }
}
