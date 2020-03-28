package com.example.cmpt_cobalt.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// class that can parse any CSV and store them
// into a 2d ArrayList of rows + cols
public class ParseCSV {

    private List<List<String>> values = new ArrayList<>();
    private static final String COMMA_SEPARATOR = ",";

    public ParseCSV(InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))) {
            String line;

            while ((line = br.readLine()) != null) {

                // need to add in an extra space in case there are no violations
//                line = line + ", ";
                //String[] lineValues = line.split(COMMA_SEPARATOR);

                String[] lineValues = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
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

    public int getColSize(int row) {
        return values.get(row).size();
    }
}
