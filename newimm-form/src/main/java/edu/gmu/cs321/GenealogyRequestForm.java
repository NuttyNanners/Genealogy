package org.openjfx;

import java.io.File;
import java.time.LocalDate;

public class GenealogyRequestForm {
    private String requesterName;
    private String requesterAddress;
    private String requesterSSN;
    private String deceasedName;
    private String deceasedAddress;
    private LocalDate dateOfDeath;
    private String countryOfOrigin;
    private File proofOfRelationshipFile;
    private File deathRecordFile;

    public GenealogyRequestForm(String requesterName, String requesterAddress, String requesterSSN,
                                String deceasedName, String deceasedAddress, LocalDate dateOfDeath, 
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
        return "Request for: " + deceasedName + " by " + requesterName;
    }
}
