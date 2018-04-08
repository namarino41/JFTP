package clientftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientModel {
	
	private String host;
	private Path homeDirectory;
	private Path currentDirectory;
	private Path remoteDirectory;
	
	public ClientModel(String inetAddress) { 
		this.host = inetAddress;
		homeDirectory = Paths.get(System.getProperty("user.home"));
		currentDirectory = Paths.get(System.getProperty("user.home"));
	}
	
	public String getInetAddress() {
		return host;
	}
	
	public String getCurrentDirectory() {
		return currentDirectory.toString();
	}
	
	public String getRemoteDirectory() {
		return remoteDirectory.toString();
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
	
	public boolean changeDirectory(String path) throws IOException {
		if (path == null) {
			currentDirectory = homeDirectory;
			return true;
		}
			
		if (path.startsWith("/")) {
			if (changeDirectoryAbsolutePath(path))
				return true;
			else 
				return false;
		} else {
			if (changeDirectoryRelativePath(path)) 
				return true;
			else
				return false;
		}
	}
	
	private boolean changeDirectoryAbsolutePath(String path) throws IOException {
		File newDirectory = new File(path);
		
		if (newDirectory.isDirectory()) {
			currentDirectory = Paths.get(newDirectory.getCanonicalPath());
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean changeDirectoryRelativePath(String path) throws IOException {
		String newPath = currentDirectory.toString() + File.separator + path;
		File newDirectory = new File(newPath);
		
		if (newDirectory.isDirectory()) {
			currentDirectory = Paths.get(newDirectory.getCanonicalPath());
			return true;
		} else {
			return false;
		}
	}
}