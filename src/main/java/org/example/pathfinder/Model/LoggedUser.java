package org.example.pathfinder.Model;

public class LoggedUser {
    private static LoggedUser instance;
    private long userId;
    private String role;
    private String email;
    private String password;
    private String name;
    private String image;


    // Private constructor to prevent instantiation
    private LoggedUser() {}

    // Get the instance
    public static LoggedUser getInstance() {
        if (instance == null) {
            instance = new LoggedUser();
        }
        return instance;
    }

    // Set the logged-in user ID
    public void setUserId(long userId) {
        this.userId = userId;
    }

    // Get the logged-in user ID
    public long getUserId() {
        return userId;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getImage() {
        return image;
    }

}
