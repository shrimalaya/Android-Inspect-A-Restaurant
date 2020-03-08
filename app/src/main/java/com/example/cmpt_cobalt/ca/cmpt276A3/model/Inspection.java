package com.example.cmpt_cobalt.ca.cmpt276A3.model;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Inspection {
    private String trackingNumber;
    private String inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private String violations;

    public Inspection(String trackingNumber, String inspectionDate, String inspectionType, int numCritical, int numNonCritical, String hazardRating, String violations) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violations = violations;
    }

    //https://www.baeldung.com/java-date-difference
    public String dateFormatter() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        String[] indexToMonth = new DateFormatSymbols().getMonths();

        String rawInspectionDate = this.getInspectionDate();

        Date inspectionDate = sdf.parse(rawInspectionDate);
        Date currentDate = new Date();

        long diffInMS = Math.abs(currentDate.getTime() - inspectionDate.getTime());
        long diffInDay = TimeUnit.DAYS.convert(diffInMS, TimeUnit.MILLISECONDS);

        //https://stackoverflow.com/questions/36370895/getyear-getmonth-getday-are-deprecated-in-calendar-what-to-use-then
        //Need calendar because Java is daf.
        Calendar inspectionCalendar = Calendar.getInstance();
        inspectionCalendar.setTime(inspectionDate);

        if (diffInDay <= 1){
            return diffInDay + "Day";
        }
        else if (diffInDay <= 30){
            return diffInDay + " Days";
        }
        else if (diffInDay <= 365){
            return indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                    + " " + inspectionCalendar.get(Calendar.DAY_OF_MONTH);
        }
        else {
            return indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                    + " " + inspectionCalendar.get(Calendar.YEAR);
        }
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

    public String getViolations() {
        return this.violations;
    }

    public void setViolations(String violations) {
        this.violations = violations;
    }
}
