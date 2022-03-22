package com.parkit.parkingsystem.model;

import java.util.Calendar;
import java.util.Date;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;
    private boolean alreadyCame;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
        if (inTime == null) {
            return null;
        } else {
            Date result = (Date) inTime.clone();
            return result;
        }
    }

    public void setInTime(Date inTime) {
        if (inTime != null) {
            Date dateIn = (Date) inTime.clone();
            this.inTime = dateIn;
        }
    }

    public Date getOutTime() {
        if (outTime == null) {
            return null;
        } else {
            Date result = (Date) outTime.clone();
            return result;
        }
    }

    public void setOutTime(Date outTime) {
        if (outTime != null) {
            Date dateOut = (Date) outTime.clone();
            this.outTime = dateOut;
        }
    }

    public void setAlreadyCame(boolean b) {
        this.alreadyCame = b;
    }

    public boolean getAlreadyCame() {
        return alreadyCame;
    }
}
