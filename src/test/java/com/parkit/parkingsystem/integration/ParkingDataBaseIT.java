package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){

        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability

        when(inputReaderUtil.readSelection()).thenReturn(1);

        //GIVEN
        // Une voiture, plaque ABCDEF
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        //La voiture se gare effectivement dans le parking en place 1
        parkingService.processIncomingVehicle();

        //THEN
        // Il y a bien un ticket dans la base de donnée pour la voiture ABCDEF.
        // La place 1 n'est plus disponible (on teste alors que la prochaine place disponible est la place 2)
        Ticket ticketCreated = ticketDAO.getTicket("ABCDEF");
        int nextAvailablePlace = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotNull(ticketCreated);
        assertEquals(2, nextAvailablePlace);

    }

    @Test
    public void testParkingLotExit(){

        //TODO: check that the fare generated and out time are populated correctly in the database

        //GIVEN
        // Remplacer le test par des données à la main pour contrôle dessus
        int nextAvailablePlace = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingSpot parkingSpot = new ParkingSpot(nextAvailablePlace, ParkingType.CAR, false);
        parkingSpotDAO.updateParking(parkingSpot);
        Ticket ticketIn = new Ticket();
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticketIn.setInTime(inTime);
        ticketIn.setParkingSpot(parkingSpot);
        ticketIn.setVehicleRegNumber("ABCDEF");
        ticketIn.setPrice(0);
        ticketIn.setOutTime(null);
        ticketDAO.saveTicket(ticketIn);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        //On sort la voiture
        parkingService.processExitingVehicle();

        //THEN
        // Le prix et la date/heure de sortie sont bien dans la base
        Ticket ticketCreated = ticketDAO.getTicket("ABCDEF");
        Date dateOut = ticketCreated.getOutTime();
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticketCreated.getPrice());
        assertNotNull(dateOut);

    }

}
