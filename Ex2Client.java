/**
 * @author Michael Acosta
 * CS 380
 * Exercise 2
 * A client to receive 100 bytes from codebank.xyz (port 38102), generate a corresponding CRC32 error
 * code, return the code to the server, and validate the response
 *
 */

import java.io.*;
import java.net.*;
import java.util.zip.CRC32;
import javax.xml.bind.DatatypeConverter;

public class Ex2Client {

	/**
	 * Contains the client in it's entirety
	 */
	public static void main(String[] args) {
		try {
			Socket ex2Client = new Socket("codebank.xyz", 38102);
			System.out.println("Connected to server.");
			
			// Create an input stream to read half of a byte from the server at a time
			DataInputStream inputStream = new DataInputStream(ex2Client.getInputStream());
			// Bytes will be stored in an array
			byte[] message = new byte[100];
			byte upperHalf, lowerHalf;
			// Read in a pair of bytes, upper and lower half, concatenate, and store in the array
			for (int i = 0; i < 100; i++) {
				upperHalf = inputStream.readByte();
				lowerHalf = inputStream.readByte();
				message[i] = (byte) ((upperHalf << 4) | lowerHalf);
			}
			// Convert bytes to the equivalent hex representation before printing
			System.out.println("Received bytes:\n\t" + DatatypeConverter.printHexBinary(message));
			
			// Generate the error code from the byte array
			CRC32 errorCode = new CRC32();
			errorCode.update(message);
			System.out.println("Generated CRC32: " + Long.toHexString(errorCode.getValue()));
			
			// Create an output stream to send the error code
			DataOutputStream outputStream = new DataOutputStream(ex2Client.getOutputStream());
			// writeInt send the four bytes individually
			outputStream.writeInt((int)errorCode.getValue());
			
			// Obtain a response from the server, and display an appropriate message
			byte response = inputStream.readByte();
			if (response == 1) { // Server constructed the same code
				System.out.println("Response good.");
			}
			else if (response == 0) { // Server constructed a different code
				System.out.println("Response bad.");
			}
			
			// Close all streams and the socket
			inputStream.close();
			outputStream.close();
			ex2Client.close();
			System.out.println("Disconnected from server.");
		}
		catch (IOException e){
			System.out.println(e);
		}
	}

}
