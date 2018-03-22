package clientftp;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientModel {
	
	private String host;
	private Path homeDirectory;
	private Path currentDirectory;
	
	public ClientModel(String inetAddress) { 
		this.host = inetAddress;
		homeDirectory = Paths.get(System.getProperty("user.home"));
		currentDirectory = Paths.get(System.getProperty("user.home"));
	}
	
	public String getInetAddress() {
		return host;
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
	
	public File[] getLocalFilesDirectories(String path) {
		return new File(currentDirectory.toString()).listFiles();
	}
	
	public boolean changeDirectory(String directory) {
		if (directory == null) {
			currentDirectory = homeDirectory;
		} else if (directory.equals("..") && currentDirectory.getParent() != null) {
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
