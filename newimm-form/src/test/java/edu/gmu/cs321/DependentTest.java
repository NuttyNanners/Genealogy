package edu.gmu.cs321;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

class DependentTest {
    private int ID, guardianID;
    private String name, nationality;
    private LocalDate DOB;
    private Dependent validDependent;

    @BeforeEach
    void setUp(){
        ID = 2;
        name = "Dependent Jones";
        DOB = LocalDate.of(2014, 5, 1);
        nationality = "United Kingdom";
        guardianID = 12;
        validDependent = new Dependent(ID, name, DOB, nationality, guardianID);
    }
    
    /** Dependent object creation tests */

    @Test
    void testCreateValidDependent() {
        assertNotNull(validDependent);
        assertEquals(ID, validDependent.getId());
        assertEquals(name, validDependent.getName());
        assertEquals(DOB, validDependent.getDOB());
        assertEquals(nationality, validDependent.getNationality());
        assertEquals(guardianID, validDependent.getGuardianID()
        );
    }

    @Test
    void testCreateInvalidDependent() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Dependent(-1, null, null, "", -12);
        });
    }

    /** Setter Testers */

    @Test
    void testUpdateValidGuardianID() {
        validDependent.setGuardianID(14);
        assertEquals(14, validDependent.getGuardianID()
        );
    }

    @Test
    void testUpdateInvalidGuardianID() {
        assertThrows(IllegalArgumentException.class, () -> {
            validDependent.setGuardianID(-12);
        });
    }

    @Test
    void testUpdateValidNationality(){
        validDependent.setNationality("Canada");
        assertEquals("Canada", validDependent.getNationality());
    }

    @Test
    void testUpdateInvalidNationality() {
        assertThrows(IllegalArgumentException.class, () -> {
            validDependent.setNationality(null);
        });
    }

    @Test
    void testUpdateValidDOB() {
        validDependent.setDOB(LocalDate.of(2012, 12, 25));
        assertEquals(LocalDate.of(2012, 12, 25), validDependent.getDOB());
    }

    @Test
    void testUpdateValidName() {
        validDependent.setName("Mike Jones");
        assertEquals("Mike Jones", validDependent.getName());
    }

    @Test
    void testUpdateInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> {
            validDependent.setName(null);
        });
    }

    @Test
    void testUpdateValidID() {
        validDependent.setID(4);
        assertEquals(4, validDependent.getID());
    }

    @Test
    void testUpdateInvalidID() {
        assertThrows(IllegalArgumentException.class, () -> {
            validDependent.setID(-8);
        });
    }

    /**Getter Testers */

    @Test
    void testGetNationality() {
        assertEquals(nationality, validDependent.getNationality());
    }

    @Test
    void testGetID() {
        assertEquals(ID, validDependent.getID());
    }

    @Test
    void testGetDOB() {
        assertEquals(DOB, validDependent.getDOB());
    }

    @Test
    void testgetGuardianID()
     {
        assertEquals(guardianID, validDependent.getGuardianID());
    }

    @Test
    void testGetName() {
        assertEquals(name, validDependent.getName());
    }
}