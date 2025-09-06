package lk.ijse.gdse.main.internbaybackend.entity;

public enum JobType {
    FULLTIME("Full Time"),
    PARTTIME("Part Time"),
    INTERNSHIP("Internship"),
    CONTRACT("Contract"),
    REMOTE("Remote");

    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}