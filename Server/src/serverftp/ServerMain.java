package serverftp;

import java.io.IOException;

public class ServerMain {
	public static void main(String args[]) {
		try {
			Server server = new Server(new ServerModel());
			server.start();
		} catch (IOException exception) {}
	}
}