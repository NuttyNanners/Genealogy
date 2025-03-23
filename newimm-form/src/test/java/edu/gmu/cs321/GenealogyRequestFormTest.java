package edu.gmu.cs321;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GenealogyRequestFormTest {

	@Test
	public void testCreateValidForm() {
    	GenealogyRequestForm form = new GenealogyRequestForm(
        	"Geddy Lee", "110 Green St", "citizenship.pdf",
        	"Tom Sawyer", "222 Blue Dr", "2000-01-15", "Canada",
        	"relationship.pdf", "deathRecord.pdf"
    	);
    	assertNotNull(form);
    	assertEquals("Geddy Lee", form.getRequesterName());
    	assertEquals("Tom Sawyer", form.getDeceasedName());
	}

	@Test
	public void testCreateInvalidForm_MissingName() {
    	Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        	new GenealogyRequestForm(
            	"", "123 One St", "citizenship.pdf",
            	"Jane Doe", "454 Two St", "1993-02-04",”Guatemala",
            	"relationship.pdf", "death_record.pdf"
        	);
    	});
    	assertEquals("Requester name is required", exception.getMessage());
	}

	@Test
	public void testCreateInvalidForm_MissingProofOfCitizenship() {
    	Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        	new GenealogyRequestForm(
            	"John Doe", "123 Main St", null,
            	"Jane Joe", "777 Other St", "2000-01-15", "Canada",
            	"relationship.pdf", "death_record.pdf"
        	);
    	});
    	assertEquals("Proof of citizenship is required", exception.getMessage());
	}

	@Test
	public void testGetFormById() {
    	GenealogyRequestForm form = new GenealogyRequestForm(
        	"Alicia Keys", "789 Up St", "citizenship.pdf",
        	"Brass Keys", "321 Down St", "1995-06-22", "UK",
        	"relationship.pdf", "death_record.pdf"
    	);
    	String formId = form.getFormId();
    	assertEquals(form, GenealogyRequestForm.getFormById(formId));
	}

  @Test
	public void testUpdateRequesterAddress() {
    	GenealogyRequestForm form = new GenealogyRequestForm(
        	"Homer Johnson", "111 Maple Ave", "citizenship.pdf",
        	"Lisa Johnson", "222 Birch Rd", "1980-12-10", "France",
        	"relationship.pdf", "death_record.pdf"
    	);
    	form.updateRequesterAddress("999 Cedar Ln");
    	assertEquals("999 Cedar Ln", form.getRequesterAddress());
	}
}

