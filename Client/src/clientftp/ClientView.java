package clientftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ClientView {

	private static final String OS = System.getProperty("os.name");

	public void welcome() {
		System.out.println("     ____.________________________________ ");
		System.out.println("    |    |\\_   _____/\\__    ___/\\______   \\");
		System.out.println("    |    | |    __)    |    |    |     ___/");
		System.out.println("/\\__|    | |     \\     |    |    |    |    ");
		System.out.println("\\________| \\___  /     |____|    |____|    ");
		System.out.println("               \\/                          ");
		System.out.println();
	}

	public void connectionEstablished() {
		System.out.println("Connection established");
	}

	public void connectionFailure() {
		System.out.println("err: a connection could not be established");
	}

	public void commandNotFound(String command) {
		System.out.println("err: '" + command + "' command not found");
	}

	public void missingFileOperand() {
		System.out.println("err: missing file operand");
	}

	public void fileDoesNotExist(String fileName) {
		System.out.println("err: cannot get '" + fileName + "': no such file");
	}

	public void directoryDoesNotExist(String directoryName) {
		System.out.println("err: cd: '" + directoryName + "': no such directory");
	}

	public void listDirectoryContents(File filesDirectories[]) {
		if (OS.contains("Windows")) {
			for (File fileDirectory : filesDirectories) {
				if (!fileDirectory.isHidden()) {
					if (fileDirectory.isDirectory())
						System.out.println("<DIR>\t" + fileDirectory.getName());
					else
						System.out.println("\t" + fileDirectory.getName());
				}
			}
		} else if (OS.contains("Linux") || OS.contains("Mac")) {
			for (File fileDirectory : filesDirectories) {
				if (!fileDirectory.isHidden()) {
					if (fileDirectory.isDirectory())
						System.out.println("\u001b[34;1m" + fileDirectory.getName() + "\u001B[0m");
						//System.out.println(fileDirectory.getName());
					else
						System.out.println(fileDirectory.getName());
				}
			}
		}
	}

	public void transferProgress(double percentDownloaded, boolean download) {
		if (download)
			System.out.printf("\r" + "Downloading...%.0f%%", percentDownloaded);
		else
			System.out.printf("\r" + "Uploading...%.0f%%", percentDownloaded);
	}

	public void remoteWorkingDirectory(String remoteWorkingDirectory) {
		System.out.println(remoteWorkingDirectory);
	}

	public void fileTransferDone() {
		System.out.println("Done");
	}

	public void missingHostname() {
		System.out.println("err: missing hostname");
	}

	public String[] getCommandLine(String currentDirectory, BufferedReader bufferedReader) throws IOException {
		System.out.print(currentDirectory + " > ");
		return bufferedReader.readLine().split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
	}

}