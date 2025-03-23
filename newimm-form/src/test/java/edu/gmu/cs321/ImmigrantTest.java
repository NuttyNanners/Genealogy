package edu.gmu.cs321;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ImmigrantTest {
    private int passportNumber, visaStatus, ID;
    private List<Integer> depIDs;
    private String name, nationality;
    private LocalDate DOB;
    private Immigrant validImmigrant;

    @BeforeEach
    void setUp(){
        ID = 1;
        name = "John Doe";
        DOB = LocalDate.of(1992,3,24);
        nationality = "Mexico";
        passportNumber = 1234567890;
        visa = 1;
        depIDs = new ArrayList<>(Arrays.asList(12,13,14));
        validImmigrant = new Immigrant(ID, name, DOB, nationality, passportNumber, visaStatus, depIDs);
    }

    /**Tester for construction of immigrants */

    @Test
    void testCreateValidImmigrant(){
        assertNotNull(validImmigrant);
        assertEquals(ID, validImmigrant.getID());
        assertEquals(name, validImmigrant.getName());
        assertEquals(DOB, validImmigrant.getDOB());
        assertEquals(nationality, validImmigrant.getNationality());
        assertEquals(passportNumber, validImmigrant.getPassportNumber());
        assertEquals(visaStatus, validImmigrant.getVisaStatus());
        assertEquals(depIDs, validImmigrant.getDependentIDs());
    }

    @Test
    void testCreateInvalidImmigrant(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Immigrant(-1, null,null, "", -2, -3, null);
        });
    }

    /**Getter Testers */

    @Test
    void testGetDOB(){
        assertEquals(DOB,validImmigrant.getDOB());
    }

    @Test
    void testGetName(){
        assertEquals(name,validImmigrant.getName());
    }

    @Test
    void testGetID(){
        assertEquals(ID,validImmigrant.getID());
    }

    @Test
    void testGetNationality(){
        assertEquals(nationality,validImmigrant.getNationality());
    }

    @Test
    void testGetVisaStatus(){
        assertEquals(visaStatus,validImmigrant.getVisaStatus());
    }

    @Test
    void testGetPassportNumber(){
        assertEquals(passportNumber,validImmigrant.getPassportNumber());
    }

    @Test
    void testgetDependentIDs(){
        assertEquals(depIDs,validImmigrant.getDependentIDs());
    }

    /**Setter Testers */

    @Test
    void testSetValidPassportNumber(){
        validImmigrant.setPassportNumber(1987654321);
        assertEquals(1987654321, validImmigrant.getPassportNumber());
    }

    @Test
    void testSetInvalidPassportNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            validImmigrant.setPassportNumber(-12345);
        });
    }

    @Test
    void testSetValidVisaStatus(){
        validImmigrant.setVisaStatus(2);
        assertEquals(2, validImmigrant.getPassportNumber());
    }

    @Test
    void testSetInvalidVisaStatus(){
        assertThrows(IllegalArgumentException.class, () -> {
            validImmigrant.setVisaStatus(-1);
        });
    }

    @Test
    void testUpdateValidDependentIDs(){
        List<Integer> newDependentIds = new ArrayList<>(Arrays.asList(201, 202));
        validImmigrant.setDependentIDs(newDependentIds);
        assertEquals(newDependentIds, validImmigrant.getDependentIDs());
    }

    @Test
    void testUpdateInvalidDependentIDs() {
        assertThrows(IllegalArgumentException.class, () -> {
            validImmigrant.setDependentIDs(null);
        });
    }

    @Test
    void testAddValidDependent() {
        int newDependentId = 19;
        validImmigrant.addDependentID(newDependentId);
        assertTrue(validImmigrant.getDependentIDs().contains(newDependentId));
    }

    @Test
    void testAddInvalidDependent() {
        assertThrows(IllegalArgumentException.class, () -> {
            validImmigrant.addDependentID(-5);
        });
    }

    
}