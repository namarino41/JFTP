package clientftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientModel {
	
	private String host;
	private Path homeDirectory;
	private Path localWorkingDirectory;
	private Path remoteWorkingDirectory;
	
	public ClientModel(String inetAddress) { 
		this.host = inetAddress;
		localWorkingDirectory = Paths.get(System.getProperty("user.home"));
		remoteWorkingDirectory = Paths.get(System.getProperty("user.home"));
	}
	
	public String getInetAddress() {
		return host;
	}
	
	public String getLocalWorkingDirectory() {
		return localWorkingDirectory.toString();
	}
	
	public String getRemoteWorkingDirectory() {
		return remoteWorkingDirectory.toString();
	}
	
	public void setRemoteWorkingDirectory(String directory) {
		 remoteWorkingDirectory = Paths.get(directory);
	}
	
	public boolean fileExists(String fileName) {
		File directory = new File(localWorkingDirectory.toString());
		
		for (File file : directory.listFiles()) {
			if (file.getName().equals(fileName))
				return true;
		}
		return false;
	}
	
	public File getFile(String fileName) {
		return new File(localWorkingDirectory + File.separator + fileName);
	}
	
	public long getFileSize(File file) {
		return file.length();
	}
	
	public File[] getLocalFilesDirectories(String path) {
		return new File(localWorkingDirectory.toString()).listFiles();
	}
	
	public boolean changeDirectory(String path) throws IOException {
		if (path == null) {
			localWorkingDirectory = homeDirectory;
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
			localWorkingDirectory = Paths.get(newDirectory.getCanonicalPath());
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean changeDirectoryRelativePath(String path) throws IOException {
		String newPath = localWorkingDirectory.toString() + File.separator + path;
		File newDirectory = new File(newPath);
		
		if (newDirectory.isDirectory()) {
			localWorkingDirectory = Paths.get(newDirectory.getCanonicalPath());
			return true;
		} else {
			return false;
		}
	}
}