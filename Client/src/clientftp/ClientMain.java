package clientftp;

public class ClientMain {
	public static void main(String args[]) {
		ClientView clientSideFTPView = new ClientView();
		
		try {
			ClientModel clientSideFTPModel = new ClientModel(args[0]);
			clientSideFTPView.welcome();

			Client client = new Client(clientSideFTPView, clientSideFTPModel);
			client.start();
		} catch (ArrayIndexOutOfBoundsException e) {
			clientSideFTPView.missingHostname();
		}

	}
}