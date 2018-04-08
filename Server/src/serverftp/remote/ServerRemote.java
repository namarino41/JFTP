package serverftp.remote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocket;


public class ServerRemote {
	private Socket client;
	private InputStream inputStream;
	private OutputStream outputStream;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private FileInputStream fileInputStream;
	private BufferedInputStream bufferedFileInputStream;
	private FileOutputStream fileOutputStream;
	private BufferedOutputStream bufferedFileOutputStream;
	private ObjectOutputStream objectOutputStream;
	
	
	public ServerRemote(Socket client) throws IOException {
		this.client = client;
		System.out.println("Connection established: " + client.getInetAddress());
		initStreams();
	}
	
	private void initStreams() throws IOException {
		inputStream = client.getInputStream();
		dataInputStream = new DataInputStream(inputStream);

		outputStream = client.getOutputStream();
		dataOutputStream = new DataOutputStream(outputStream);
		objectOutputStream = new ObjectOutputStream(outputStream);
	}
	
	public int getCommand() throws IOException {
		return dataInputStream.readInt();
	}
	
	public void printWorkingDirectory(String workingDirectory) throws IOException {
		dataOutputStream.writeUTF(workingDirectory);
	}
	
	public void changeDirectory(boolean success) throws IOException {
		dataOutputStream.writeBoolean(success);
	}
	
	public void fileExists(boolean exists) throws IOException {
		dataOutputStream.writeBoolean(exists);
	}
	
	public String getFileName() throws IOException {
		return dataInputStream.readUTF();
	}
	
	public void listDirectoryContents(File[] filesDirectories) throws IOException {
		objectOutputStream.writeObject(filesDirectories);
	}
	
	public void pushFile(File file, long fileSize) {
		byte fileDataBuffer[] = new byte[8192];
		int bytesRead;

		try {
			fileInputStream = new FileInputStream(file);
			bufferedFileInputStream = new BufferedInputStream(fileInputStream);

			dataOutputStream.writeLong(fileSize);

			while ((bytesRead = bufferedFileInputStream.read(fileDataBuffer)) > 0)
				outputStream.write(fileDataBuffer, 0, bytesRead);
			outputStream.flush();

			fileInputStream.close();
			bufferedFileInputStream.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public void getFile(File file) throws IOException {		
		long fileSize = 0;
		byte fileDataBuffer[] = new byte[8192];
		int bytesRead = 0;
		long bytesDownloaded = 0;

		try {
			fileSize = dataInputStream.readLong();

			fileOutputStream = new FileOutputStream(file);
			bufferedFileOutputStream = new BufferedOutputStream(fileOutputStream);

			while (bytesDownloaded != fileSize) {
				bytesRead = inputStream.read(fileDataBuffer);
				bufferedFileOutputStream.write(fileDataBuffer, 0, bytesRead);
				bufferedFileOutputStream.flush();

				bytesDownloaded += bytesRead;
			}
			bufferedFileOutputStream.close();
			fileOutputStream.close();
			bufferedFileOutputStream.flush();
		} catch (IOException exception) {
			System.out.println("something went wrong");
		}
		dataOutputStream.writeBoolean(true);
	}
	
	public void exit() {
		try {
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
			if (dataInputStream != null)
				dataInputStream.close();
			if (dataOutputStream != null)
				dataOutputStream.close();
			if (client != null)
				client.close();
		} catch (IOException | NullPointerException exception) {
		}
	}
}
