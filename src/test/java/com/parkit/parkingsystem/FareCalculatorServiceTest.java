package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);

        //WHEN
        ticket.setParkingSpot(parkingSpot);

        //THEN
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // WHEN THEN
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){

        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        BigDecimal fareExpected = BigDecimal.valueOf(Fare.BIKE_RATE_PER_HOUR).multiply(BigDecimal.valueOf(0.75));
        assertEquals(fareExpected.setScale(2, RoundingMode.HALF_UP).doubleValue(), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){

        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        BigDecimal fareExpected = BigDecimal.valueOf(Fare.CAR_RATE_PER_HOUR).multiply(BigDecimal.valueOf(0.75));
        assertEquals(fareExpected.setScale(2, RoundingMode.HALF_UP).doubleValue(), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){

        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        BigDecimal fareExpected = BigDecimal.valueOf(Fare.CAR_RATE_PER_HOUR).multiply(BigDecimal.valueOf(24));
        assertEquals(fareExpected.setScale(2, RoundingMode.HALF_UP).doubleValue(), ticket.getPrice() );
    }

    @Test
    public void calculateFareBikeWithLessThanHalfAnHourParkingTime(){

        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  15 * 60 * 1000) );//15 minutes parking time should give 0 parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        assertEquals(0, ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanHalfAnHourParkingTime(){

        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  15 * 60 * 1000) );//15 minutes parking time should give 0 parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        assertEquals( 0, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithHalfAnOneHourParkingTime(){

        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );//30 minutes parking time should give 1/2th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        BigDecimal fareExpected = BigDecimal.valueOf(Fare.BIKE_RATE_PER_HOUR).multiply(BigDecimal.valueOf(0.5));
        assertEquals(fareExpected.setScale(2, RoundingMode.HALF_UP).doubleValue(), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithHalfAnHourParkingTime(){

        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );//30 minutes parking time should give 1/2th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        BigDecimal fareExpected = BigDecimal.valueOf(Fare.CAR_RATE_PER_HOUR).multiply(BigDecimal.valueOf(0.5));
        assertEquals(fareExpected.setScale(2, RoundingMode.HALF_UP).doubleValue(), ticket.getPrice() );
    }

    @Test
    public void calculateFareWithCarAlreadyCame () {

        //GIVEN
        //Voiture déjà venue et garée sur le parking depuis une heure
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setAlreadyCame(true);

        //WHEN
        //Quand on calcule le prix
        fareCalculatorService.calculateFare(ticket);

        //THEN
        //Le prix demandé est de 95% de 1h = 1.425 - arrondi à 1.43
        BigDecimal fareExpected = BigDecimal.valueOf(Fare.CAR_RATE_PER_HOUR).multiply(BigDecimal.valueOf(0.95));
        assertEquals(fareExpected.setScale(2, RoundingMode.HALF_UP).doubleValue(), ticket.getPrice() );
    }

    @Test
    public void calculateFareWithBikeAlreadyCame () {

        //GIVEN
        //Moto déjà venue et garée sur le parking depuis une heure
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setAlreadyCame(true);

        //WHEN
        //Quand on calcule le prix
        fareCalculatorService.calculateFare(ticket);

        //THEN
        //Le prix demandé est de 95% de 1h = 0.95
        BigDecimal fareExpected = BigDecimal.valueOf(Fare.BIKE_RATE_PER_HOUR).multiply(BigDecimal.valueOf(0.95));
        assertEquals(fareExpected.setScale(2, RoundingMode.HALF_UP).doubleValue(), ticket.getPrice() );
    }

}
