package serversideftp;

import java.io.File;
import java.io.IOException;

import serversideftp.remoteconnection.ServerSideFTPRemoteHandler;

public class ServerSideFTPController {
	
	ServerSideFTPRemoteHandler remoteHandler;
	private ServerSideFTPModel serverSideFTPModel;

	private static final int GET_CODE = 1;
	private static final int PUSH_CODE = 2;
	private static final int CHANGE_DIRECTORY_CODE = 3;
	private static final int PRINT_WORKING_DIRECTORY_CODE = 4;
	private static final int FILE_EXISTS_CODE = 5;
	private static final int LIST_FILES_DIRECTORIES_CODE = 6;
	private static final int EXIT_CODE = 0;

	public ServerSideFTPController(ServerSideFTPModel serverSideFTPModel) throws IOException {
		this.serverSideFTPModel = serverSideFTPModel;
		
		while (true) {
			try {
				remoteHandler = new ServerSideFTPRemoteHandler();
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
				command = remoteHandler.getCommand();
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
		remoteHandler.printWorkingDirectory(serverSideFTPModel.getCurrentPath());
	}
	
	private void changeDirectory() throws IOException {
		String fileName = remoteHandler.getFileName();
		boolean success = serverSideFTPModel.changeDirectory(fileName);
		
		remoteHandler.changeDirectory(success);
	}
	
	private void fileExists() throws IOException {
		String fileName = remoteHandler.getFileName();
		remoteHandler.fileExists(serverSideFTPModel.fileExists(fileName));
	}

	private void pushFile() throws IOException {
		File file = serverSideFTPModel.getFile(remoteHandler.getFileName());
		long fileSize = serverSideFTPModel.getFileSize(file);
		
		remoteHandler.pushFile(file, fileSize);
	} 

	private void listFilesDirectories() throws IOException {
		remoteHandler.listFilesDirectories(serverSideFTPModel.listFilesDirectories());
		
	}
	
	private void getFile() throws IOException {
		String fileName = remoteHandler.getFileName();
		File file = new File(serverSideFTPModel.getCurrentPath() + File.separator + fileName);
	
		remoteHandler.getFile(file);
	}
	
	private void exit() {
		remoteHandler.exit();
	}
}