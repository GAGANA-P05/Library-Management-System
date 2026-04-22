package com.library.model;

public class Librarian {
    private String librarianId;
    private String employeeId;
    private String name;
    private String email;
    private String phoneNumber;
    private String workingHours;

    public Librarian(String librarianId, String employeeId, String name,
                     String email, String phoneNumber, String workingHours) {
        this.librarianId = librarianId;
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.workingHours = workingHours;
    }

    public String getLibrarianId() { return librarianId; }
    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getWorkingHours() { return workingHours; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    @Override
    public String toString() {
        return "Librarian{id='" + librarianId + "', name='" + name + "', hours='" + workingHours + "'}";
    }
}
