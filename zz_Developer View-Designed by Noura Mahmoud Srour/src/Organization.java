/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gaza.aid.tracker;

/**
 * Represents a humanitarian organization user.
 */
public class Organization extends User {

    private String coverage_Area;

    // Organization constructor
    public Organization(int user_ID, String user_Name, String password, String user_Type, String coverage_Area) {
        super(user_ID, user_Name, password, user_Type);
        this.coverage_Area = coverage_Area;
    }

    // Get the coverage area of the organization
    public String getCoverage_Area() {
        return coverage_Area;
    }

    // Set the coverage area of the organization
    public void setCoverage_Area(String coverage_Area) {
        this.coverage_Area = coverage_Area;
    }

    // Define specific permissions for the organization role
    @Override
    public String getRolePermissions() {
        return "Permissions: Add Aid Items, Update Inventory, and Distriute Resources in " + coverage_Area;
    }

    // Return organization details as a string
    @Override
    public String toString() {
        return super.toString() + "coverage_Area : " + coverage_Area;
    }

}