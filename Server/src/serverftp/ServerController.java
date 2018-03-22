package serverftp;

import java.io.File;
import java.io.IOException;

import serverftp.remote.ServerRemoteHandler;

public class ServerController {
	
	ServerRemoteHandler serverRemoteHandler;
	private ServerModel serverModel;

	private static final int GET_CODE = 1;
	private static final int PUSH_CODE = 2;
	private static final int CHANGE_DIRECTORY_CODE = 3;
	private static final int PRINT_WORKING_DIRECTORY_CODE = 4;
	private static final int FILE_EXISTS_CODE = 5;
	private static final int LIST_FILES_DIRECTORIES_CODE = 6;
	private static final int EXIT_CODE = 0;

	public ServerController(ServerModel serverModel) throws IOException {
		this.serverModel = serverModel;
		
		while (true) {
			try {
				serverRemoteHandler = new ServerRemoteHandler();
				parseCommand();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	

	private void parseCommand() {
		int command;
		
		try {
			while (true) {
				command = serverRemoteHandler.getCommand();
				System.out.println(command);
				switch (command) {
					case CHANGE_DIRECTORY_CODE:
						changeDirectory();
						break;
					case PRINT_WORKING_DIRECTORY_CODE:
						printWorkingDirectory();
						break;
					case FILE_EXISTS_CODE:
						fileExists();
						break;
					case LIST_FILES_DIRECTORIES_CODE:
						listFilesDirectories();
						break;
					case GET_CODE:
						pushFile();
						break;
					case PUSH_CODE:
						getFile();
						break;
					case EXIT_CODE:
						exit();
						break;
				}
			}
		} catch (IOException e) {
			exit();
		}
	}
	
	private void printWorkingDirectory() throws IOException {
		serverRemoteHandler.printWorkingDirectory(serverModel.getCurrentPath());
	}
	
	private void changeDirectory() throws IOException {
		String fileName = serverRemoteHandler.getFileName();
		boolean success = serverModel.changeDirectory(fileName);
		
		serverRemoteHandler.changeDirectory(success);
	}
	
	private void fileExists() throws IOException {
		String fileName = serverRemoteHandler.getFileName();
		serverRemoteHandler.fileExists(serverModel.fileExists(fileName));
	}

	private void pushFile() throws IOException {
		File file = serverModel.getFile(serverRemoteHandler.getFileName());
		long fileSize = serverModel.getFileSize(file);
		
		serverRemoteHandler.pushFile(file, fileSize);
	} 

	private void listFilesDirectories() throws IOException {
		serverRemoteHandler.listFilesDirectories(serverModel.listFilesDirectories());
		
	}
	
	private void getFile() throws IOException {
		String fileName = serverRemoteHandler.getFileName();
		File file = new File(serverModel.getCurrentPath() + File.separator + fileName);
	
		serverRemoteHandler.getFile(file);
	}
	
	private void exit() {
		serverRemoteHandler.exit();
	}
}