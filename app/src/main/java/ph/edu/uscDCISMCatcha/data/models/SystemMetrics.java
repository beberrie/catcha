package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SystemMetrics {
    private long totalRsvps = 0;
    private long totalGoing = 0;
    private long totalInterested = 0;
    private long totalEvents = 0;
    private long totalOrganizations = 0;
    private long totalUsers = 0;

    public SystemMetrics() {}

    public long getTotalRsvps() { return totalRsvps; }
    public void setTotalRsvps(long totalRsvps) { this.totalRsvps = totalRsvps; }

    public long getTotalGoing() { return totalGoing; }
    public void setTotalGoing(long totalGoing) { this.totalGoing = totalGoing; }

    public long getTotalInterested() { return totalInterested; }
    public void setTotalInterested(long totalInterested) { this.totalInterested = totalInterested; }

    public long getTotalEvents() { return totalEvents; }
    public void setTotalEvents(long totalEvents) { this.totalEvents = totalEvents; }

    public long getTotalOrganizations() { return totalOrganizations; }
    public void setTotalOrganizations(long totalOrganizations) { this.totalOrganizations = totalOrganizations; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
}
