package serverftp;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import serverftp.remote.ServerRemote;


public class Server {
	private ServerSocket serverSocket;
	private ServerModel serverModel;
	
	private static final int HOST_PORT = 6000;
	
	public Server(ServerModel serverModel) throws IOException {
		//serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(HOST_PORT);
		serverSocket = new ServerSocket(HOST_PORT);
		this.serverModel = serverModel;
	}
	
	public void start() {
		try {
			acceptClients();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void acceptClients() throws IOException {
		while (true) {
			//SSLSocket client = (SSLSocket) serverSocket.accept();
			Socket client = serverSocket.accept();
			Thread worker = new Thread(new ServerWorker(client, serverModel));
			worker.start();
		}
	}
	
	
	private class ServerWorker implements Runnable {

		private ServerRemote serverRemote;
		private ServerModel serverModel;

		private static final int GET_CODE = 1;
		private static final int PUSH_CODE = 2;
		private static final int CHANGE_DIRECTORY_CODE = 3;
		private static final int PRINT_WORKING_DIRECTORY_CODE = 4;
		private static final int FILE_EXISTS_CODE = 5;
		private static final int LIST_FILES_DIRECTORIES_CODE = 6;
		private static final int EXIT_CODE = 0;
		
			
		public ServerWorker(Socket client, ServerModel serverModel) throws IOException {
			this.serverModel = serverModel;

			try {
				serverRemote = new ServerRemote(client);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void parseCommand() {
			int command;

			try {
				while (true) {
					command = serverRemote.getCommand();
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
			serverRemote.printWorkingDirectory(serverModel.getCurrentDirectory());
		}

		private void changeDirectory() throws IOException {			
			String fileName = serverRemote.getFileName();
			boolean success = serverModel.changeDirectory(fileName);

			serverRemote.changeDirectory(success);
		}

		private void fileExists() throws IOException {
			String fileName = serverRemote.getFileName();
			serverRemote.fileExists(serverModel.fileExists(fileName));
		}

		private void pushFile() throws IOException {
			File file = serverModel.getFile(serverRemote.getFileName());
			long fileSize = serverModel.getFileSize(file);

			serverRemote.pushFile(file, fileSize);
		}

		private void listFilesDirectories() throws IOException {
			serverRemote.listFilesDirectories(serverModel.listFilesDirectories());
		}

		private void getFile() throws IOException {
			String fileName = serverRemote.getFileName();
			File file = new File(serverModel.getCurrentDirectory() + File.separator + fileName);

			serverRemote.getFile(file);
		}

		private void exit() {
			serverRemote.exit();
		}

		@Override
		public void run() {
			while (true) {
				parseCommand();
			}
		}
	}
}