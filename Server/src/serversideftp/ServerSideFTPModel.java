package serversideftp;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerSideFTPModel {
	
	private Path currentDirectory;
	
	
	public ServerSideFTPModel() {
		currentDirectory = Paths.get(System.getProperty("user.home"));
		System.out.println(currentDirectory);
	}
	
	public String getCurrentPath() {
		return currentDirectory.toString();
	}
	
	public boolean fileExists(String fileName) {
		File directory = new File(currentDirectory.toString());	
		
		for (File file : directory.listFiles()) {
			if (file.getName().equals(fileName))
				return true;
		}
		return false;
	}
	
	public File getFile(String fileName) {
		return new File(currentDirectory + File.separator + fileName);
	}
	
	public long getFileSize(File file) {
		return file.length();
	}
	
	public File[] listFilesDirectories() {		
		return new File(currentDirectory.toString()).listFiles();
	}
	
	public boolean changeDirectory(String directory) {
		if (directory.equals("..") && currentDirectory.getParent() != null) {
			currentDirectory = currentDirectory.getParent();
			return true;
		} else if (!directory.equals("..")){
			String newPath = currentDirectory.toString() + File.separator + directory;
			File newDirectory = new File(newPath);
			if (newDirectory.isDirectory()) {
				currentDirectory = Paths.get(newPath);
				return true;
			}
		}
		return false;
	}
}
