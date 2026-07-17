/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gaza.aid.tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Responsible for saving and loading distribution records from a UTF-8 text file.
 */
public class DatabaseManager {

    private static final String FILE_NAME = "aid_data.txt";

    // Save the list of aid distributions to the text file
    public static void saveToFile(ArrayList<AidDistribution> list) {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(FILE_NAME), StandardCharsets.UTF_8))) {

            for (AidDistribution dist : list) {
                Family family = dist.getReceiving_Family();

                writer.println(
                        clean(dist.getDistribution_ID()) + ";"
                        + dist.getDistributed_Quantity() + ";"
                        + clean(family.getUserName()) + ";"
                        + family.getFamily_ID() + ";"
                        + family.getMember_Count() + ";"
                        + clean(family.getContact_Number()) + ";"
                        + family.isIs_Displaced() + ";"
                        + clean(family.getLast_Aid_Date()) + ";"
                        + clean(dist.getReceipt_status())
                );
            }
        } catch (Exception ex) {
            System.err.println("Error writing to file: " + ex.getMessage());
        }
    }

    // Load distribution records from the text file
    public static ArrayList<AidDistribution> loadFromFile(Organization defaultOrg) {
        ArrayList<AidDistribution> list = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] data = line.split(";", -1);
                    if (data.length < 9) {
                        System.err.println("Skipped incomplete record at line " + lineNumber);
                        continue;
                    }

                    Family family = new Family(
                            Integer.parseInt(data[3].trim()),
                            data[2].trim(),
                            Integer.parseInt(data[4].trim()),
                            data[5].trim(),
                            Boolean.parseBoolean(data[6].trim()),
                            data[7].trim()
                    );

                    // Use the loading constructor to preserve the saved status
                    AidDistribution distribution = new AidDistribution(
                            data[0].trim(),
                            defaultOrg,
                            family,
                            Integer.parseInt(data[1].trim()),
                            data[8].trim()
                    );

                    list.add(distribution);
                } catch (Exception recordError) {
                    System.err.println("Skipped invalid record at line "
                            + lineNumber + ": " + recordError.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error reading from file: " + ex.getMessage());
        }

        return list;
    }

    // Clean string values to prevent file structure corruption
    private static String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(';', ',')
                .replace('\n', ' ')
                .replace('\r', ' ')
                .trim();
    }
}