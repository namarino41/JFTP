package serversideftp;

import java.io.IOException;

public class ServerSideFTPMain {
	public static void main(String args[]) {
		try {
			new ServerSideFTPController(new ServerSideFTPModel());
		} catch (IOException exception) {}
	}
}