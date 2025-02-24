package l_append;

import java.util.Base64;

import Utils.TaskFileReader;
import Utils.User;
import Utils.GetCommandLineArguments;
import Utils.InputArgument;

import java.util.ArrayList;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Arrays;
import java.util.Scanner;

class LogAppend {
	public static BufferedReader bufferedReader = null;
	public static String REGEX_INT = "[0-9]+";
	public static String REGEX_SECRET = "[A-Za-z0-9]+";
	public static String REGEX_NAME = "[A-Za-z]+";
	public static String REGEX_OMIT_ZEROS = "^0+(?!$)";

	public static int MAX_INT_VAL = 1073741823;
	public static int MIN_ROOM_VAL = 0;
	public static int MIN_INT_VAL = 1;

	public static String lineString = "";
	public static int EVENT_ARRIVAL = 1;
	public static int EVENT_DEPARTURE = 2;
	public static int ROOM_GALLERY = -1;

	public static void main(String[] args) {
		String logFileName = args[args.length - 1];
		String newFileName = "";
		if(args[0].equals("-B")){
			File file = null;
			file = new File(logFileName);
			if (!file.exists()) {
				System.out.println("invalid no file");
				System.exit(255);
			}
			try{
				Scanner scanData = new Scanner(file);
				FileWriter myWriter = null;
				String encryptedText = "";
				while (scanData.hasNextLine()) {
					lineString = scanData.nextLine();
					String[] readLine = lineString.split(" ");
					newFileName = readLine[readLine.length-1];
					file = new File(newFileName);
					if (!file.exists()) {
						file.createNewFile();
					}
					myWriter = new FileWriter(newFileName, true);
					String[] performInitialChecksNew = parseAncCheckSanityBatchFile(readLine,newFileName);
					if(performInitialChecksNew[0].equals("true")){
						myWriter.write(encryptLogFile(performInitialChecksNew[1]));
						myWriter.write(System.lineSeparator());
						myWriter.close();
					}
				 }
				System.exit(0);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}else {
			parseInformation(args,logFileName);
		}
	}

	public static void parseInformation(String[] cmdArgument, String logFileName){
		int roomId = 0;

		GetCommandLineArguments argTimestamp = TaskFileReader.getArgument("-T", cmdArgument);
		GetCommandLineArguments argToken = TaskFileReader.getArgument("-K", cmdArgument);
		GetCommandLineArguments argEmployeeName = TaskFileReader.getArgument("-E", cmdArgument);
		GetCommandLineArguments argGuestName = TaskFileReader.getArgument("-G", cmdArgument);
		GetCommandLineArguments argEventArrival = TaskFileReader.getArgument("-A", cmdArgument);
		GetCommandLineArguments argEventLeft = TaskFileReader.getArgument("-L", cmdArgument);
		GetCommandLineArguments argRoomId = TaskFileReader.getArgument("-R", cmdArgument);

		User user = new User(argEmployeeName.identifierVrified() ? argEmployeeName.getidentifierValue() : argGuestName.getidentifierValue(), argEmployeeName.identifierVrified() ? User.USER_EMPLOYEE : User.USER_GUEST);

		if (argRoomId.identifierVrified()) {
			if (!argRoomId.getidentifierValue().matches(REGEX_INT)) {
				System.out.println("invalid");
				System.exit(255);
			}

			roomId = Integer.parseInt(argRoomId.getidentifierValue().replaceFirst(REGEX_OMIT_ZEROS, ""));

			if (roomId < MIN_ROOM_VAL || roomId > MAX_INT_VAL) {
				System.out.println("invalid");
				System.exit(255);
			}
		}

		if (!argTimestamp.identifierVrified()) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (!argTimestamp.getidentifierValue().matches(REGEX_INT)) {
			System.out.println("invalid");
			System.exit(255);
		}

		InputArgument argumentParser = new InputArgument(
				Integer.parseInt(argTimestamp.getidentifierValue()),
				argToken.getidentifierValue(),
				user,
				argEventArrival.identifierVrified() ? User.EVENT_ARRIVAL : User.EVENT_DEPARTURE,
				argRoomId.identifierVrified() ? roomId : User.ROOM_GALLERY
		);

		boolean performInitialChecksOld = performInitialChecks(argTimestamp, argToken, argEmployeeName, argGuestName
				, argEventArrival, argEventLeft, user, argumentParser, logFileName);

		if (!performInitialChecksOld)
			System.exit(0);

		String argument = String.valueOf(argumentParser.getTimestampInformation());
		argument = argument + " " + argumentParser.getTokenInformation();
		if (argumentParser.getUserInformation().getUserType() == User.USER_EMPLOYEE) {
			argument = argument + " 1 " + argumentParser.getUserInformation().getUserName();
		} else {
			argument = argument + " 2 " + argumentParser.getUserInformation().getUserName();
		}
		if (argumentParser.getEventTypeInformation() == User.EVENT_ARRIVAL) {
			argument = argument + " 1";
		} else {
			argument = argument + " 2";
		}
		if (argumentParser.getRoomUniqueId() == User.ROOM_GALLERY) {
			argument = argument + " -1";
		} else {
			argument = argument + " " + argumentParser.getRoomUniqueId();
		}
		try {
			String encrytptedText = encryptLogFile(argument)+ " \n";
			String directoryName = "";
			if (logFileName.contains("/")){
				String[] arrayArgument = logFileName.split("/");
				String[] slicedDirectoryName= Arrays.copyOf(arrayArgument, arrayArgument.length-1);
				directoryName = String.join("/", slicedDirectoryName);
				logFileName = arrayArgument[arrayArgument.length - 1];
				File directory = new File(directoryName);
				if (! directory.exists()){
					System.out.println("invalid no such directory ");
					System.exit(255);
					directory.mkdirs();
				}
				logFileName = directoryName + "/" + logFileName;
			}
			File file = new File(logFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter myWriter = new FileWriter(file, true);
			myWriter.write(encrytptedText);
			myWriter.close();
			System.exit(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}

	public static String encryptLogFile(String value) {
		String key = "aesEncryptionKey";
		String IV = "encryptionIntVec";

		try {
			IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipherText = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipherText.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encryptedBytes = cipherText.doFinal(value.getBytes());
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.exit(0);
		}
		return null;
	}

	public static boolean performInitialChecks(GetCommandLineArguments argTimestamp,GetCommandLineArguments argToken
			, GetCommandLineArguments argEmployeeName, GetCommandLineArguments argGuestName, GetCommandLineArguments argEventArrival
			, GetCommandLineArguments argEventLeft, User user, InputArgument argumentParser, String logFileName) {

		if (Integer.parseInt(argTimestamp.getidentifierValue()) < MIN_INT_VAL ||
				Integer.parseInt(argTimestamp.getidentifierValue()) > MAX_INT_VAL) {
			System.out.println("invalid");
			System.exit(255);
		}


		if (!argToken.identifierVrified()) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (!argToken.getidentifierValue().matches(REGEX_SECRET)) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (argEmployeeName.identifierVrified() && argGuestName.identifierVrified()) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (!argEmployeeName.identifierVrified() && !argGuestName.identifierVrified()) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (argEmployeeName.identifierVrified() && !argEmployeeName.getidentifierValue().matches(REGEX_NAME)) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (argGuestName.identifierVrified() && !argGuestName.getidentifierValue().matches(REGEX_NAME)) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (!argEventArrival.identifierVrified() && !argEventLeft.identifierVrified()) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (argEventArrival.identifierVrified() && argEventLeft.identifierVrified()) {
			System.out.println("invalid");
			System.exit(255);
		}

		ArrayList<InputArgument> fileContents = TaskFileReader.readFile(logFileName);

		if (fileContents == null) {
			System.out.println("invalid");
			System.exit(255);
		}

		boolean checkFileEmpty = fileContents.size() != 0;
		int fileLastIndex = fileContents.size() - 1;
		User argumentUser = new User(argumentParser.getUserInformation().getUserName(), argumentParser.getUserInformation().getUserType());

		if (!checkFileEmpty) {
			if (argumentParser.getEventTypeInformation() != User.EVENT_ARRIVAL || argumentParser.getRoomUniqueId() != User.ROOM_GALLERY) {
				System.out.println("invalid");
				System.exit(255);
			}
			return true;
		}

		if (!InputArgument.checkTokenCorrectness(fileContents.get(0).getTokenInformation(), argumentParser.getTokenInformation())) {
			System.out.println("invalid");
			System.exit(255);
		}

		if (!timeStampCorrectness(fileContents.get(fileLastIndex).getTimestampInformation(), argumentParser.getTimestampInformation())) {
			System.out.println("invalid");
			System.exit(255);
		}

		InputArgument trackUser = TaskFileReader.getLastLogInformation(fileContents, fileLastIndex, argumentUser);
		if (logGalleryEntryInformation(argumentParser.getEventTypeInformation(), argumentParser.getRoomUniqueId())) {
			if (!User.checkUserPermissionEnterGallery(trackUser)) {
				System.out.println("invalid");
				System.exit(255);
			}
		}


		if (logGalleryExitInformation(argumentParser.getEventTypeInformation(), argumentParser.getRoomUniqueId())) {
			if (!User.checkUserPermissionLeaveGallery(trackUser)) {
				System.out.println("invalid");
				System.exit(255);
			}
		}

		if (logRoomEntryInformation(argumentParser.getEventTypeInformation(), argumentParser.getRoomUniqueId())) {
			if (!User.checkUserPermissionEnterRoom(trackUser)) {
				System.out.println("invalid");
				System.exit(255);
			}
		}

		if (logRoomExitInformation(argumentParser.getEventTypeInformation(), argumentParser.getRoomUniqueId())) {
			if (!User.checkUserPermissionLeaveRoom(trackUser, argumentParser.getRoomUniqueId())) {
				System.out.println("invalid");
				System.exit(255);
			}
		}

		return true;
	}

	public static boolean logRoomEntryInformation(int eventTypeInformation, int roomUniqueId) {
		boolean identifier =  eventTypeInformation == EVENT_ARRIVAL && roomUniqueId >= 0;
		return identifier;
	}

	public static boolean logRoomExitInformation(int eventTypeInformation, int roomUniqueId) {
		boolean identifier = eventTypeInformation == EVENT_DEPARTURE && roomUniqueId >= 0;
		return identifier;
	}

	public static boolean logGalleryEntryInformation(int eventTypeInformation, int roomUniqueId) {
		boolean identifier = eventTypeInformation == EVENT_ARRIVAL && roomUniqueId == -1;
		return identifier;
	}

	public static boolean logGalleryExitInformation(int eventTypeInformation, int roomUniqueId) {
		boolean identifier = eventTypeInformation == EVENT_DEPARTURE && roomUniqueId == -1;
		return identifier;
	}

	public static boolean timeStampCorrectness(int fileTimestamp, int enteredTimestamp) {
		return enteredTimestamp > fileTimestamp;
	}


	public static String[] parseAncCheckSanityBatchFile(String[] cmdArgument, String logFileName) {
		int roomId = 0;
		String passChecks = "true";
		GetCommandLineArguments argTimestamp = TaskFileReader.getArgument("-T", cmdArgument);
		GetCommandLineArguments argToken = TaskFileReader.getArgument("-K", cmdArgument);
		GetCommandLineArguments argEmployeeName = TaskFileReader.getArgument("-E", cmdArgument);
		GetCommandLineArguments argGuestName = TaskFileReader.getArgument("-G", cmdArgument);
		GetCommandLineArguments argEventArrival = TaskFileReader.getArgument("-A", cmdArgument);
		GetCommandLineArguments argEventLeft = TaskFileReader.getArgument("-L", cmdArgument);
		GetCommandLineArguments argRoomId = TaskFileReader.getArgument("-R", cmdArgument);

		User user = new User(argEmployeeName.identifierVrified() ? argEmployeeName.getidentifierValue() : argGuestName.getidentifierValue(), argEmployeeName.identifierVrified() ? User.USER_EMPLOYEE : User.USER_GUEST);

		if (argRoomId.identifierVrified()) {
			if (!argRoomId.getidentifierValue().matches(REGEX_INT)) {
				System.out.println("invalid");
				passChecks = "false";
			}

			roomId = Integer.parseInt(argRoomId.getidentifierValue().replaceFirst(REGEX_OMIT_ZEROS, ""));

			if (roomId < MIN_ROOM_VAL || roomId > MAX_INT_VAL) {
				System.out.println("invalid");
				passChecks = "false";
			}
		}

		InputArgument argumentParser = new InputArgument(
				Integer.parseInt(argTimestamp.getidentifierValue()),
				argToken.getidentifierValue(),
				user,
				argEventArrival.identifierVrified() ? User.EVENT_ARRIVAL : User.EVENT_DEPARTURE,
				argRoomId.identifierVrified() ? roomId : User.ROOM_GALLERY
		);
		if (!argTimestamp.identifierVrified()) {
			System.out.println("invalid");
			passChecks = "false";
		}

		if (!argTimestamp.getidentifierValue().matches(REGEX_INT)) {
			System.out.println("invalid");
			passChecks = "false";
		}

		if (Integer.parseInt(argTimestamp.getidentifierValue()) < MIN_INT_VAL ||
				Integer.parseInt(argTimestamp.getidentifierValue()) > MAX_INT_VAL) {
			System.out.println("invalid");
			passChecks = "false";
		}


		if (!argToken.identifierVrified()) {
			System.out.println("invalid");
			passChecks = "false";
		}

		if (!argToken.getidentifierValue().matches(REGEX_SECRET)) {
			System.out.println("invalid");
			passChecks = "false";
		}

		if (!argEmployeeName.identifierVrified() && !argGuestName.identifierVrified()) {
			System.out.println("invalid");
			passChecks = "false";
		}

		if (argEmployeeName.identifierVrified() && !argEmployeeName.getidentifierValue().matches(REGEX_NAME)) {
			System.out.println("invalid");
			passChecks = "false";
		}

		if (argGuestName.identifierVrified() && !argGuestName.getidentifierValue().matches(REGEX_NAME)) {
			System.out.println("invalid");
			passChecks = "false";
		}

		if (!argEventArrival.identifierVrified() && !argEventLeft.identifierVrified()) {
			System.out.println("invalid");
			passChecks = "false";
		}

		String argument = String.valueOf(argumentParser.getTimestampInformation());
		argument = argument + " " + argumentParser.getTokenInformation();
		if (argumentParser.getUserInformation().getUserType() == User.USER_EMPLOYEE) {
			argument = argument + " 1 " + argumentParser.getUserInformation().getUserName();
		} else {
			argument = argument + " 2 " + argumentParser.getUserInformation().getUserName();
		}
		if (argumentParser.getEventTypeInformation() == User.EVENT_ARRIVAL) {
			argument = argument + " 1";
		} else {
			argument = argument + " 2";
		}
		if (argumentParser.getRoomUniqueId() == User.ROOM_GALLERY) {
			argument = argument + " -1";
		} else {
			argument = argument + " " + argumentParser.getRoomUniqueId();

		}

		String[] stringArguments = new String[2];

		stringArguments[0] = passChecks;
		stringArguments[1] = argument;

		return stringArguments;
	}
}