package Utils;

public class GetCommandLineArguments {
    private String identifier;
    private String identifierValue;
    private boolean identifierVrified;

    public GetCommandLineArguments(String identifier, String identifierValue, boolean identifierVrified) {
        this.identifier = identifier;
        this.identifierValue = identifierValue;
        this.identifierVrified = identifierVrified;
    }

    public String getidentifier() {
        return identifier;
    }

    public void setidentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getidentifierValue() {
        return identifierValue;
    }

    public void setidentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public boolean identifierVrified() {
        return identifierVrified;
    }

    public void setidentifierVrified(boolean passed) {
        identifierVrified = passed;
    }
}
