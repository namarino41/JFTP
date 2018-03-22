package clientftp.remote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import clientftp.ClientView;

public class ClientRemoteHandler {
	
	private static final int HOST_PORT = 6000;
	private static final int TIMEOUT = 5000;
	
	private static final int GET_CODE = 1;
	private static final int PUSH_CODE = 2;
	private static final int REMOTE_CHANGE_DIRECTORY_CODE = 3;
	private static final int REMOTE_PRINT_WORKING_DIRECTORY_CODE = 4;
	private static final int REMOTE_FILE_EXISTS_CODE = 5;
	private static final int REMOTE_LIST_FILES_DIRECTORIES_CODE = 6;
	private static final int EXIT_CODE = 0;
	
	private Socket socket;
	private SocketAddress socketAddress;
	private InputStream inputStream;
	private OutputStream outputStream;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private FileOutputStream fileOutputStream;
	private BufferedOutputStream bufferedOutputStream;
	private FileInputStream fileInputStream;
	private BufferedInputStream bufferedFileInputStream;
	private ObjectInputStream objectInputStream;
	
	public ClientRemoteHandler(String inetAddress) throws IOException {
		initConnection(inetAddress);
		initStreams();
	}
	
	private void initConnection(String inetAddress) throws IOException {
		// sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		// sslsocket = (SSLSocket) sslsocketfactory.createSocket(inetAddress,
		// HOST_PORT);

		socket = new Socket();
		socketAddress = new InetSocketAddress(inetAddress, HOST_PORT);
		socket.connect(socketAddress, TIMEOUT);
	}
	
	private void initStreams() throws IOException {
		inputStream = socket.getInputStream();
		dataInputStream = new DataInputStream(inputStream);
		objectInputStream = new ObjectInputStream(inputStream);

		outputStream = socket.getOutputStream();
		dataOutputStream = new DataOutputStream(outputStream);
	}
	
	public boolean changeDirectory(String directory) throws IOException {
		dataOutputStream.writeInt(REMOTE_CHANGE_DIRECTORY_CODE);
		
		dataOutputStream.writeUTF(directory);
		return dataInputStream.readBoolean();
	}
	
	public String printWorkingDirectory() throws IOException {
		dataOutputStream.writeInt(REMOTE_PRINT_WORKING_DIRECTORY_CODE);
		
		return dataInputStream.readUTF();
	} 
	
	public File[] listFilesDirectories() throws IOException, ClassNotFoundException {		
		dataOutputStream.writeInt(REMOTE_LIST_FILES_DIRECTORIES_CODE);
		
		return (File[]) objectInputStream.readObject();
	}
	
	public void getFile(String fileName, File file, ClientView clientSideFTPView) throws IOException {
		dataOutputStream.writeInt(GET_CODE);
		dataOutputStream.writeUTF(fileName);
		
		long fileSize = 0;
		byte fileDataBuffer[] = new byte[8192];

		int bytesRead = 0;
		long bytesDownloaded = 0;
		double percentDownloaded = 0;

		try {
			fileSize = dataInputStream.readLong();

			fileOutputStream = new FileOutputStream(file);
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

			while (bytesDownloaded != fileSize) {
				bytesRead = inputStream.read(fileDataBuffer);
				bufferedOutputStream.write(fileDataBuffer, 0, bytesRead);
				bufferedOutputStream.flush();

				bytesDownloaded += bytesRead;
				percentDownloaded = ((double) bytesDownloaded / ((double) fileSize)) * 100;
				clientSideFTPView.transferProgress(percentDownloaded, true);
			}

			bufferedOutputStream.flush();
			bufferedOutputStream.close();
			fileOutputStream.close();
			clientSideFTPView.fileTransferDone();
		} catch (IOException exception) {
			System.out.println("something went wrong");
		}
	}
	
	public boolean fileExists(String fileName) throws IOException {
		dataOutputStream.writeInt(REMOTE_FILE_EXISTS_CODE);
		dataOutputStream.writeUTF(fileName);
		return dataInputStream.readBoolean();
	}
	
	public void pushFile(String fileName, File file, long fileSize, ClientView clientSideFTPView) throws IOException {
		dataOutputStream.writeInt(PUSH_CODE);
		dataOutputStream.writeUTF(fileName);
		
		byte fileDataBuffer[] = new byte[8192];
		int bytesRead = 0;
		int bytesUploaded = 0;
		double percentUploaded = 0;

		try {
			fileInputStream = new FileInputStream(file);
			bufferedFileInputStream = new BufferedInputStream(fileInputStream);

			dataOutputStream.writeLong(fileSize);

			while ((bytesRead = bufferedFileInputStream.read(fileDataBuffer)) > 0) {
				outputStream.write(fileDataBuffer, 0, bytesRead);
				
				bytesUploaded += bytesRead;
				percentUploaded = ((double) bytesUploaded / ((double) fileSize)) * 100;
				clientSideFTPView.transferProgress(percentUploaded, false);
			}
			
			outputStream.flush();
			fileInputStream.close();
			bufferedFileInputStream.close();
			clientSideFTPView.fileTransferDone();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		dataInputStream.readBoolean();
	}
	
	public void exit() throws IOException {
		dataOutputStream.writeInt(EXIT_CODE);
		closeConnection();
	} 
	
	private void closeConnection() {
		try {
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
			if (dataInputStream != null)
				dataInputStream.close();
			if (dataOutputStream != null)
				dataOutputStream.close();
			if (socket != null)
				socket.close();
		} catch (IOException | NullPointerException exception) {
		}
	}
}
