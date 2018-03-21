package clientsideftp;

public class ClientSideFTPMain {
	public static void main(String args[]) {
		try {
			ClientSideFTPView clientSideFTPView = new ClientSideFTPView();
			ClientSideFTPModel clientSideFTPModel = new ClientSideFTPModel(args[0]);
			clientSideFTPView.welcome();
			new ClientSideFTPController(clientSideFTPView, clientSideFTPModel);
		} catch (ArrayIndexOutOfBoundsException exception) {
			System.out.println("err: missing server inet address");
		}
	}
}