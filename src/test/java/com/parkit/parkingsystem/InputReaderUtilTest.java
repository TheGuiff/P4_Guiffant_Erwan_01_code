package com.parkit.parkingsystem;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {

    InputReaderUtil inputReaderUtil;

    @Test
    public void readSelectionFromScanIsOK () {
        //GIVEN
        String input = "1";
        InputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        inputReaderUtil = new InputReaderUtil();
        //WHEN
        int lineSelected = inputReaderUtil.readSelection();
        //THEN
        assertEquals(1, lineSelected);
    }

    @Test
    public void readSelectionFromScanIsKOWhenWrongSelection () {
        //GIVEN
        String input = "A";
        InputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        inputReaderUtil = new InputReaderUtil();
        //WHEN
        int lineSelected = inputReaderUtil.readSelection();
        //THEN
        assertEquals(-1, lineSelected);
    }

        @Test
        public void readVehicleRegistrationNumberFromScanIsOK () {
            //GIVEN
            String input = "ABCDEF";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            inputReaderUtil = new InputReaderUtil();
            //WHEN
            String lineSelected = inputReaderUtil.readVehicleRegistrationNumber();
            //THEN
            assertEquals(input, lineSelected);
        }

        @Test
        public void readVehicleRegistrationNumberFromScanIsKOWhenNoInput () {
            //GIVEN
            String input = "     ";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            inputReaderUtil = new InputReaderUtil();
            //WHEN THEN
            assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
         }

}
