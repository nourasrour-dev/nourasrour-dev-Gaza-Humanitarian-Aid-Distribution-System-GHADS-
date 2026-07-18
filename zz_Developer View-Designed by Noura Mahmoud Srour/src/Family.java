/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gaza.aid.tracker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Represents the beneficiary family data[cite: 18].
 */
public class Family {

    // Data Fields: Store family identification, demographic, and aid history information
    private int family_ID;
    private String family_Name;
    private int member_Count;
    private String contact_Number;
    private boolean is_Displaced;
    private String last_Aid_Date;

    // Constant: Define the standard ISO date format
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    // Constructor: Initializes a new family record
    public Family(int family_ID, String family_Name, int member_Count,
            String contact_Number, boolean is_Displaced, String last_Aid_Date) {
        this.family_ID = family_ID;
        this.family_Name = family_Name;
        this.member_Count = member_Count;
        this.contact_Number = contact_Number;
        this.is_Displaced = is_Displaced;
        this.last_Aid_Date = last_Aid_Date;
    }

    // Getter Method: Returns family ID
    public int getFamily_ID() {
        return family_ID;
    }

    // Setter Method: Updates family ID
    public void setFamily_ID(int family_ID) {
        this.family_ID = family_ID;
    }

    // Getter Method: Returns family name
    public String getFamily_Name() {
        return family_Name;
    }

    // Setter Method: Updates family name
    public void setFamily_Name(String family_Name) {
        this.family_Name = family_Name;
    }

    // Getter Method: Returns number of family members
    public int getMember_Count() {
        return member_Count;
    }

    // Setter Method: Updates member count
    public void setMember_Count(int member_Count) {
        this.member_Count = member_Count;
    }

    // Getter Method: Returns contact number
    public String getContact_Number() {
        return contact_Number;
    }

    // Deprecated Method: Legacy method kept for backward compatibility
    @Deprecated
    public String isContact_Number() {
        return getContact_Number();
    }

    // Setter Method: Updates contact number
    public void setContact_Number(String contact_Number) {
        this.contact_Number = contact_Number;
    }

    // Getter Method: Checks if the family is displaced
    public boolean isIs_Displaced() {
        return is_Displaced;
    }

    // Setter Method: Updates displacement status
    public void setIs_Displaced(boolean is_Displaced) {
        this.is_Displaced = is_Displaced;
    }

    // Getter Method: Returns the date of last aid
    public String getLast_Aid_Date() {
        return last_Aid_Date;
    }

    // Setter Method: Updates the last aid date
    public void setLast_Aid_Date(String last_Aid_Date) {
        this.last_Aid_Date = last_Aid_Date;
    }

    // Logic Method: Normalizes aid date input to "Never" or valid yyyy-MM-dd format
    public static String normalizeLastAidDate(String value) {
        if (value == null) {
            return "Never";
        }

        String normalized = value.trim();
        if (normalized.isEmpty()
                || normalized.equalsIgnoreCase("Never")
                || normalized.startsWith("لم يتلق")
                || normalized.equals("لا يوجد")) {
            return "Never";
        }

        LocalDate.parse(normalized, DATE_FORMAT);
        return normalized;
    }

    // Logic Method: Determines aid eligibility based on the 30-day rule
    public boolean isEligibleForAid() {
        if (last_Aid_Date == null
                || last_Aid_Date.trim().isEmpty()
                || last_Aid_Date.equalsIgnoreCase("Never")
                || last_Aid_Date.trim().startsWith("لم يتلق")) {
            return true;
        }

        try {
            LocalDate lastDate = LocalDate.parse(last_Aid_Date.trim(), DATE_FORMAT);
            long daysSinceLastAid = ChronoUnit.DAYS.between(lastDate, LocalDate.now());
            return daysSinceLastAid >= 30;
        } catch (Exception ex) {
            return true;
        }
    }

    // Getter Method: Returns the user name associated with the family
    public String getUserName() {
        return family_Name;
    }

    // Method: Returns a string representation of family details
    @Override
    public String toString() {
        return "Family ID: " + family_ID
                + "\nFamily Name: " + family_Name
                + "\nMembers: " + member_Count
                + "\nContact Number: " + contact_Number
                + "\nDisplaced: " + (is_Displaced ? "Yes" : "No")
                + "\nLast Aid Date: " + last_Aid_Date;
    }
}