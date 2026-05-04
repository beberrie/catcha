package ph.edu.uscDCISMCatcha.data.seeding;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Calendar;
import java.util.Date;

import ph.edu.uscDCISMCatcha.data.models.AnnouncementModel;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.MembershipModel;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.data.models.RSVPModel;
import ph.edu.uscDCISMCatcha.data.models.UserModel;

public class DatabaseSeeder {

    private static final String TAG = "DatabaseSeeder";

    public interface SeedingCallback {
        void onComplete();
        void onError(Exception e);
    }

    public static void seedDatabase(SeedingCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // 1. Seed Sample Users
        String user1Id = "sample_user_1";
        UserModel user1 = new UserModel("John", "Doe", "johndoe", "john@usc.edu.ph", "University of San Carlos", "DCISM", "Student");
        batch.set(db.collection("users").document(user1Id), user1);

        String adminId = "sample_admin_1";
        UserModel admin = new UserModel("Admin", "User", "admin", "admin@usc.edu.ph", "University of San Carlos", "Administration", "Admin");
        batch.set(db.collection("users").document(adminId), admin);

        // 2. Seed Sample Organization
        String org1Id = "sample_org_1";
        Organization org1 = new Organization(
                "Computer Science Society",
                "University of San Carlos",
                "School of Engineering",
                "DCISM",
                "The official student organization of the DCISM.",
                "Academic",
                "https://example.com/css_logo.png",
                user1Id
        );
        batch.set(db.collection("organizations").document(org1Id), org1);

        // 3. Seed Sample Membership
        String membershipId = user1Id + "_" + org1Id;
        MembershipModel membership = new MembershipModel(user1Id, org1Id, "Computer Science Society", "President", "Active");
        batch.set(db.collection("memberships").document(membershipId), membership);

        // 4. Seed Sample Event
        String event1Id = "sample_event_1";
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date nextWeek = cal.getTime();

        EventModel event1 = new EventModel(
                org1Id,
                "Computer Science Society",
                "Hackathon 2024",
                "Annual coding competition for all students.",
                "DCISM Lobby",
                now,
                nextWeek,
                "University of San Carlos",
                "https://example.com/hackathon.png",
                user1Id
        );
        event1.setCreatedAt(now); // Added to ensure it shows up in queries ordered by createdAt
        batch.set(db.collection("events").document(event1Id), event1);

        // 5. Seed Sample RSVP
        String rsvpId = user1Id + "_" + event1Id;
        RSVPModel rsvp = new RSVPModel(user1Id, event1Id, "Hackathon 2024", "Going");
        batch.set(db.collection("rsvps").document(rsvpId), rsvp);

        // 6. Seed Sample Announcements (Moved to top-level collection and added timestamps)
        String announce1Id = "sample_announce_1";
        AnnouncementModel announce1 = new AnnouncementModel("Welcome!", "Welcome to the CSS Organization. Stay tuned for more updates!", user1Id);
        announce1.setOrgName("Computer Science Society");
        announce1.setTimestamp(new Date());
        batch.set(db.collection("announcements").document(announce1Id), announce1);

        String announce2Id = "sample_announce_2";
        AnnouncementModel announce2 = new AnnouncementModel("General Assembly", "We will have our first General Assembly this Friday at 4 PM in the DCISM Lab.", user1Id);
        announce2.setOrgName("Computer Science Society");
        announce2.setTimestamp(new Date());
        batch.set(db.collection("announcements").document(announce2Id), announce2);

        String announce3Id = "sample_announce_3";
        AnnouncementModel announce3 = new AnnouncementModel("Hackathon Registration", "Registration for Hackathon 2024 is now open! Visit the CSS office for more details.", user1Id);
        announce3.setOrgName("Computer Science Society");
        announce3.setTimestamp(new Date());
        batch.set(db.collection("announcements").document(announce3Id), announce3);

        // Commit all changes
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Database seeded successfully!");
                if (callback != null) callback.onComplete();
            } else {
                Log.e(TAG, "Error seeding database", task.getException());
                if (callback != null) callback.onError(task.getException());
            }
        });
    }
}
