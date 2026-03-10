package model;

public class Caretaker extends User {

    private String phone;

    // Constructor
    public Caretaker(int id, String username, String password, String phone) {
        super(id, username, password, "CARETAKER");
        this.phone = phone;
    }

    // Getters
    public String getPhone() {
        return phone;
    }

    // Setters
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
