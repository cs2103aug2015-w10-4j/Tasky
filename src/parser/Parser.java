package parser;

import global.Command;
import global.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Parser {
	/**
	 * Parses the command string based on keyword
	 * 
	 * @param command
	 * @return commandObject to be executed, or null if invalid
	 */
	private static Parser parserInstance = null;

	// warning messages
	private static final String WARNING_INSUFFICIENT_ARGUMENT = "Warning: '%s': insufficient command arguments";
	private static final String WARNING_INVALID_DAY = "Invalid day specified!";
	private static final String WARNING_INVALID_MONTH = "Invalid month specified!";

	private static final String[] COMMAND_ADD = { "add" };
	private static final String[] COMMAND_EDIT = { "edit" };
	private static final String[] COMMAND_DELETE = { "delete" };
	private static final String[] COMMAND_UNDO = { "undo" };
	private static final String[] COMMAND_REDO = { "redo" };
	private static final String[] COMMAND_EXIT = { "exit" };
	private static final String[] COMMAND_DISPLAY = { "display" };
	private static final String[] COMMAND_SAVETO = { "saveto" };

	private static class WordList {
		String name;
		ArrayList<String> listOfWords = new ArrayList<String>();

		WordList(String listName, String[] wordList) {
			name = listName;
			for (int i = 0; i < wordList.length; i++) {
				listOfWords.add(wordList[i]);
			}
		}

		private String getName() {
			return this.name;
		}

		private String returnIfContainsString(String partOfCommandString) {
			for (int i = 0; i < listOfWords.size(); i++) {
				String curWord = listOfWords.get(i);
				if (partOfCommandString.contains(curWord)) {
					return curWord;
				}
			}
			return null;
		}
	};

	private ArrayList<WordList> keywordDate;
	private ArrayList<WordList> keywordPeriodic;
	private ArrayList<WordList> keywordMonth;
	private ArrayList<WordList> keywordDay;
	private ArrayList<WordList> keywordCommand;
	private static final String ARGUMENT_FROM = "start";
	private static final String ARGUMENT_TO = "end";

	private static final String[] DATE_EVENT = { "start", "end" };
	private static final String[] DATE_END = { "date", "by" };
	private static final String[] DATE_SPECIAL = { "this",
			"next", "tomorrow", "today" };
	private static final String ARGUMENTS_PERIODIC = "every";
	private static final String ARGUMENT_LOC = "loc";
	private static final String DEFAULT_DAY = "friday";

	private static final String[] MONTHS = { "jan", "feb", "mar", "apr", "may",
			"jun", "jul", "aug", "sep", "oct", "nov", "dec" };
	private static final String[] DAYS = { "sunday", "monday", "tuesday",
			"wednesday", "thursday", "friday", "saturday" };

	private Parser() {
		keywordCommand = new ArrayList<WordList>();
		WordList add = new WordList("add", COMMAND_ADD);
		WordList edit = new WordList("edit", COMMAND_EDIT);
		WordList delete = new WordList("delete", COMMAND_DELETE);
		WordList undo = new WordList("undo", COMMAND_UNDO);
		WordList redo = new WordList("redo", COMMAND_REDO);
		WordList display = new WordList("display", COMMAND_DISPLAY);
		WordList saveto = new WordList("saveto", COMMAND_SAVETO);
		WordList exit = new WordList("exit", COMMAND_EXIT);
		keywordCommand.add(add);
		keywordCommand.add(edit);
		keywordCommand.add(delete);
		keywordCommand.add(undo);
		keywordCommand.add(redo);
		keywordCommand.add(display);
		keywordCommand.add(saveto);
		keywordCommand.add(exit);
		
		keywordDate = new ArrayList<WordList>();
		WordList event = new WordList("event", DATE_EVENT);
		WordList end = new WordList("end", DATE_END);
		WordList special = new WordList("special", DATE_SPECIAL);
		keywordDate.add(event);
		keywordDate.add(end);
		keywordDate.add(special);

		keywordMonth = new ArrayList<WordList>();
		WordList threeCharMonth = new WordList("threeCharMonth", MONTHS);
		keywordMonth.add(threeCharMonth);

		keywordDay = new ArrayList<WordList>();
		WordList fullDay = new WordList("fullDay", DAYS);
		keywordDay.add(fullDay);
	}

	/**
	 * Parses the string provided and returns the corresponding object
	 * 
	 * @param command
	 *            user input
	 * @return Command object for execution
	 * @throws Exception
	 *             parsing error message
	 */
	public Command parseCommand(String command) throws Exception {
		String[] commandSplit = command.split(" ", 2);

		String commandWord, arguments = "";
		commandWord = commandSplit[0];
		if (commandSplit.length >= 2) {
			arguments = commandSplit[1];
		}

		Command commandObject;
		if (hasKeyword(commandWord, keywordCommand, "add")) {
			try {
				Task taskObj = new Task();
				extractTaskInformation(taskObj, arguments);
				commandObject = new Command(Command.Type.ADD, taskObj);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(
						WARNING_INSUFFICIENT_ARGUMENT, commandWord));
			}
		} else if (hasKeyword(commandWord, keywordCommand, "edit")) {
			try {
				String[] argumentSplit = arguments.split(" ", 2);
				String[] indexToDelete = { argumentSplit[0] };
				String taskInformation = argumentSplit[1];

				Task taskObj = new Task();
				extractTaskInformation(taskObj, taskInformation);
				commandObject = new Command(Command.Type.EDIT, indexToDelete,
						taskObj);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(
						WARNING_INSUFFICIENT_ARGUMENT, commandWord));
			}
		} else if (hasKeyword(commandWord, keywordCommand, "delete")) {
			if (commandSplit.length >= 2) {
				String[] indexToDelete = arguments.split(" ");

				commandObject = new Command(Command.Type.DELETE, indexToDelete);
			} else {
				throw new Exception(String.format(
						WARNING_INSUFFICIENT_ARGUMENT, commandWord));
			}
		} else if (hasKeyword(commandWord, keywordCommand, "exit")) {
			commandObject = new Command(Command.Type.EXIT);
		} else if (hasKeyword(commandWord, keywordCommand, "display")) {
			commandObject = new Command(Command.Type.DISPLAY);
		} else if (hasKeyword(commandWord, keywordCommand, "undo")) {
			commandObject = new Command(Command.Type.UNDO);
		} else if (hasKeyword(commandWord, keywordCommand, "redo")) {
			commandObject = new Command(Command.Type.REDO);
		} else if (hasKeyword(commandWord, keywordCommand, "saveto")) {
			String[] argumentArray = { arguments };
			commandObject = new Command(Command.Type.SAVETO, argumentArray);
		} else {
			commandObject = null;
		}
		return commandObject;
	}

	/*
	 * Extracts and returns 'name' segment of the command.
	 */
	private String extractTaskName(String arg) throws Exception {
		return arg.split("'")[1];
	}

	private String extractTaskNameWithoutCommand(String arg) {
		return arg.split(" ")[0];
	}

	private boolean extractTaskInformation(Task taskObject, String arguments)
			throws Exception {
		//String taskName = extractTaskNameWithoutCommand(arguments);
		
		String dateExtracted = extractDate(arguments, taskObject);
		String locationExtracted = extractLocation(dateExtracted, taskObject); // for
																				// extension
		taskObject.setName(locationExtracted);

		return true;
	}

	/**
	 * Extracts 'date' segment of the command if present & returns Calendar
	 * object. Extracts 'day' segment of the command if present and returns
	 * Calendar object - current supported parameters before day string are
	 * 'this' and 'next' Special argument: 'tomorrow' will set date to the next
	 * day from current date pre-condition: String must contain DATE_ARGUMENTS,
	 * date parameters are valid dates in format dd MMM yyyy OR String must
	 * contain day arg in lowercase only post-condition: returns parsed Calendar
	 * object if date is present, else return null. Exception if day is not
	 * spelt in full.
	 */
	private String extractDate(String arguments, Task taskObj) throws Exception {
		if (hasKeyword(arguments, keywordDate, "end")) {
			return extractOneDate(arguments, taskObj);
		} else if (hasKeyword(arguments, keywordDate, "event")) {
			return extractTwoDates(arguments, taskObj);
		} else {
			return arguments;
		}

	}

	private boolean hasDate(String argumentString) {
		for (int i = 0; i < keywordDate.size(); i++) {
			WordList curWordList = keywordDate.get(i);
			String wordFound = curWordList
					.returnIfContainsString(argumentString);
			if (wordFound != null) {
				return true;
			}
		}
		return false;
	}

	// construct task when there is just one date in the input
	private String extractOneDate(String arg, Task taskObj) throws Exception {
		Calendar date;
		String keywordToSplitAt = getKeyword(arg, keywordDate, "end");
		String[] newArgs = arg.split(keywordToSplitAt);
		if (hasKeyword(arg, keywordDate, "special")) {
			date = parseSpecialDate(newArgs[1]);
		} else {
			date = parseDate(newArgs[1]);
		}

		taskObj.setEndingTime(date);

		return newArgs[0];
	}

	// construct task when there are both starting time and endingtime
	public String extractTwoDates(String commandArguments, Task taskObj)
			throws Exception {
		Calendar dateOne, dateTwo;
		String taskName = extractTaskNameWithoutCommand(commandArguments);
		taskObj.setName(taskName);
		assert (commandArguments.contains(ARGUMENT_FROM) && commandArguments
				.contains(ARGUMENT_TO));
		String[] endSplit = commandArguments.split(ARGUMENT_TO);
		String[] startSplit = endSplit[0].split(ARGUMENT_FROM);
		String remainingCommandString = startSplit[0];

		if (hasKeyword(startSplit[1], keywordDate, "special")) {
			dateOne = parseSpecialDate(startSplit[1]);
		} else {
			dateOne = parseDate(startSplit[1]);
		}

		if (hasKeyword(endSplit[1], keywordDate, "special")) {
			dateTwo = parseSpecialDate(endSplit[1]);
		} else {
			dateTwo = parseDate(endSplit[1]);
		}

		if (dateOne.before(dateTwo)) {
			taskObj.setEndingTime(dateTwo);
			taskObj.setStartingTime(dateOne);
		} else {
			taskObj.setEndingTime(dateOne);
			taskObj.setStartingTime(dateTwo);
		}

		return remainingCommandString;

	}

	private Calendar parseDate(String dateString) throws Exception {
		dateString = dateString.trim();
		String[] dateArgs = dateString.split(" ");

		int day = Integer.parseInt(dateArgs[0]);

		int month = Arrays.asList(MONTHS).indexOf(dateArgs[1]);
		if (month == -1) {
			month = Calendar.getInstance().get(Calendar.MONTH);
		}

		// year will be set to current year if not specified by user
		int year;
		try {
			year = Integer.parseInt(dateArgs[2]);
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		Calendar date = new GregorianCalendar();
		date.set(year, month, day);
		return date;
	}

	private Calendar parseSpecialDate(String arg) throws Exception {
		Calendar date = new GregorianCalendar();
		String keywordToSplitAt = getKeyword(arg, keywordDate, "special");
		String[] newArgs = arg.split(keywordToSplitAt);

		date = new GregorianCalendar();
		int setDate, today, todayDate, offset;

		today = date.get(Calendar.DAY_OF_WEEK);
		todayDate = date.get(Calendar.DATE);

		if (keywordToSplitAt.equalsIgnoreCase(DATE_SPECIAL[0])) {
			if (!hasKeyword(newArgs[1], keywordDay)) {
				throw new Exception(WARNING_INVALID_DAY);
			}
			offset = dayOfTheWeek(getKeyword(newArgs[1], keywordDay)) - today;
			if (offset < 0) {
				offset += DAYS.length;
			}
			setDate = todayDate + offset;
		} else if (keywordToSplitAt.equalsIgnoreCase(DATE_SPECIAL[1])) {
			if (!hasKeyword(newArgs[1], keywordDay)) {
				throw new Exception(WARNING_INVALID_DAY);
			}
			offset = dayOfTheWeek(getKeyword(newArgs[1], keywordDay)) - today;
			if (offset < 0) {
				offset += DAYS.length;
			}
			setDate = todayDate + offset + DAYS.length;
		} else if (keywordToSplitAt.equalsIgnoreCase(DATE_SPECIAL[2])) {
			setDate = todayDate + 1;
		} else if (keywordToSplitAt.equalsIgnoreCase(DATE_SPECIAL[3])) {
			setDate = todayDate;
		} else {
			offset = dayOfTheWeek(DEFAULT_DAY) - today;
			if (offset < 0) {
				offset += DAYS.length;
			}
			setDate = todayDate + offset;
		}
		date.set(Calendar.DATE, setDate);

		return date;
	}

	/*
	 * Extracts 'loc' segment pre-condition: String must contain
	 * LOCATION_ARGUMENTS post-condition: returns extracted string if
	 * LOCATION_ARGUMENTS is present, else return original string if date is not
	 * present
	 */
	private String extractLocation(String arg, Task taskObj) throws Exception {
		String[] newArgs;
		String returnArg = "";

		if (arg.contains(ARGUMENT_LOC)) {
			newArgs = arg.split(ARGUMENT_LOC);
			taskObj.setLocation(newArgs[1]);
			returnArg = newArgs[0];
		} else {
			returnArg = arg;
		}

		return returnArg;
	}

	private String extractPeriodic(String arg, Task taskObj) {
		if (arg.contains(ARGUMENTS_PERIODIC)) {
			String argPeriodic = arg.split(ARGUMENTS_PERIODIC)[1];
			for (int i = 0; i < DAYS.length; i++) {
				if (argPeriodic.indexOf(DAYS[i]) == 0) {
					return DAYS[i];
				}
			}
		}
		return null;
	}

	private boolean hasKeyword(String str, ArrayList<WordList> keywordType) {
		for (int i = 0; i < keywordType.size(); i++) {
			WordList curWordList = keywordType.get(i);
			if (curWordList.returnIfContainsString(str) != null) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasKeyword(String str, ArrayList<WordList> keywordType,
			String keywordList) {
		for (int i = 0; i < keywordType.size(); i++) {
			WordList curWordList = keywordType.get(i);
			if (curWordList.getName().equals(keywordList)
					&& curWordList.returnIfContainsString(str) != null) {
				return true;
			}
		}
		return false;
	}

	private String getKeyword(String str, ArrayList<WordList> keywordType,
			String keywordList) {
		for (int i = 0; i < keywordType.size(); i++) {
			WordList curWordList = keywordType.get(i);
			if (curWordList.getName().equals(keywordList)) {
				return curWordList.returnIfContainsString(str);
			}
		}
		return null;
	}
	private String getKeyword(String str, ArrayList<WordList> keywordType) {
		for (int i = 0; i < keywordType.size(); i++) {
			WordList curWordList = keywordType.get(i);
			String wordFound = curWordList.returnIfContainsString(str);
			if (wordFound != null) {
				return wordFound;
			}
		}
		return null;
	}

	private int dayOfTheWeek(String dayString) {
		return Arrays.asList(DAYS).indexOf(dayString) + 1;
	}

	public static Parser getInstance() {
		if (parserInstance == null) {
			parserInstance = new Parser();
		}
		return parserInstance;
	}
}