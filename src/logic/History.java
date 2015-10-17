package logic;
import global.Command;

import java.util.ArrayList;
import java.util.logging.Logger;

import parser.Parser;

public class History {
	Logger logger = Logger.getGlobal();
	private static History historyInstance = null;
	ArrayList<Command> commandHistoryList  = new ArrayList<Command>();
	ArrayList<String> commandStringHistoryList = new ArrayList<String>(); // for future previous command string
	ArrayList<Command> commandUndoHistoryList = new ArrayList<Command>();
	
	private History() {
		// to make constructor private
	}
	
	boolean pushCommand(Command commandObject, boolean isForUndo) {
		if (isForUndo) {
			commandHistoryList.add(commandObject);
			commandUndoHistoryList.clear();
		} else {
			commandUndoHistoryList.add(commandObject);
		}
		return true;
	}
	
	boolean pushUndoCommand(Command commandObject) {
		commandHistoryList.add(commandObject);
		return true;
	}
	
	Command getPreviousCommand(boolean isForUndo) {
		ArrayList<Command> arrayListToUse = (isForUndo) ? commandHistoryList : commandUndoHistoryList; 
		int historySize = arrayListToUse.size();
		if (historySize > 0) {
			Command commandObjectToReturn = arrayListToUse.get(historySize - 1);
			arrayListToUse.remove(historySize - 1);
			return commandObjectToReturn;
		} else {
			return null;
		}
	}
	
	public static History getInstance(){
		if(historyInstance == null){
			historyInstance = new History();
		}
		return historyInstance;
	}
}
