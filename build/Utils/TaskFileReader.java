package Utils;

import java.io.*;
import java.util.ArrayList;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import java.util.Base64;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class TaskFileReader {
    public static BufferedReader bufferedReader = null;

    public static String lineString = "";
    public static ArrayList<InputArgument> readFile(String fileName) {
        ArrayList<InputArgument> fileContentsArr = new ArrayList<InputArgument>();
        try {
            String splitFileName = "";
            if (!fileName.contains("/")){
                splitFileName = fileName;
            }
            String[] arrayArgument = fileName.split("/");
            splitFileName = arrayArgument[arrayArgument.length - 1];
            File file = new File(splitFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            bufferedReader = new BufferedReader(new FileReader(splitFileName));

            while ((lineString = bufferedReader.readLine()) != null) {
                String[] parsedFileName = decryption(lineString).split(" ");
                String userToken;
                String timeStampInformation;
                String userName;
                String userTypeInformation;
                String roomIdInformation;
                String eventTypeInformation;

                if(parsedFileName[0].equals("-K")){
                    userToken = parsedFileName[1];
                    timeStampInformation = parsedFileName[3];
                    if(!parsedFileName[5].equals("-R")){
                        userName = parsedFileName[6];
                        userTypeInformation = parsedFileName[5];
                        if(parsedFileName[5].equals("-E")){
                            userTypeInformation = "1";
                        }else{
                            userTypeInformation = "2";
                        }
                    }
                    else{
                        userName = parsedFileName[8];
                        userTypeInformation = parsedFileName[7];
                        if(parsedFileName[7].equals("-E")){
                            userTypeInformation = "1";
                        }else{
                            userTypeInformation = "2";
                        }
                    }
                    if(parsedFileName[5].equals("-R"))
                        roomIdInformation = parsedFileName[6];
                    else
                        roomIdInformation = "-1";

                    eventTypeInformation = parsedFileName[4];
                    if(parsedFileName[4].equals("-A")){
                        eventTypeInformation = "1";
                    }else{
                        eventTypeInformation = "2";
                    }

                }else{
                    userToken = parsedFileName[1];
                    timeStampInformation = parsedFileName[0];
                    userName = parsedFileName[3];
                    userTypeInformation = parsedFileName[2];
                    roomIdInformation = parsedFileName[5];
                    eventTypeInformation = parsedFileName[4];
                }

                User user = new User(userName, Integer.parseInt(userTypeInformation));
                InputArgument inputArgument = new InputArgument(
                        Integer.parseInt(timeStampInformation),
                        userToken,
                        user,
                        Integer.parseInt(eventTypeInformation),
                        Integer.parseInt(roomIdInformation)
                );
                fileContentsArr.add(inputArgument);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return fileContentsArr;
    }

    public static String decryption(String encrypted) {
        String key = "aesEncryptionKey";
        String IV = "encryptionIntVec";

        try {
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getMimeDecoder().decode(encrypted.getBytes()));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static GetCommandLineArguments getArgument(String identifier, String[] args) {
        GetCommandLineArguments cmdArgument = new GetCommandLineArguments(identifier, "", false);
        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1) break;
            if (args[i].equals(identifier)) {
                cmdArgument.setidentifierVrified(true);
                if (!args[i + 1].startsWith("-")) {
                    cmdArgument.setidentifierValue(args[i + 1]);
                }
            }

        }
        return cmdArgument;
    }

    public static InputArgument getLastLogInformation(ArrayList<InputArgument> fileData, int fileLastIndex, User user) {
        InputArgument userLastLog = null;
        for (int i = fileLastIndex; i >= 0; i--) {
            if (fileData.get(i).getUserInformation().getUserName().equals(user.getUserName())
                    && fileData.get(i).getUserInformation().getUserType() == user.getUserType()) {
                userLastLog = fileData.get(i);
                break;
            }
        }
        return userLastLog;
    }
}
