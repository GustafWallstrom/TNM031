// An example class that uses the secure server socket class

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.util.StringTokenizer;



public class SecureAdditionServer {
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "server/LIUkeystore.ks";//"LIUkeystore.ks"; 
	static final String TRUSTSTORE = "server/LIUtruststore.ks";//"LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";
	
	
	/** Constructor
	 * @param port The port where the server
	 *    will listen for requests
	 */
	SecureAdditionServer( int port ) {
		this.port = port;
	}
	
	/** The method that does the work for the class */
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
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket( port );
			sss.setEnabledCipherSuites( sss.getSupportedCipherSuites() );
			
			System.out.println("\n>>>> SecureAdditionServer: active ");
			SSLSocket incoming = (SSLSocket)sss.accept();

      		BufferedReader socketFromClient = new BufferedReader( new InputStreamReader( incoming.getInputStream() ) );
			PrintWriter socketToClient = new PrintWriter( incoming.getOutputStream(), true );			
		
			String option = socketFromClient.readLine();
			String line;

			switch(option){
				case "1": // Upload file to server
					System.out.println("Option 1, uploading file to server...");

					try{
						File newFile = new File( "./server/Files/" + socketFromClient.readLine());
						System.out.println("Filename: " + newFile);
						newFile.createNewFile(); 

						FileWriter writer = new FileWriter(newFile);
						
						while ((line = socketFromClient.readLine()) != null) { 

							writer.write(line + "\n");
						}
						writer.close();
		
					}catch (IOException e) {
						System.out.println("An error occurred.");
						e.printStackTrace();
					}
		
					break;
				case "2": // Download file from server
					try {
						String fileOption = socketFromClient.readLine();	
						BufferedReader reader = new BufferedReader(new FileReader( "./server/Files/" + fileOption)); 

						socketToClient.println(fileOption);
						
						while ((line = reader.readLine()) != null) { 
							System.out.println(line);
							socketToClient.println(line); // send one line at the time to client
						}
						reader.close();
						socketToClient.close();

					} catch (IOException ioe) {
						System.out.println("Error is: " + ioe);
						ioe.printStackTrace();
					}

					break;
				case "3": // Delete file from server

					String fileDeleteOption = socketFromClient.readLine();	
					try{
						File file = new File( "./server/Files/" + fileDeleteOption);

						if(file.delete()) socketToClient.println("The file: " + fileDeleteOption + " is deleted."); 
						else socketToClient.println("Deletion failed");

					} catch (Exception ioe) {
						System.out.println("Error is: " + ioe);
						ioe.printStackTrace();
					}

					break;
				default:
					System.out.println("Error");
					break;
			}

			incoming.close();

		}catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}
	
	
	/** The test method for the class
	 * @param args[0] Optional port number in place of
	 *        the default
	 */
	public static void main( String[] args ) {
		int port = DEFAULT_PORT;
		if (args.length > 0 ) {
			port = Integer.parseInt( args[0] );
		}
		SecureAdditionServer addServe = new SecureAdditionServer( port );
		addServe.run();
	}
}

