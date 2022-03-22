package com.parkit.parkingsystem;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {

    InputReaderUtil inputReaderUtil;
    private InputStream testIn;

    @Test
    public void readSelectionFromScanIsOK () {
        //GIVEN
        String input = "1";
        InputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        inputReaderUtil = new InputReaderUtil();
        int lineSelected = inputReaderUtil.readSelection();
        assertEquals(1, lineSelected);
    }

    @Test
    public void readSelectionFromScanIsKOWhenWrongSelection () {
        //GIVEN
        String input = "A";
        InputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        inputReaderUtil = new InputReaderUtil();
        int lineSelected = inputReaderUtil.readSelection();
        assertEquals(-1, lineSelected);
    }

        @Test
        public void readVehicleRegistrationNumberFromScanIsOK () throws Exception {
            //GIVEN
            String input = "ABCDEF";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            inputReaderUtil = new InputReaderUtil();
            String lineSelected = inputReaderUtil.readVehicleRegistrationNumber();
            assertEquals(input, lineSelected);
        }

        @Test
        public void readVehicleRegistrationNumberFromScanIsKOWhen () throws Exception {
            //GIVEN
            String input = "     ";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            inputReaderUtil = new InputReaderUtil();
            String lineSelected = inputReaderUtil.readVehicleRegistrationNumber();
            assertEquals(-1, lineSelected);
         }

}
