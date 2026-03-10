package model;

public class Admin extends User {

    private String department;

    // Constructor
    public Admin(int id, String username, String password, String department) {
        super(id, username, password, "ADMIN");
        this.department = department;
    }

    // Getters
    public String getDepartment() {
        return department;
    }

    // Setters
    public void setDepartment(String department) {
        this.department = department;
    }
}
