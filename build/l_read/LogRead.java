package l_read;

import Utils.TaskFileReader;
import Utils.GetCommandLineArguments;
import Utils.InputArgument;
import Utils.RoomGuests;
import Utils.UserRoomInformation;
import Utils.User;
import  java.util.Comparator;
import  java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;


public class LogRead {

    public static void main(String[] cmdArgument){

        String REGEX_SECRET = "[A-Za-z0-9]+";
        String finalOutput = "";

        String EncryptedFileName = cmdArgument[cmdArgument.length - 1];
        ArrayList<Integer> roomListGallery = new ArrayList<>();
        ArrayList<User> guestListGallery = new ArrayList<>();
        ArrayList<User> employeeListGallery = new ArrayList<>();


        GetCommandLineArguments secretToken = TaskFileReader.getArgument("-K", cmdArgument);
        GetCommandLineArguments employeeName = TaskFileReader.getArgument("-E", cmdArgument);
        GetCommandLineArguments logStates = TaskFileReader.getArgument("-S", cmdArgument);
        GetCommandLineArguments guestName = TaskFileReader.getArgument("-G", cmdArgument);
        GetCommandLineArguments roomLists = TaskFileReader.getArgument("-R", cmdArgument);

        try {

            ArrayList<InputArgument> fileData = TaskFileReader.readFile(EncryptedFileName);

            if (!secretToken.getidentifierValue().matches(REGEX_SECRET)) {
                System.out.println("error");
                System.exit(255);
            }

            if (!secretToken.identifierVrified()) {
                System.out.println("error");
                System.exit(255);
            }
            if (roomLists.identifierVrified() && (!employeeName.identifierVrified() && !guestName.identifierVrified())) {
                System.out.println("error");
                System.exit(255);
            }

            if (!logStates.identifierVrified() && !roomLists.identifierVrified()) {
                System.out.println("invalid");
                System.exit(255);
            }


            if (!InputArgument.checkTokenCorrectness(fileData.get(0).getTokenInformation(), secretToken.getidentifierValue())) {
                System.out.println("error");
                System.exit(255);
            }

            if (logStates.identifierVrified()) {
                int i = 0;
                while(i < fileData.size()){
                    User user = fileData.get(i).getUserInformation();
                    InputArgument userLogsStore = TaskFileReader.getLastLogInformation(fileData, fileData.size() - 1, user);
                    if (!(userLogsStore.getEventTypeInformation() == User.EVENT_DEPARTURE
                            && userLogsStore.getRoomUniqueId() == User.ROOM_GALLERY)) {

                        if (user.getUserType() == User.USER_EMPLOYEE && !employeeListGallery.contains(user))
                            employeeListGallery.add(user);
                        if (user.getUserType() == User.USER_GUEST && !guestListGallery.contains(user))
                            guestListGallery.add(user);
                    }
                    int roomId = fileData.get(i).getRoomUniqueId();
                    if (roomId != User.ROOM_GALLERY
                            && !roomListGallery.contains(roomId)) {
                        roomListGallery.add(roomId);
                    }
                    i++;
                }

                ArrayList<UserRoomInformation> userArr = new ArrayList<>();

                i = 0;
                while(i < employeeListGallery.size()) {
                    User user = employeeListGallery.get(i);
                    InputArgument userLogsStore = TaskFileReader.getLastLogInformation(fileData, fileData.size() - 1, user);
                    i++;
                    if (User.checkUserInRoom(userLogsStore)) continue;
                    ArrayList<Integer> roomIds = User.getRoomsInformation(fileData, user);
                    UserRoomInformation TOTALROOM = new UserRoomInformation(user, roomIds);
                    userArr.add(TOTALROOM);
                }

                i=0;
                while(i < guestListGallery.size()) {
                    User user = guestListGallery.get(i);
                    InputArgument userLogsStore = TaskFileReader.getLastLogInformation(fileData, fileData.size() - 1, user);
                    i++;
                    if (User.checkUserInRoom(userLogsStore)) continue;
                    ArrayList<Integer> roomIds = User.getRoomsInformation(fileData, user);
                    UserRoomInformation TOTALROOM = new UserRoomInformation(user, roomIds);
                    userArr.add(TOTALROOM);
                }

                ArrayList<RoomGuests> Rooms = new ArrayList<>();

                int galleryRoomsIndex = 0;
                while (galleryRoomsIndex < roomListGallery.size()) {
                    int room = roomListGallery.get(galleryRoomsIndex);
                    ArrayList<User> usersInRoom = new ArrayList<>();
                    for (int uservisited = 0; uservisited < userArr.size(); uservisited++) {
                        ArrayList<Integer> RoomsVisitedByUser = userArr.get(uservisited).getUniqueRoomIds();

                        if (room == RoomsVisitedByUser.get(RoomsVisitedByUser.size() - 1)) {
                            usersInRoom.add(userArr.get(uservisited).getParticularUser());
                        }
                    }
                    RoomGuests roomGuests = new RoomGuests(room, usersInRoom);
                    Rooms.add(roomGuests);
                    galleryRoomsIndex++;
                }

                i = 0;
                String tempString = "";
                while (i < employeeListGallery.size()) {
                    boolean isLastIndex = i == (employeeListGallery.size() - 1);
                    tempString += employeeListGallery.get(i).getUserName();
                    if (isLastIndex){
                        String[] splitArray = tempString.split(",");
                        Arrays.sort(splitArray,String.CASE_INSENSITIVE_ORDER);
                        finalOutput = finalOutput + String.join(",", splitArray)+ "\n";
                    }
                    else tempString += ",";
                    i++;
                }

                i = 0;
                tempString = "";
                while( i < guestListGallery.size()) {
                    boolean isLastIndex = i == (guestListGallery.size() - 1);
                    tempString += guestListGallery.get(i).getUserName();
                    if (isLastIndex){
                        String[] splitArray = tempString.split(",");
                        Arrays.sort(splitArray,String.CASE_INSENSITIVE_ORDER);
                        finalOutput = finalOutput + String.join(",", splitArray)+ "\n";
                    }
                    else tempString += ",";
                    i++;
                }

                i = 0;
                Collections.sort (Rooms ,  new SortById ());
                while ( i < (Rooms.size())) {
                    RoomGuests room = Rooms.get(i);
                    i++;
                    if (room.getUserslist().size() == 0) continue;
                    finalOutput += room.getRoomUniqueId() + ": ";
                    tempString = "";
                    for (int j = 0; j < room.getUserslist().size(); j++) {
                        boolean isLastUser = j == (room.getUserslist().size() - 1);
                        User user = room.getUserslist().get(j);
                        tempString += user.getUserName();
                        if (!isLastUser) tempString += ",";
                    }
                    String[] splitArray = tempString.split(",");
                    Arrays.sort(splitArray,String.CASE_INSENSITIVE_ORDER);
                    finalOutput = finalOutput + String.join(",", splitArray)+ "\n";
                }
            } else {
                String userName = employeeName.identifierVrified() ? employeeName.getidentifierValue() : guestName.getidentifierValue();
                int userType = employeeName.identifierVrified() ? User.USER_EMPLOYEE : User.USER_GUEST;
                User user = new User(userName, userType);
                InputArgument userLogsStore = TaskFileReader.getLastLogInformation(fileData, fileData.size() - 1, user);
                if (userLogsStore == null) System.exit(0);

                ArrayList<Integer> roomvisitedbyUser = User.getRoomsInformation(fileData, user);
                for (int i = 0; i < roomvisitedbyUser.size(); i++) {
                    boolean isLastIndex = i == (roomvisitedbyUser.size() - 1);
                    finalOutput += roomvisitedbyUser.get(i);
                    if (!isLastIndex) finalOutput += ",";
                }
            }

            System.out.println(finalOutput);
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}

class  SortById implements Comparator < RoomGuests > {
    @Override
    public int compare(RoomGuests o1, RoomGuests o2) {
        int roomId_1 = o1.getRoomUniqueId();
        int roomId_2 = o2.getRoomUniqueId();
        if (roomId_1 > roomId_2) {
            return 1;
        } else if (roomId_1 < roomId_2) {
            return -1;
        } else return 0;
    }
}
