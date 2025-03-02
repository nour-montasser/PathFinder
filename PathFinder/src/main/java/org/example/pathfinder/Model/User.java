package org.example.pathfinder.Model;

public class User {
    private long id_user	;
    private String name;
    private String email;
    private String role; // Utilisation de l'énumération Role
    private String password;
    private String image;

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User(long id_user, String name, String email, String role, String password, String image) {
        this.id_user = id_user;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.image = image;
    }

    // Constructeur par défaut
    public User() {}

    // Constructeur avec paramètres


    // Getters et Setters
    public long getId() {
        return id_user	;
    }

    public void setId(long id) {
        this.id_user = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id_user	 +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", password='" + password + '\'' +
                '}';
    }
}
