package ph.edu.uscDCISMCatcha.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import ph.edu.uscDCISMCatcha.models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseRemoteDataSource {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public FirebaseRemoteDataSource() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public Task<AuthResult> signIn(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signUp(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> saveUserProfile(String uid, UserModel user) {
        return firestore.collection("users").document(uid).set(user);
    }

    public Task<DocumentSnapshot> getUserProfile(String uid) {
        return firestore.collection("users").document(uid).get();
    }

    /**
     * Fetches the role of a user from Firestore.
     * @param uid The user's unique ID.
     * @return A Task that resolves to the role string ("Student", "OrgHandler", or "Admin").
     */
    public Task<String> getUserRole(String uid) {
        return getUserProfile(uid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                return task.getResult().getString("role");
            }
            return null;
        });
    }

    public void signOut() {
        auth.signOut();
    }
}
