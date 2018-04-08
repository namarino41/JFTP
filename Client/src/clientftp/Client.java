package clientftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


import clientftp.remote.ClientRemote;

public class Client {

	private ClientView clientView;
	private ClientModel clientModel;
	private ClientRemote clientremote;
	
	private static final String LOCAL_LIST_DIRECTORY_CONTENTS = "lls";
	private static final String LOCAL_CHANGE_DIRECTORY = "lcd";
	private static final String REMOTE_CHANGE_DIRECTORY = "cd";
	private static final String REMOTE_LIST_DIRECTORY_CONTENTS = "ls";
	private static final String REMOTE_PRINT_WORKING_DIRECTORY = "pwd";
	private static final String GET = "get";
	private static final String PUSH = "push";
	private static final String CLEAR = "clear";
	private static final String BLANK = "";
	private static final String EXIT = "exit";

	public Client(ClientView clientSideFTPView, ClientModel clientSideFTPModel) {
		this.clientView = clientSideFTPView;
		this.clientModel = clientSideFTPModel;
		try {
			this.clientremote = new ClientRemote(clientSideFTPModel.getInetAddress());
		} catch (IOException e) {
			//connection failure
		}
	}
	
	public void start() {
		parseRequest();
	}

	private void parseRequest() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				String commandLine[] = clientView.getCommandLine(clientModel.getLocalWorkingDirectory(), bufferedReader);
				String request = commandLine[0];
	 
				switch (request) {
					case LOCAL_LIST_DIRECTORY_CONTENTS:
						listDirectoryContents();
						break;
					case LOCAL_CHANGE_DIRECTORY:
						localChangeDirectory(commandLine[1]);
						break;
					case REMOTE_CHANGE_DIRECTORY:
						remoteChangeDirectory(commandLine[1]);
						break;
					case REMOTE_PRINT_WORKING_DIRECTORY:
						remotePrintWorkingDirectory();
						break;
					case REMOTE_LIST_DIRECTORY_CONTENTS:
						remoteListDirectoryContents();
						break;
					case GET:
						getFile(commandLine);
						break;
					case PUSH:
						pushFile(commandLine);
						break;
					case CLEAR: 
						clear();
						break;
					case EXIT:
						clientremote.exit();
						return;
					case BLANK:
						break;
					default:
						clientView.commandNotFound(request);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("err: missing command argument");
			} catch (IOException e) {
				System.out.println("err: a problem has occured with input");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void listDirectoryContents() {
		File localFilesDirectories[] = clientModel.getLocalFilesDirectories(null);
		clientView.listDirectoryContents(localFilesDirectories);
	}

	private void localChangeDirectory(String path) throws IOException {	
		boolean success = clientModel.changeDirectory(path);

		if (!success)
			clientView.directoryDoesNotExist(path);
	}
	
	private void remoteChangeDirectory(String directory) throws IOException {
		String remoteWorkingDirectory = clientModel.getRemoteWorkingDirectory();
		String newDirectory = clientremote.changeDirectory(remoteWorkingDirectory, directory);
		
		if (!newDirectory.isEmpty())
			clientModel.setRemoteWorkingDirectory(newDirectory);
		else
			clientView.directoryDoesNotExist(directory);
	}
	
	private void remotePrintWorkingDirectory() throws IOException {
		String remoteWorkingDirectory = clientModel.getRemoteWorkingDirectory();
		clientView.remoteWorkingDirectory(remoteWorkingDirectory);
	}
	
	private void remoteListDirectoryContents() throws ClassNotFoundException, IOException {
		clientView.listDirectoryContents(clientremote.listDirectoryContents());
	}

	private void getFile(String commandLine[]) throws IOException {
		String fileName;

		if (commandLine.length > 1) {
			for (int i = 1; i < commandLine.length; i++) {
				fileName = commandLine[i];
				if (clientremote.fileExists(fileName)) {
					File file = new File(clientModel.getLocalWorkingDirectory() + File.separator + fileName);
					clientremote.getFile(fileName, file, clientView);
				} else {
					clientView.fileDoesNotExist(fileName);
				}
			}
		} else {
			clientView.missingFileOperand();
		}
	}

	private void pushFile(String commandLine[]) throws IOException {
		String fileName;

		if (commandLine.length > 1) {
			for (int i = 1; i < commandLine.length; i++) {
				fileName = commandLine[i];
				if (clientModel.fileExists(fileName)) {
					File file = clientModel.getFile(fileName);
					long fileSize = clientModel.getFileSize(file);
					
					clientremote.pushFile(fileName, file, fileSize, clientView);
				} else {
					clientView.fileDoesNotExist(fileName);
				}
			}
		} else {
			clientView.missingFileOperand();
		}
	}

	//move to view
	private void clear() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}
}