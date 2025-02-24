package Utils;

public class InputArgument {
    private int eventTypeInformation;
    private int roomUniqueId;
    private int timestampInformation;
    private String tokenInformation;
    private User userObject;
    public static int EVENT_TYPE_ARRIVAL = 1;
    public static int EVENT_TYPE_DEPARTURE = 2;
    public static int ROOM_TYPE_GALLERY = -1;

    public InputArgument(int timestampInformation, String tokenInformation, User userObject, int eventTypeInformation, int roomUniqueId) {
        this.timestampInformation = timestampInformation;
        this.tokenInformation = tokenInformation;
        this.userObject = userObject;
        this.eventTypeInformation = eventTypeInformation;
        this.roomUniqueId = roomUniqueId;
    }

    public int getTimestampInformation() {
        return timestampInformation;
    }

    public String getTokenInformation() {
        return tokenInformation;
    }

    public User getUserInformation() {
        return this.userObject;
    }

    public void setUserInformation(User userObject) {
        this.userObject = userObject;
    }

    public int getEventTypeInformation() {
        return eventTypeInformation;
    }

    public void setEventTypeInformation(int eventTypeInformation) {
        this.eventTypeInformation = eventTypeInformation;
    }

    public int getRoomUniqueId() {
        return roomUniqueId;
    }

    public static boolean logRoomEntryInformation(int eventTypeInformation, int roomUniqueId) {
        boolean identifier =  eventTypeInformation == EVENT_TYPE_ARRIVAL && roomUniqueId >= 0;
        return identifier;
    }

    public static boolean logRoomExitInformation(int eventTypeInformation, int roomUniqueId) {
        boolean identifier = eventTypeInformation == EVENT_TYPE_DEPARTURE && roomUniqueId >= 0;
        return identifier;
    }

    public static boolean logGalleryEntryInformation(int eventTypeInformation, int roomUniqueId) {
        boolean identifier = eventTypeInformation == EVENT_TYPE_ARRIVAL && roomUniqueId == -1;
        return identifier;
    }

    public static boolean logGalleryExitInformation(int eventTypeInformation, int roomUniqueId) {
        boolean identifier = eventTypeInformation == EVENT_TYPE_DEPARTURE && roomUniqueId == -1;
        return identifier;
    }

    public static boolean checkTokenCorrectness(String fileToken, String passedToken) {
        return fileToken.equals(passedToken);
    }
    public static boolean timeStampCorrectness(int fileTimestamp, int enteredTimestamp) {
        return enteredTimestamp > fileTimestamp;
    }
    
}
