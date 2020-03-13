package com.example.cmpt_cobalt.model;

import android.util.Log;

import com.example.cmpt_cobalt.R;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Inspection {

    private int hazardIcon;
    private String formattedDate;
    private String trackingNumber;
    private String inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private String[] violations;

    //TODO: Inspection Date not displayed in proper format (yyyy-mm-dd or yyyy/mm/dd)
    public Inspection(
            String trackingNumber,
            String inspectionDate,
            String inspectionType,
            int numCritical,
            int numNonCritical,
            String hazardRating,
            String violations) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violations = parseViolations(violations);
        this.formattedDate = dateFormatter();
    }

    //https://www.baeldung.com/java-date-difference
    public String dateFormatter(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            String[] indexToMonth = new DateFormatSymbols().getMonths();

            String rawInspectionDate = this.getInspectionDate();
            Date inspectionDate = sdf.parse(rawInspectionDate);
            Date currentDate = new Date();

            long diffInMS = Math.abs(currentDate.getTime() - inspectionDate.getTime());
            long diffInDay = TimeUnit.DAYS.convert(diffInMS, TimeUnit.MILLISECONDS);

            //https://stackoverflow.com/questions/36370895/getyear-getmonth-getday-are-deprecated-in-calendar-what-to-use-then
            Calendar inspectionCalendar = Calendar.getInstance();
            inspectionCalendar.setTime(inspectionDate);

            if (diffInDay <= 1){ return diffInDay + "Day"; }
            else if (diffInDay <= 30){ return diffInDay + " Days"; }
            else if (diffInDay <= 365){
                return indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.DAY_OF_MONTH);
            }
            else {
                return indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.YEAR);
            }
        }
        catch (Exception e){
            return "N/A";
        }
    }

    private String[] parseViolations(String rawViolations) {
        return rawViolations.split("\\|");
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public void setRawViolations(String violations) {
        setViolations(parseViolations(violations));
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String[] getViolations() {
        return this.violations;
    }

    public void setViolations(String[] violations) {
        this.violations = violations;
    }


    public int getHazardIcon() {
        if (hazardRating.equals("\"Low\"")) {
            return R.drawable.green;
        } else if (hazardRating.equals("\"Moderate\"")) {
            return R.drawable.yellow;
        } else {
            return R.drawable.red;
        }
    }
    @Override
    public String toString() {

            return numCritical + ", " +
                    numNonCritical + ", " +
                    this.dateFormatter() + ", " +
                    inspectionType + ", " +
                    hazardRating;

    }

    public String getViolation(int position) {
        if(violations.length == 0){
            return "";
        } else {
            return this.violations[position];
        }
    }

    public String getShortViolation(int position) {
        if(violations.length == 0){
            return "";
        }

        String[] shortViolations = new String[violations.length];
        for (int i = 0; i < violations.length; i++) {
            if(violations[i].length()>10) {
                if(violations[i].length()<40)
                    shortViolations[i] = violations[i];
                else
                    shortViolations[i] = violations[i].substring(0, 40) + "...";
            }
            else {
                shortViolations[i] = violations[i];
            }
        }
        return shortViolations[position];
    }
}
