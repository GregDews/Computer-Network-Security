/*
Greg Dews &
Jeff Walto

***Issues***
    Can't run files in expected folders in my VSCode. "Main not found"
***TO-DO***
    test run, check that everything works.
*/

import java.io.*;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Sender {
    public static void main(String[] args) throws Exception {

        // Get Ciphers/Hasher/keys
        Cipher RSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        Cipher AES = Cipher.getInstance("AES/ECB/PKCS5Padding");
        MessageDigest hasher = MessageDigest.getInstance("SHA-256");
        PublicKey pubKey = readPubKeyFromFile();
        SecretKey secretKey = readSecretKeyFromFile();
        SecureRandom random = new SecureRandom();
        BufferedInputStream input;

        // initialize RSA/AES
        RSA.init(Cipher.ENCRYPT_MODE, pubKey, random);
        AES.init(Cipher.ENCRYPT_MODE, secretKey, random);

        // Get the file
        System.out.println("Input the file path and name of the file we will decrypt.");
        Scanner kb = new Scanner(System.in);
        File M = new File(kb.nextLine());
        InputStream in = new FileInputStream(M);
        input = new BufferedInputStream(in);
        kb.close();

        // decrypt DS//M with AES and store in message.ds-msg
        try (FileInputStream reader = new FileInputStream(M);
                FileOutputStream writer = new FileOutputStream("message.ds-msg")) {
            byte[] temp = new byte[1028];
            while (reader.available() > 1028) {
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
        
        // remove provided digital signature from message.ds-msg
        byte[] sha1 = [0];
        try (FileInputStream reader = new FileInputStream("message.ds-msg");
                FileOutputStream writer = new FileOutputStream("message.msg")) {
            digitalSignature = new byte[64];
            reader.readNBytes(digitalSignature,0,64);
            byte[] temp = new byte[1028];
            while (reader.available() > 0) {
                reader.read(temp);
                writer.write(temp);
            }
        }

        // RSA the Digital Signature
        byte[] providedHash = RSA.doFinal(digitalSignature);
        

        // Display Digital Signature
        System.out.println("Provided Hash Value: ");
        for (int i = 0, j = 0; i < providedHash.length; i++, j++) {
            System.out.format("%2X ", providedHash[i]);
            if (j >= 15) {
                System.out.println("");
                j = -1;
            }
        }
        System.out.println("");

        // Hash the file - 1028 byte increment
        byte[] sha256;
        try (FileInputStream reader = new FileInputStream("message.msg")) {
            byte[] temp = new byte[1028];
            while (reader.available() > 0) {
                reader.read(temp);
                hasher.update(temp);
            }
            if (reader.available() > 0) {
            hasher.update(reader.readAllBytes());
            }
        }
        sha256 = hasher.digest();

        // Display Hash Value
        System.out.println(" Calculated SHA-256 Value: ");
        for (int i = 0, j = 0; i < sha256.length; i++, j++) {
            System.out.format("%2X ", sha256[i]);
            if (j >= 15) {
                System.out.println("");
                j = -1;
            }
        }
        System.out.println("");

        // compare provided hash with calculated hash (sha256)
        if (sha256.equals(providedHash)){
            System.out.println("Signature Verified");
        } else{
            System.out.println("Signature Invalid");
        }


    } // end of Sender Class

    // read key parameters from a file and generate the public key
    public static PublicKey readPubKeyFromFile() throws IOException {

        InputStream in =
                 new FileInputStream("./KeyGen/XRSAPublic.key");
                //new FileInputStream("XRSAPublic.key");
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));

        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();

            System.out.println(
                    "Read from XRSAPublic.key: modulus = " + m.toString() + ", exponent = " + e.toString() + "\n");

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey key = factory.generatePublic(keySpec);

            return key;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    // Reads AESSecret.key, returns a key
    public static SecretKey readSecretKeyFromFile() {
        byte[] storedkey = new byte[128];
        // try (FileInputStream reader = new FileInputStream("./KeyGen/symmetric.key"))
        // {
        try (FileInputStream reader = new FileInputStream("symmetric.key")) {
            reader.read(storedkey);
        } catch (Exception e) {
            throw new RuntimeException("Ya done messed up!", e);
        }
        SecretKey symKey = new SecretKeySpec(storedkey, "AES");
        return symKey;
    }
}
