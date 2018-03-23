package serverftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final int HOST_PORT = 6000;
	
	private ServerSocket serverSocket;
	private ServerModel serverModel;
	
	public Server(ServerModel serverModel) throws IOException {
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
			Socket client = serverSocket.accept();
			Thread worker = new Thread(new ServerWorker(client, serverModel));
			worker.start();
		}
	}
}
