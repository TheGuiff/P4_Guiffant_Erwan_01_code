package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class FareCalculatorService {

    public static final double HOUR_IN_MILLISEC = 3600000.0;

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();

        double duration = (outHour.getTime() - inHour.getTime()) / HOUR_IN_MILLISEC;

        boolean alreadyCame = ticket.getAlreadyCame();
        double multi = alreadyCame? 0.95 : 1;

        if (duration < 0.5) {
            ticket.setPrice(0.0);
        }
        else {
            BigDecimal fare;
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    fare = BigDecimal.valueOf(Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    fare = BigDecimal.valueOf(Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
            BigDecimal price = fare
                    .multiply(BigDecimal.valueOf(multi))
                    .multiply(BigDecimal.valueOf(duration)).setScale(2, RoundingMode.HALF_UP);
            ticket.setPrice(price.doubleValue());
        }
    }
}