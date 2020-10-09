// A client-side class that uses a secure TCP/IP socket

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.Scanner;
import javax.net.ssl.*;

public class SecureAdditionClient {
	private InetAddress host;
	private int port;
	// This is not a reserved port number 
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "client/LIUkeystore.ks";
	static final String TRUSTSTORE = "client/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";
  
	
	// Constructor @param host Internet address of the host where the server is located
	// @param port Port number on the host where the server is listening
	public SecureAdditionClient( InetAddress host, int port ) {
		this.host = host;
		this.port = port;
	}
	
  // The method used to start a client object
	public void run() {
		try {
			KeyStore ks = KeyStore.getInstance( "JCEKS" );
			ks.load( new FileInputStream( KEYSTORE ), KEYSTOREPASS.toCharArray() );
			
			KeyStore ts = KeyStore.getInstance( "JCEKS" );
			ts.load( new FileInputStream( TRUSTSTORE ), TRUSTSTOREPASS.toCharArray() );
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, KEYSTOREPASS.toCharArray() );
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ts );
			
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
			SSLSocketFactory sslFact = sslContext.getSocketFactory();      	
			SSLSocket client =  (SSLSocket)sslFact.createSocket(host, port);
			client.setEnabledCipherSuites( client.getSupportedCipherSuites() );
			System.out.println("\n>>>> SSL/TLS handshake completed");

			
			BufferedReader socketFromServer;
			socketFromServer= new BufferedReader( new InputStreamReader( client.getInputStream() ) );
			PrintWriter socketToServer = new PrintWriter( client.getOutputStream(), true );

			System.out.println( "\n*-----* Lab 3: SSL *-----*");
			System.out.println("Option 1: Upload file ");
			System.out.println("Option 2: Download file");
			System.out.println("Option 3: Delete file");
			System.out.println( "*------------------------*\n\n");

			System.out.print("Choose wisely: ");
			Scanner scan = new Scanner(System.in);
			String option = scan.nextLine();	
			String fileOption = "";

			socketToServer.println(option);

			switch(option){

				case "1": // Upload file
				
					System.out.println("Option 1: Upload file.");
					System.out.println("Which file do you want to upload? ");
					
					File clientF = new File("D:/Skola/Natverksprogrammeringochsäkerhet(TNM031)/Labs-and-assignments/3/SecureAdditionServer/Client/Files");
					listFiles(clientF);

					System.out.print("Type the file you want to upload: ");
					fileOption = scan.nextLine();
					socketToServer.println(fileOption);

					// Upload to server:
					try (BufferedReader reader = new BufferedReader(
						new FileReader(clientF + "/" + fileOption))) {
						String line; 
						while ((line = reader.readLine()) != null) { 
							System.out.println(line);
							socketToServer.println(line); // send one line at the time to server
						}
						reader.close();
						socketToServer.close();
					} catch (IOException ioe) {
						System.out.println("Error is: " + ioe);
						ioe.printStackTrace();
					}
					
					break;

				case "2": // Download file
				
					System.out.println("Option 2: Download file");
					System.out.println("Choose which file to download: ");

					File serverF = new File("D:/Skola/Natverksprogrammeringochsäkerhet(TNM031)/Labs-and-assignments/3/SecureAdditionServer/Server/Files");
					listFiles(serverF);

					fileOption = scan.nextLine();
					socketToServer.println(fileOption);

					// Download file: 
					try{
						String fileName = socketFromServer.readLine();
						File newFile = new File( "./client/Files/" + fileName);
						System.out.println("Downloading file: " + fileName);
						newFile.createNewFile(); 

						FileWriter writer = new FileWriter(newFile);
						String line;
						
						while ((line = socketFromServer.readLine()) != null) { 

							writer.write(line + "\n");
						}
						writer.close(); 
		
					}catch (IOException e) {
						System.out.println("An error occurred.");
						e.printStackTrace();
					}

					socketFromServer.close();
					
					break;
				case "3": // Delete file

					System.out.println("Option 2: Delete file");
					System.out.println("Choose which file to delete from the server: ");

					File deleteF = new File("D:/Skola/Natverksprogrammeringochsäkerhet(TNM031)/Labs-and-assignments/3/SecureAdditionServer/Server/Files");
					listFiles(deleteF);

					fileOption = scan.nextLine();
					socketToServer.println(fileOption);
				
					
					System.out.println("Server response: " + socketFromServer.readLine()); 
					socketFromServer.close();
					socketToServer.close();

					break; 

				default: 
					System.out.println("Wrong option, try agin!");
					break;

			}

			scan.close();

		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}


	private static void listFiles(File files ){

		String fileNames[];
		fileNames = files.list();
		System.out.println("----------");
		System.out.println("");
				
		for (String serverFileName : fileNames)
					System.out.println(serverFileName);

		System.out.println("");	
		System.out.println("----------");
		System.out.println("");	
	}
	
	
	// The test method for the class @param args Optional port number and host name
	public static void main( String[] args ) {
		try {
			InetAddress host = InetAddress.getLocalHost();
			int port = DEFAULT_PORT;
			if ( args.length > 0 ) {
				port = Integer.parseInt( args[0] );
			}
			if ( args.length > 1 ) {
				host = InetAddress.getByName( args[1] );
			}
			SecureAdditionClient addClient = new SecureAdditionClient( host, port );
			addClient.run();
		}
		catch ( UnknownHostException uhx ) {
			System.out.println( uhx );
			uhx.printStackTrace();
		}
	}
}
