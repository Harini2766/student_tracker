package dto;

// Used for both loading (GET) and updating (PUT) staff settings
public class StaffSettingsDTO {

    private String fullName;
    private String email;
    private String password; // Only used for updates (optional)

    // Default Constructor
    public StaffSettingsDTO() {}

    // Constructor for loading profile data
    public StaffSettingsDTO(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    // --- Getters and Setters ---
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}