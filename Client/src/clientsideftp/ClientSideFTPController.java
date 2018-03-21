package clientsideftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import clientsideftp.remoteconnection.ClientSideFTPRemoteHandler;

public class ClientSideFTPController {

	SSLSocketFactory sslsocketfactory;
	SSLSocket sslsocket;

	private ClientSideFTPRemoteHandler remoteHandler;
	private ClientSideFTPView clientSideFTPView;
	private ClientSideFTPModel clientSideFTPModel;

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
	

	public ClientSideFTPController(ClientSideFTPView clientSideFTPView, ClientSideFTPModel clientSideFTPModel) {
		this.clientSideFTPView = clientSideFTPView;
		this.clientSideFTPModel = clientSideFTPModel;
		
		try {
			this.remoteHandler = new ClientSideFTPRemoteHandler(clientSideFTPModel.getInetAddress());
			parseCommand();
		} catch (IOException e) {
			//connection failure
		}
	}

	private void parseCommand() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				String commandLine[] = clientSideFTPView.getCommandLine(clientSideFTPModel.getCurrentPath(), bufferedReader);
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
						remoteHandler.exit();
						return;
					case BLANK:
						break;
					default:
						clientSideFTPView.commandNotFound(command);
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
		File localFilesDirectories[] = clientSideFTPModel.getLocalFilesDirectories(null);
		clientSideFTPView.listFilesDirectories(localFilesDirectories);
	}

	private void localChangeDirectory(String directory) {	
		boolean success = clientSideFTPModel.changeDirectory(directory);

		if (!success)
			clientSideFTPView.directoryDoesNotExist(directory);
	}
	
	private void remoteChangeDirectory(String directory) throws IOException {
		boolean success = remoteHandler.changeDirectory(directory);
		
		if (!success)
			clientSideFTPView.directoryDoesNotExist(directory);
	}
	
	private void remotePrintWorkingDirectory() throws IOException {
		String remoteWorkingDirectory = remoteHandler.printWorkingDirectory();
		clientSideFTPView.remoteWorkingDirectory(remoteWorkingDirectory);
	}
	
	private void remoteListFilesDirectories() throws ClassNotFoundException, IOException {
		clientSideFTPView.listFilesDirectories(remoteHandler.listFilesDirectories());
	}

	private void getFile(String commandLine[]) throws IOException {
		String fileName;

		if (commandLine.length > 1) {
			for (int i = 1; i < commandLine.length; i++) {
				fileName = commandLine[i];
				if (remoteHandler.fileExists(fileName)) {
					File file = new File(clientSideFTPModel.getCurrentPath() + File.separator + fileName);
					remoteHandler.getFile(fileName, file, clientSideFTPView);
				} else {
					clientSideFTPView.fileDoesNotExist(fileName);
				}
			}
		} else {
			clientSideFTPView.missingFileOperand();
		}
	}

	private void pushFile(String commandLine[]) throws IOException {
		String fileName;

		if (commandLine.length > 1) {
			for (int i = 1; i < commandLine.length; i++) {
				fileName = commandLine[i];
				if (clientSideFTPModel.fileExists(fileName)) {
					File file = clientSideFTPModel.getFile(fileName);
					long fileSize = clientSideFTPModel.getFileSize(file);
					
					remoteHandler.pushFile(fileName, file, fileSize, clientSideFTPView);
				} else {
					clientSideFTPView.fileDoesNotExist(fileName);
				}
			}
		} else {
			clientSideFTPView.missingFileOperand();
		}
	}

	//move to view
	private void clear() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}
}