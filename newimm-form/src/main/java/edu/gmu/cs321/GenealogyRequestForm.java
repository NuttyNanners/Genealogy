package edu.gmu.cs321;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class GenealogyRequestForm {
    private String requesterName;
    private String requesterAddress;
    private String requesterSSN;
    private String deceasedName;
    private String deceasedAddress;
    private Date dateOfDeath;
    private String countryOfOrigin;
    private File proofOfRelationshipFile;
    private File deathRecordFile;
    private final String formID = UUID.randomUUID().toString();
    

    public GenealogyRequestForm(String requesterName, String requesterAddress, String requesterSSN,
                                String deceasedName, String deceasedAddress, Date dateOfDeath, 
                                String countryOfOrigin, File proofOfRelationshipFile, File deathRecordFile) {
        this.requesterName = requesterName;
        this.requesterAddress = requesterAddress;
        this.requesterSSN = requesterSSN;
        this.deceasedName = deceasedName;
        this.deceasedAddress = deceasedAddress;
        this.dateOfDeath = dateOfDeath;
        this.countryOfOrigin = countryOfOrigin;
        this.proofOfRelationshipFile = proofOfRelationshipFile;
        this.deathRecordFile = deathRecordFile;
    }

    @Override
    public String toString() {
        return "Form ID: " + formID + ", Request for: " + deceasedName + " by " + requesterName;
    }
}
