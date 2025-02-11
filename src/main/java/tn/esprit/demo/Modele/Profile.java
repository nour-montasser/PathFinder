package tn.esprit.demo.Modele;

import java.sql.Date;

public class Profile {
    private int id_user	;
    private String address;
    private Date birthday;
    private String phone;
    private String current_occupation;
    private String photo;
    private String bio;

    public Profile() {
    }

    public Profile(int id_user, String address, Date birthday, String phone, String current_occupation, String photo, String bio) {
        this.id_user = id_user;
        this.address = address;
        this.birthday = birthday;
        this.phone = phone;
        this.current_occupation = current_occupation;
        this.photo = photo;
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id_user=" + id_user +
                ", address='" + address + '\'' +
                ", birthday=" + birthday +
                ", phone='" + phone + '\'' +
                ", current_occupation='" + current_occupation + '\'' +
                ", photo='" + photo + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrent_occupation() {
        return current_occupation;
    }

    public void setCurrent_occupation(String current_occupation) {
        this.current_occupation = current_occupation;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
