/*
Greg Dews &
Jeff Walto

Sender.java
    Hardcoded to find keys in other (KeyGen) directory
    
***Issues***
    None

***TO-DO***
    Clean the code

*/

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Sender {
    public static void main(String[] args) throws Exception {

        // Get Ciphers/Hasher/keys
        Cipher RSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        Cipher AES = Cipher.getInstance("AES/ECB/PKCS5Padding");
        MessageDigest hasher = MessageDigest.getInstance("SHA-256");
        PrivateKey privKey = readPrivKeyFromFile();
        SecretKey secretKey = readSecretKeyFromFile();
        SecureRandom random = new SecureRandom();
        BufferedInputStream input;

        // initialize RSA/AES
        RSA.init(Cipher.ENCRYPT_MODE, privKey, random);
        AES.init(Cipher.ENCRYPT_MODE, secretKey, random);

        // Get the file
        System.out.println("Input the file path and name of the file we will encrypt.");
        Scanner kb = new Scanner(System.in);
        File M = new File(kb.nextLine());
        InputStream in = new FileInputStream(M);
        input = new BufferedInputStream(in);
        kb.close();

        // Hash the file - 1028 byte increment
        byte[] sha256;
	byte[] temporary = new byte[1024];
        while (input.available() > 1024) {
	    input.read(temporary, 0, 1024);
            hasher.update(temporary);
        }
	int avail = input.available();
	temporary = new byte[avail];
        if (avail > 0) {
	    input.read(temporary, 0, avail);
            hasher.update(temporary);
        }
        input.close();
        in.close();
        sha256 = hasher.digest();
        try (FileOutputStream writer = new FileOutputStream("message.dd")) {
            writer.write(sha256);
        }

        // Display Hash Value
        System.out.println("SHA-256 Value: ");
        for (int i = 0, j = 0; i < sha256.length; i++, j++) {
            System.out.format("%2X ", sha256[i]);
            if (j >= 15) {
                System.out.println("");
                j = -1;
            }
        }
        System.out.println("");

        // RSA the hash value output message.ds-msg
        try (FileInputStream reader = new FileInputStream("message.dd");
            FileOutputStream writer = new FileOutputStream("message.ds-msg")) {
            byte[] temp = new byte[16];
            while (reader.available() > 16) {
                reader.read(temp, 0, 16);
                writer.write(RSA.update(temp));
            }
            // handle last block with exact size array
            if (reader.available() > 0) {
                temp = new byte[reader.available()];
                reader.read(temp);
                writer.write(RSA.doFinal(temp));
            }
        }

        // Display Digital Signature
        System.out.println("Digital Signature: ");
        byte[] cipherHash = new byte[128];
        try (FileInputStream reader = new FileInputStream("message.ds-msg")) {
            reader.read(cipherHash);
        }
        for (int i = 0, j = 0; i < cipherHash.length; i++, j++) {
            System.out.format("%2X ", cipherHash[i]);
            if (j >= 15) {
                System.out.println("");
                j = -1;
            }
        }
        System.out.println("");

        // append message to end of RSA(SHA256(M)) - message.ds-msg
        try (FileInputStream reader = new FileInputStream(M);
                FileOutputStream writer = new FileOutputStream("message.ds-msg", true)) {
            byte[] chunk = new byte[1024];
            while (reader.available() > 0) {
                int size = reader.read(chunk);
                byte[] usefulBytes = new byte[size];
                for(int i = 0; i < size; i++){
                    usefulBytes[i] = chunk[i];
                }
                writer.write(usefulBytes);
            }
        }

        // encrypt DS//M with AES and store in message.aescipher
        try (FileInputStream reader = new FileInputStream("message.ds-msg");
                FileOutputStream writer = new FileOutputStream("..//Receiver//message.aescipher")) {
            byte[] temp = new byte[64];
            while (reader.available() > 64) {
                reader.read(temp);
                temp = AES.update(temp);
                writer.write(temp);
            }
            // handle last block with exact size array
            if (reader.available() > 0) {
                temp = new byte[reader.available()];
                reader.read(temp);
                temp = AES.doFinal(temp);
                writer.write(temp);
            }
        }

    } // end of Sender Class

    // read key parameters from a file and generate the private key
    public static PrivateKey readPrivKeyFromFile() throws IOException {

        InputStream in =
                new FileInputStream("..//KeyGen//XRSAPrivate.key");
                //new FileInputStream("XRSAPrivate.key");
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));

        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();

            System.out.println(
                    "Read from XRSAPrivate.key: modulus = " + m.toString() + ", exponent = " + e.toString() + "\n");

            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey key = factory.generatePrivate(keySpec);

            return key;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    // Reads AESSecret.key, returns a key
    public static SecretKey readSecretKeyFromFile() {
        byte[] storedkey = new byte[16];
         try (FileInputStream reader = new FileInputStream("..//KeyGen//symmetric.key"))
         {
        //try (FileInputStream reader = new FileInputStream("symmetric.key")) {
            reader.read(storedkey);
        } catch (Exception e) {
            throw new RuntimeException("Ya done messed up!", e);
        }
        SecretKey symKey = new SecretKeySpec(storedkey, "AES");
        return symKey;
    }
}
