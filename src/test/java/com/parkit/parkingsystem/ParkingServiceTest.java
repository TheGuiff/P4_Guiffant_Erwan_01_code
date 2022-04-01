package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;
    private Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    void setUpPerTest() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setAlreadyCame(true);
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @Test
    public void processExitingVehicleTest() {
        //GIVEN
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
        //WHEN
        parkingService.processExitingVehicle();
        //THEN
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

    }

    @Test
    public void processIncomingVehicleTest() {
        //GIVEN
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        //WHEN
        parkingService.processIncomingVehicle();
        //THEN
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
    }

    @Test
    public void processExitingVehicle_WhenBadVehicleRegNumber_ShouldNotCreateTicket () {
        //GIVEN
        //Un utilisateur va taper une entrée incorrecte sur la plaque d'immatriculation
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("     ");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        //WHEN
        //Sortie voiture
        parkingService.processExitingVehicle();
        //THEN
        //Le ticket n'a pas été créé et le parking non mis à jour
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
    }

    @Test
    public void getNextParkingNumberIfAvailableTest () {
        //GIVEN
        //Quand on cherche une place de voiture et que la place 1 est disponible
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        //WHEN
        //j'appelle le next parking spot
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        //THEN
        //Le parking slot renvoyé est bien le 1
        assertEquals(1,parkingSpot.getId());
        assertEquals(ParkingType.CAR,parkingSpot.getParkingType());
        assertTrue(parkingSpot.isAvailable());
    }

    @Test
    public void getNextParkingNumberIfAvailableBikeTest () {
        //GIVEN
        try {
            when(inputReaderUtil.readSelection()).thenReturn(2);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        //WHEN
        //j'appelle le next parking spot
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        //THEN
        //Le parking slot renvoyé est bien le 1
        assertEquals(1,parkingSpot.getId());
        assertEquals(ParkingType.BIKE,parkingSpot.getParkingType());
        assertTrue(parkingSpot.isAvailable());
    }

    @Test
    public void getNextParkingNumberIfAvailableUnknownTest () {
        //GIVEN
        // Simulation du choix "3" en type de véhicule (choix qui n'existe pas)
        try {
            when(inputReaderUtil.readSelection()).thenReturn(3);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        //WHEN
        //j'appelle le next parking spot
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        //THEN
        //Le parking slot renvoyé est null
        assertNull(parkingSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailableNullWhenSlotZero () {
        //GIVEN
        //On mocke parkingSpotDAO pour renvoyer 0 comme prochain slot dispo
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        //WHEN
        //j'appelle le next parking spot
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        //THEN
        //Le parking slot est null (la place 0 n'existe pas)
        assertNull(parkingSpot);
    }

}
