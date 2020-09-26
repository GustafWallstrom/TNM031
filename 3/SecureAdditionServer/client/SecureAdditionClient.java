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
			
			// String numbers = "1.2 3.4 5.6";
			// System.out.println( ">>>> Sending the numbers " + numbers+ " to SecureAdditionServer " );
			// socketOut.println( numbers );
			// System.out.println( socketIn.readLine() );

			System.out.println( "\n*-----* Lab 3: SSL *-----*");
			System.out.println("Option 1: Upload file ");
			System.out.println("Option 2: Download file");
			System.out.println("Option 3: Delete file");
			System.out.println( "*------------------------*\n\n");

			System.out.print("Choose wisely: ");
			Scanner scan = new Scanner(System.in);
			String option = scan.nextLine();	
			//scan.close();

			socketToServer.println(option);

			switch(option){

				case "1": // Upload file
				
				System.out.println("Option 1: Upload file.");
				System.out.println("Which file do you want to upload? ");

				String[] clientFileNames;

				File clientF = new File("D:/Skola/Natverksprogrammeringochsäkerhet(TNM031)/Labs-and-assignments/3/SecureAdditionServer/Client/Files");
				clientFileNames = clientF.list();

				System.out.println("----------");
				System.out.println("");
				
				for (String clientFileName : clientFileNames)
					System.out.println(clientFileName);

				System.out.println("");	
				System.out.println("----------");
				System.out.println("");	

				System.out.print("Type the file you want to upload: ");
				//Scanner fileScan = new Scanner(System.in);
				String fileOption = scan.nextLine();
				scan.close();

				//String fileOption = "Gustafs.txt";
				
				try (BufferedReader reader = new BufferedReader(
                    new FileReader(clientF + "/" + fileOption))) {
                String line; 
                while ((line = reader.readLine()) != null) { 
					System.out.println(line);
					socketToServer.println(line); // send one line at the time to server
                }
				} catch (IOException ioe) {
					System.out.println(ioe);
					ioe.printStackTrace();
				}
				
					break;

				case "2": // Download file
					System.out.println("Option 2: Download file");

					System.out.println("Choose which file to download: ");

					String[] serverFileNames;

					File serverF = new File("D:/Skola/Natverksprogrammeringochsäkerhet(TNM031)/Labs-and-assignments/3/SecureAdditionServer/Server/Files");
					serverFileNames = serverF.list();

					System.out.println("----------");
					System.out.println("");
					
					for (String serverFileName : serverFileNames)
						System.out.println(serverFileName);

					System.out.println("");	
					System.out.println("----------");
					System.out.println("");	



					break;
				case "3": // Delete file
					
					break; 

				default: 
					System.out.println("Wrong option, try agin!");
					break;

			}




		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
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
