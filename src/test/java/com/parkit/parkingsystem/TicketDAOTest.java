package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

    private Ticket ticket;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static DataBaseTestConfig dataBaseTestConfig;

    @BeforeAll
    static void setUp () {
        ticketDAO = new TicketDAO();
        dataBasePrepareService = new DataBasePrepareService();
        dataBaseTestConfig = new DataBaseTestConfig();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @BeforeEach
    void setUpPerTest (){
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void saveTicketFalseWhenSlotZero () {
        //GIVEN
        //Un ticket pour l'emplacement 0
        ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR,false);
        ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        //WHEN THEN
        assertFalse(ticketDAO.saveTicket(ticket));
    }

    @Test
    public void processTicketDAOGetTicketTest () {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setId(1);
        //WHEN
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataBaseTestConfig.getConnection();
            ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            ps.executeUpdate();
        }catch (SQLException | ClassNotFoundException | IOException ex){
            ex.printStackTrace();
        }finally {
            dataBaseTestConfig.closePreparedStatement(ps);
            dataBaseTestConfig.closeConnection(con);
        }
        //THEN
        Ticket ticketOut = ticketDAO.getTicket("ABCDEF");
        assertEquals(1, ticketOut.getId());
    }

    @Test
    public void processTicketDAOSaveTicketTest () {
        //GIVEN
        //Ticket pour l'emplacement 1
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setId(1);
        //WHEN THEN
        //La sauvegarde du ticket dans la base se passe bien
        assertTrue(ticketDAO.saveTicket(ticket));
        Ticket ticketOut = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticketOut);
        assertEquals(ticket.getId(),ticketOut.getId());
    }

    @Test
    public void processTicketDAOUpdateTicketTest () {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setId(1);
        //WHEN
        ticketDAO.saveTicket(ticket);
        Date outTime = new Date();
        ticket.setOutTime(outTime);
        ticketDAO.updateTicket(ticket);
        Ticket ticketOut = ticketDAO.getTicket("ABCDEF");
        Date dateOut = ticketOut.getOutTime();
        //THEN
        assertNotNull(dateOut);
    }

    @Test
    public void processTicketDAOUpdateTicketTestKOWhenTicketNull () {
        ticket = new Ticket();
        //WHEN
        assertThrows(NullPointerException.class, ()-> ticketDAO.updateTicket(ticket));
    }

    @Test
    public void processTicketDAOSaveTicketTestKOWhenTicketNull () {
        ticket = new Ticket();
        //WHEN
        assertThrows(NullPointerException.class, ()-> ticketDAO.saveTicket(ticket));
    }

    @Test
    public void processTicketDAOGetTicketTestKOWhenNoVehicleRegistrationNumber () {
        Ticket ticketOut = ticketDAO.getTicket("    ");
        //THEN
        assertNull(ticketOut);
    }

    @Test
    public void processTicketDAOGetTicketTestWhenAlreadyCame () {
        ParkingSpot firstParkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket firstTicket = new Ticket();
        firstTicket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        firstTicket.setParkingSpot(firstParkingSpot);
        firstTicket.setVehicleRegNumber("ABCDEF");
        firstTicket.setId(1);
        ticketDAO.saveTicket(firstTicket);
        ParkingSpot secondParkingSpot = new ParkingSpot(2, ParkingType.CAR,false);
        Ticket secondTicket = new Ticket();
        secondTicket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        secondTicket.setParkingSpot(secondParkingSpot);
        secondTicket.setVehicleRegNumber("ABCDEF");
        secondTicket.setId(2);
        ticketDAO.saveTicket(secondTicket);
        //WHEN
        Ticket ticketOut = ticketDAO.getTicket("ABCDEF");
        //THEN
        assertTrue(ticketOut.getAlreadyCame());
    }

}
