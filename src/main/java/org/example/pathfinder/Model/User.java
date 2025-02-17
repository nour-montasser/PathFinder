package org.example.pathfinder.Model;

public class User {
    private int id_user	;
    private String name;
    private String email;
    private String role; // Utilisation de l'énumération Role
    private String password;

    // Constructeur par défaut
    public User() {}

    // Constructeur avec paramètres
    public User(int id, String name, String email, String role, String password) {
        this.id_user	 = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    // Getters et Setters
    public int getId() {
        return id_user	;
    }

    public void setId(int id) {
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
