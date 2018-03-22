package clientftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import clientftp.remote.ClientRemoteHandler;

public class ClientController {

	SSLSocketFactory sslsocketfactory;
	SSLSocket sslsocket;

	private ClientRemoteHandler clientremoteHandler;
	private ClientView clientView;
	private ClientModel clientModel;

	private static final String LOCAL_LIST_FILES_DIRECTORIES = "lls";
	private static final String LOCAL_CHANGE_DIRECTORY = "lcd";
	private static final String REMOTE_CHANGE_DIRECTORY = "cd";
	private static final String REMOTE_LIST_FILES_DIRECTORIES = "ls";
	private static final String REMOTE_PRINT_WORKING_DIRECTORY = "pwd";
	private static final String GET = "get";
	private static final String PUSH = "push";
	private static final String CLEAR = "clear";
	private static final String BLANK = "";
	private static final String EXIT = "exit";
	

	public ClientController(ClientView clientSideFTPView, ClientModel clientSideFTPModel) {
		this.clientView = clientSideFTPView;
		this.clientModel = clientSideFTPModel;
		
		try {
			this.clientremoteHandler = new ClientRemoteHandler(clientSideFTPModel.getInetAddress());
			parseCommand();
		} catch (IOException e) {
			//connection failure
		}
	}

	private void parseCommand() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				String commandLine[] = clientView.getCommandLine(clientModel.getCurrentPath(), bufferedReader);
				String command = commandLine[0];
	 
				switch (command) {
					case LOCAL_LIST_FILES_DIRECTORIES:
						localListFilesDirectories();
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
					case REMOTE_LIST_FILES_DIRECTORIES:
						remoteListFilesDirectories();
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
						clientremoteHandler.exit();
						return;
					case BLANK:
						break;
					default:
						clientView.commandNotFound(command);
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

	private void localListFilesDirectories() {
		File localFilesDirectories[] = clientModel.getLocalFilesDirectories(null);
		clientView.listFilesDirectories(localFilesDirectories);
	}

	private void localChangeDirectory(String directory) {	
		boolean success = clientModel.changeDirectory(directory);

		if (!success)
			clientView.directoryDoesNotExist(directory);
	}
	
	private void remoteChangeDirectory(String directory) throws IOException {
		boolean success = clientremoteHandler.changeDirectory(directory);
		
		if (!success)
			clientView.directoryDoesNotExist(directory);
	}
	
	private void remotePrintWorkingDirectory() throws IOException {
		String remoteWorkingDirectory = clientremoteHandler.printWorkingDirectory();
		clientView.remoteWorkingDirectory(remoteWorkingDirectory);
	}
	
	private void remoteListFilesDirectories() throws ClassNotFoundException, IOException {
		clientView.listFilesDirectories(clientremoteHandler.listFilesDirectories());
	}

	private void getFile(String commandLine[]) throws IOException {
		String fileName;

		if (commandLine.length > 1) {
			for (int i = 1; i < commandLine.length; i++) {
				fileName = commandLine[i];
				if (clientremoteHandler.fileExists(fileName)) {
					File file = new File(clientModel.getCurrentPath() + File.separator + fileName);
					clientremoteHandler.getFile(fileName, file, clientView);
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
					
					clientremoteHandler.pushFile(fileName, file, fileSize, clientView);
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