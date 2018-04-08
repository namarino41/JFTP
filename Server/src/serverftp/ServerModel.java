package serverftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerModel {
	
	public ServerModel() {
	}
	
	public boolean fileExists(String workingDirectory, String fileName) {
		File directory = new File(workingDirectory);	
		
		for (File file : directory.listFiles()) {
			if (file.getName().equals(fileName))
				return true;
		}
		return false;
	}
	
	public File getFile(String workingDirectory, String fileName) {
		return new File(workingDirectory + File.separator + fileName);
	}
	
	public long getFileSize(File file) {
		return file.length();
	}
	
	public File[] listDirectoryContents(String workingDirectory) {		
		return new File(workingDirectory).listFiles();
	}
	
	public String changeDirectory(String workingDirectory, String directory) throws IOException {			
		if (directory.startsWith("/")) {
			String newDirectory = changeDirectoryAbsolutePath(workingDirectory, directory);
			if (newDirectory != "")
				return newDirectory;
			else 
				return "";
		} else {
			String newDirectory = changeDirectoryRelativePath(workingDirectory, directory);
			if (newDirectory != "") 
				return newDirectory;
			else
				return "";
		}
	}
	
	private String changeDirectoryAbsolutePath(String workingDirectory, String directory) throws IOException {
		File newDirectory = new File(directory);
		
		if (newDirectory.isDirectory())
			return newDirectory.getCanonicalPath();
		else
			return "";
	}
	
	
	private String changeDirectoryRelativePath(String workingDirectory, String directory) throws IOException {
		String newPath = workingDirectory.toString() + File.separator + directory;
		File newDirectory = new File(newPath);
		
		if (newDirectory.isDirectory())
			return newDirectory.getCanonicalPath();
		else
			return "";
	}
}
