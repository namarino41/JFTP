package clientftp;

public class ClientMain {
	public static void main(String args[]) {
		ClientView clientSideFTPView = new ClientView();
		
		try {
			ClientModel clientSideFTPModel = new ClientModel(args[0]);
			clientSideFTPView.welcome();

			new ClientController(clientSideFTPView, clientSideFTPModel);
		} catch (ArrayIndexOutOfBoundsException e) {
			clientSideFTPView.missingHostname();
		}

	}
}