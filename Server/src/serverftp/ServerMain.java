package serverftp;

import java.io.IOException;

public class ServerMain {
	public static void main(String args[]) {
		try {
			new ServerController(new ServerModel());
		} catch (IOException exception) {}
	}
}