package com.example.cmpt_cobalt.ca.cmpt276A3.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseCSV {
    private List<List<String>> values = new ArrayList<>();
    private static final String COMMA_SEPARATOR = ",";

    // parses the file and stores values into a 2d ArrayList
    // get the values by using passing in row + col into getter method
    // note: need to pass in the complete file path
    public ParseCSV(String filePath) {
        //InputStream is = getClass().getClassLoader().getResourceAsStream("raw/inspectionreports_itr1.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] lineValues = line.split(COMMA_SEPARATOR);
                values.add(Arrays.asList(lineValues));
            }

        } catch (FileNotFoundException e) {
            Log.wtf("ParseCSV", "File not found ", e);
            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public String getVal(int row, int col) {
        return values.get(row).get(col);
    }

    public int getRowSize() {
        return values.size();
    }

    public int getColSize() {
        return values.get(0).size();
    }
}
