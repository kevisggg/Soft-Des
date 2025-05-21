package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import pacman.leaderboard.LeaderboardManager;
import spaceInvaders.leaderboard.SILeaderboardManager;

public class LeaderboardReader {
	public static LeaderboardSorter loadLeaderboardFromFile(String filename) {
		File file = new File(filename);
	    if (!file.exists()) {
	        System.out.println("File not found. Leaderboard empty");
	        //LeaderboardSorter newSorter = new LeaderboardSorter();
	        //saveLeaderboardToFile(newSorter, filename);
	        return null;
	    }
	    else {
	    	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
		        return (LeaderboardSorter) ois.readObject();
		    } catch (IOException | ClassNotFoundException e) {
		        e.printStackTrace();
		        return new LeaderboardSorter();
		    }
	    }
		
	}

	public static LeaderboardManager loadPMManager(String filename) {
		File file = new File(filename);
	    if (!file.exists()) {
	        System.out.println("File not found. Leaderboard empty");
	        //LeaderboardSorter newSorter = new LeaderboardSorter();
	        //saveLeaderboardToFile(newSorter, filename);
	        return null;
	    }
	    else {
	    	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
		        return (LeaderboardManager) ois.readObject();
		    } catch (IOException | ClassNotFoundException e) {
		        e.printStackTrace();
		        return new LeaderboardManager();
		    }
	    }
	}

	public static SILeaderboardManager loadSIManager(String filename) {
		File file = new File(filename);
	    if (!file.exists()) {
	        System.out.println("File not found. Leaderboard empty");
	        //LeaderboardSorter newSorter = new LeaderboardSorter();
	        //saveLeaderboardToFile(newSorter, filename);
	        return null;
	    }
	    else {
	    	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
		        return (SILeaderboardManager) ois.readObject();
		    } catch (IOException | ClassNotFoundException e) {
		        e.printStackTrace();
		        return new SILeaderboardManager();
		    }
	    }
	}
}
