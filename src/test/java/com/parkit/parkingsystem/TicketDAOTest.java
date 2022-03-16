package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import static junit.framework.Assert.*;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

    private Ticket ticket;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static DataBaseTestConfig dataBaseTestConfig;
    private static DataBaseConfig dataBaseConfig;

    @BeforeAll
    private static void setUp () {
        ticketDAO = new TicketDAO();
        dataBasePrepareService = new DataBasePrepareService();
        dataBaseTestConfig = new DataBaseTestConfig();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBaseConfig = new DataBaseTestConfig();
    }

    @BeforeEach
    private void setUpPerTest (){
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
        //On essaie de sauvegarder le ticket mais c'est impossible
        assertFalse(ticketDAO.saveTicket(ticket));
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
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,"ABCDEF");
            ResultSet rs = ps.executeQuery();
            Ticket ticketOut = new Ticket();
            if(rs.next()){
                ParkingSpot parkingSpotOut = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticketOut.setParkingSpot(parkingSpotOut);
                ticketOut.setId(rs.getInt(2));
                ticketOut.setVehicleRegNumber("ABCDEF");
                ticketOut.setPrice(rs.getDouble(3));
                ticketOut.setInTime(rs.getTimestamp(4));
                ticketOut.setOutTime(rs.getTimestamp(5));
            }
            assertNotNull(ticketOut);
            assertEquals(ticket.getId(),ticketOut.getId());
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
             }catch (Exception ex){
                ex.printStackTrace();
            }finally {
            dataBaseConfig.closeConnection(con);
            }
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
        ticketDAO.saveTicket(ticket);
        Ticket ticketOut = ticketDAO.getTicket("ABCDEF");
        //THEN
        assertEquals(1, ticketOut.getId());
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
        boolean resultUpdate = ticketDAO.updateTicket(ticket);
        //THEN
        assertFalse(resultUpdate);
    }

    @Test
    public void processTicketDAOSaveTicketTestKOWhenTicketNull () {
        ticket = new Ticket();
        //WHEN
        boolean resultUpdate = ticketDAO.saveTicket(ticket);
        //THEN
        assertFalse(resultUpdate);
    }

    @Test
    public void processTicketDAOGetTicketTestKOWhenNoVehicleRegistrationNumber () {
        Ticket ticketOut = ticketDAO.getTicket("    ");
        //THEN
        assertNull(ticketOut);
    }

    @Test
    public void processTicketDAOGetTicketTestWhenAllreadyCame () {
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
        secondTicket.setParkingSpot(firstParkingSpot);
        secondTicket.setVehicleRegNumber("ABCDEF");
        secondTicket.setId(2);
        ticketDAO.saveTicket(secondTicket);
        //WHEN
        Ticket ticketOut = ticketDAO.getTicket("ABCDEF");
        //THEN
        assertTrue(ticketOut.getAlreadyCame());
    }

}
