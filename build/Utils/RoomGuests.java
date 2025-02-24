package Utils;
import java.util.ArrayList;

public class RoomGuests {
    private int roomUniqueId;
    private ArrayList<User> usersList;

    public RoomGuests(int roomUniqueId, ArrayList<User> usersList) {
        this.roomUniqueId = roomUniqueId;
        this.usersList = usersList;
    }

    public int getRoomUniqueId() {
        return roomUniqueId;
    }

    public void setRoomUniqueId(int roomUniqueId) {
        this.roomUniqueId = roomUniqueId;
    }

    public ArrayList<User> getUserslist() {
        return usersList;
    }

    public void setUserslist(ArrayList<User> usersList) {
        this.usersList = usersList;
    }
}
