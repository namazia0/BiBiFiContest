package Utils;
import java.util.ArrayList;

public class UserRoomInformation {
    private User user;
    private ArrayList<Integer> roomUniqueIds;

    public UserRoomInformation(User user, ArrayList<Integer> roomUniqueIds) {
        this.user = user;
        this.roomUniqueIds = roomUniqueIds;
    }

    public User getParticularUser() {
        return user;
    }

    public void setParticularUser(User user) {
        this.user = user;
    }

    public ArrayList<Integer> getUniqueRoomIds() {
        return roomUniqueIds;
    }

    public void setUniqueRoomIds(ArrayList<Integer> roomUniqueIds) {
        this.roomUniqueIds = roomUniqueIds;
    }
}
