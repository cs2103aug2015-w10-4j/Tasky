package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class storage {
	public static String filePath="save.txt";
	
public static boolean writeitem(ArrayList<Task>task) throws IOException{
	String content = "";
	for(int i = 0; i <task.size(); i ++){
		content += task.get(i).getName() + "\r\n";
	}
	
	File file = new File(filePath);
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);
	bw.write(content);
	//bw.newLine();
 	bw.close();
	return true;
}

//save result to a path
//input: new path (path to save)
//return true if path exists
//return false if path does not exist
public static boolean saveFileToPath(String path) throws IOException{
	File file = new File(path);
	if (!file.exists()){
		file.createNewFile();
		filePath = path;
		return false;
	}
	filePath = path;
	return true;
}

public static ArrayList<Task> getItemList() throws FileNotFoundException{
	File file = new File(filePath);
	Scanner sc = new Scanner(file);
	ArrayList<Task>tasklist = new ArrayList<Task>();
	
	while (sc.hasNext()){
		String taskName=sc.next();
		Task tempTask = new Task(taskName);
		tasklist.add(tempTask);
	}
	

return tasklist;
}

}
