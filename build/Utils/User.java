package Utils;

import java.util.ArrayList;

public class User {
    public static int EVENT_ARRIVAL = 1;
    public static int EVENT_DEPARTURE = 2;
    public static int ROOM_GALLERY = -1;

    public static int USER_EMPLOYEE = 1;
    public static int USER_GUEST = 2;


    private String userName;
    private int userType;

    public User(String userName, int userType) {
        this.userName = userName;
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }


    public static boolean checkUserPermissionEnterRoom(InputArgument userLastLog) {
        if (userLastLog == null) return false;
        boolean userPermissions = (userLastLog.getEventTypeInformation() == EVENT_ARRIVAL && userLastLog.getRoomUniqueId() == ROOM_GALLERY) ||(userLastLog.getEventTypeInformation() == EVENT_DEPARTURE && userLastLog.getRoomUniqueId() >= 0);
        return userPermissions;
    }

    public static boolean checkUserPermissionLeaveRoom(InputArgument userLastLog, int roomId) {
        if (userLastLog == null) return false;
        boolean userPermissions = userLastLog.getEventTypeInformation() == EVENT_ARRIVAL && userLastLog.getRoomUniqueId() == roomId;
        return userPermissions;
    }


    public static boolean checkUserPermissionEnterGallery(InputArgument userLastLog) {
        if (userLastLog == null) return true;
        boolean userPermissions = userLastLog.getEventTypeInformation() == 2 && userLastLog.getRoomUniqueId() == -1;
        return userPermissions;
    }

    public static boolean checkUserPermissionLeaveGallery(InputArgument userLastLog) {
        if (userLastLog == null) return false;
        boolean userPermissions = (userLastLog.getEventTypeInformation() == 1 && userLastLog.getRoomUniqueId() == -1) || (userLastLog.getEventTypeInformation() == 2 && userLastLog.getRoomUniqueId() >= 0);
        return userPermissions;
    }

    public static boolean checkUserInRoom(InputArgument userLastLog) {
        boolean checkUserInside = (userLastLog.getEventTypeInformation() == EVENT_DEPARTURE && userLastLog.getRoomUniqueId() >= 0) || (userLastLog.getEventTypeInformation() == EVENT_ARRIVAL && userLastLog.getRoomUniqueId() == ROOM_GALLERY);
        return checkUserInside;
    }

    public static ArrayList<Integer> getRoomsInformation(ArrayList<InputArgument> fileData, User user) {
        ArrayList<Integer> roomIds = new ArrayList<>();
        for (InputArgument currentFileData : fileData) {
            if (currentFileData.getEventTypeInformation() == EVENT_ARRIVAL
                    && currentFileData.getUserInformation().getUserName().equals(user.getUserName())
                    && currentFileData.getUserInformation().getUserType() == user.getUserType()
                    && currentFileData.getRoomUniqueId() >= 0) {
                roomIds.add(currentFileData.getRoomUniqueId());
            }
        }
        return roomIds;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final User other = (User) obj;
        if (obj == this) {
            return true;
        }

        return other.userName.equals(this.userName) && other.userType == this.userType;
    }
}
