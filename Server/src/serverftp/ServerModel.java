package serverftp;

import java.io.File;
import java.io.IOException;

public class ServerModel {
	
	public ServerModel() {
	}
	
	public boolean fileExists(String fileName) {
		return new File(fileName).exists();	
	}
	
	public File getFile(String workingDirectory, String fileName) {
		return new File(workingDirectory + File.separator + fileName);
	}
	
	public File getFile(String fileName) {
		return new File(fileName);
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
