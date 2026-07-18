/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gaza.aid.tracker;

/**
 * Represents a field volunteer: can view distribution records and add new ones,
 * without edit or delete permissions.
 */
public class Volunteer extends User {

    private String assigned_Area;

    // Volunteer constructor
    public Volunteer(int user_ID, String user_Name, String password, String assigned_Area) {
        super(user_ID, user_Name, password, "Volunteer");
        this.assigned_Area = assigned_Area;
    }

    // Get the assigned area for the volunteer
    public String getAssigned_Area() {
        return assigned_Area;
    }

    // Set the assigned area for the volunteer
    public void setAssigned_Area(String assigned_Area) {
        this.assigned_Area = assigned_Area;
    }

    // Define specific permissions for the volunteer role
    @Override
    public String getRolePermissions() {
        return "Permissions: View records and Add new distributions in "
                + assigned_Area + " (Edit and Delete are restricted).";
    }

    // Return volunteer details as a string
    @Override
    public String toString() {
        return super.toString() + "assigned_Area : " + assigned_Area;
    }
}