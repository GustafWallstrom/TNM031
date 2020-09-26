import java.util.*;
import java.math.*;
import java.security.SecureRandom;

/**
 *  This is a lab in the course TNM031 at Linköping University
 *  The following program encrypt and decrypt a message with the RSA algorithm.
 *  Joel Paulsson - joepa811
 *  Gustaf Wallström - gusan112
 */

class Lab2{

    //Function to generate a big number greater than 2^32 to secure e < nPhi.
    private static BigInteger bigPrime() {
        BigInteger minLimit = BigInteger.TWO.pow(32);
        BigInteger number = BigInteger.ZERO;

        while(number.compareTo(minLimit) < 0){
            number = BigInteger.probablePrime(512, new SecureRandom());
        };
        System.out.println("Number okay");
        return number;
    }

    public static void main(String args[]) {
        
        // Variables
        BigInteger p, q, n, e, d, nPhi;

        p = bigPrime();
        q = bigPrime();

        // RSA - algorithm
        /*************** Step 1 ************/
        n = p.multiply(q);
        nPhi = (p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1"))));
        
 
        /*************** Step 2 ************/
        // Public key exponent
        e = BigInteger.probablePrime(32, new SecureRandom());

        System.out.println("e: "+ e);
        
        /*************** Step 3 ************/
        // Get private key exponent d such that (ed % (p-1)(q-1) = 1)
        d = e.modInverse(nPhi);

        System.out.println("\nWe first choose two large random prime numbers.");
        System.out.println("Secret keys:\n p: " + p + ", q: " + q + "\n");
        System.out.println("Combine them to get n, which is public");
        System.out.println("n = p*q = " + n);
        
        System.out.println("Enter a integer to encrypt & decrypt:");
        Scanner obj = new Scanner(System.in);
        BigInteger msg = new BigInteger(obj.nextLine());

        /*************** Step 4 ************/

        //Make variables n & e public, keep p,q & d secret

        /*************** Step 5 ************/
        // Encrypted message: c=m^e (mod n)
        BigInteger c = msg.modPow(e,n);
        System.out.println("Encrypted message is: " + c + "\n");

        /*************** Step 6 ************/
        // Decrypt the message: m = c^d (mod n)
        BigInteger m = c.modPow(d, n);
        String message = m.toString();

        System.out.println("Decrypted message: " + message);

        obj.close();
    }
}