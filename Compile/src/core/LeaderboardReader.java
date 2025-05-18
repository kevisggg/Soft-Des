package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LeaderboardReader {
	public static LeaderboardSorter loadLeaderboardFromFile(String filename) {
	    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
	        return (LeaderboardSorter) ois.readObject();
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	        return new LeaderboardSorter(); // return empty sorter if error
	    }
	}
}
