package model;

public class Apartment {

    private int id;
    private String name;
    private String estate;
    private String address;
    private int numberOfUnits;
    private int caretakerId;
    private Caretaker caretaker;

    // Original constructor - with Caretaker object
    public Apartment(int id, String name, String estate,
            String address, int numberOfUnits, Caretaker caretaker) {
        this.id = id;
        this.name = name;
        this.estate = estate;
        this.address = address;
        this.numberOfUnits = numberOfUnits;
        this.caretaker = caretaker;
    }

    // Simplified constructor - without Caretaker object
    public Apartment(int id, String name, String estate,
            String address, int numberOfUnits) {
        this.id = id;
        this.name = name;
        this.estate = estate;
        this.address = address;
        this.numberOfUnits = numberOfUnits;
        this.caretaker = null;
        this.caretakerId = 0;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEstate() {
        return estate;
    }

    public String getAddress() {
        return address;
    }

    public int getNumberOfUnits() {
        return numberOfUnits;
    }

    public Caretaker getCaretaker() {
        return caretaker;
    }

    public int getCaretakerId() {
        return caretakerId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEstate(String estate) {
        this.estate = estate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNumberOfUnits(int numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public void setCaretaker(Caretaker caretaker) {
        this.caretaker = caretaker;
    }

    public void setCaretakerId(int caretakerId) {
        this.caretakerId = caretakerId;
    }
}