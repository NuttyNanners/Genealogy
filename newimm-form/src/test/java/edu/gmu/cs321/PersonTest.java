package edu.gmu.cs321;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

class PersonTest {
    private int ID;
    private String name;
    private LocalDate DOB;
    private String nationality;

    @BeforeEach
    void setUp(){
        ID = 1;
        name = "John Smith";
        DOB = LocalDate.of(2000, 3, 27);
        nationality = "United Kingdom";
    }

    /**Person construction testers */

    @Test
    void testCreateValidPersion(){
        Person person = new Person(ID, name, DOB, nationality);
        assertNotNull(person);
        assertEquals(ID, person.getID());
        assertEquals(name, person.getName());
        assertEquals(DOB, person.getDOB());
        assertEquals(nationality, person.getNationality());
    }

    @Test
    void testCreateInvalidPerson(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Person(-2, null, null, "");
        });
    }

    /** Setter Testers */

    @Test
    void testUpdateValidName() {
        Person person = new Person(ID, name, DOB, nationality);
        String newName = "Zac Will";
        person.setName(newName);
        assertEquals(newName, person.getName());
    }

    @Test
    void testUpdateInvalidName(){
        Person person = new Person(ID, name, DOB, nationality);
        assertThrows(IllegalArgumentException.class, () -> {
            person.setName(null);
        });
    }

    @Test
    void testUpdateValidNationality() {
        Person person = new Person(ID, name, DOB, nationality);
        String newNationality = "Germany";
        person.setNationality(newNationality);
        assertEquals(newNationality, person.getNationality());
    }

    @Test
    void testUpdateInvalidNationality(){
        Person person = new Person(ID, name, DOB, nationality);
        assertThrows(IllegalArgumentException.class, () -> {
            person.setNationality(null);
        });
    }

    @Test
    void testUpdateValidID() {
        Person person = new Person(ID, name, DOB, nationality);
        int newID = 8;
        person.setID(newID);
        assertEquals(newID, person.getID());
    }

    @Test
    void testUpdateInvalidID(){
        Person person = new Person(ID, name, DOB, nationality);
        assertThrows(IllegalArgumentException.class, () -> {
            person.setID(-8);
        });
    }

    @Test
    void testUpdateValidDOB() {
        Person person = new Person(ID, name, DOB, nationality);
        LocalDate newDOB = LocalDate.of(1984, 1, 12);
        person.setDOB(newDOB);
        assertEquals(newDOB, person.getDOB());
    }

    /**Getter Testers */

    @Test
    void testGetDOB() {
        Person person = new Person(ID, name, DOB, nationality);
        assertEquals(DOB, person.getDOB());
    }

    @Test
    void testGetName(){
        Person person = new Person(ID, name, DOB, nationality);
        assertEquals(name, person.getName());
    }

    @Test
    void testgetID(){
        Person person = new Person(ID, name, DOB, nationality);
        assertEquals(ID, person.getID());
    }

    @Test
    void testGetNationality(){
        Person person = new Person(ID, name, DOB, nationality);
        assertEquals(nationality, person.getNationality());
    }
}