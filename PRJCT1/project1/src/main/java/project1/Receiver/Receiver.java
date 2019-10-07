package project1.Receiver;
/*
Greg Dews &
Jeff Walto

***Issues***
    not separating message from message.ds-msg correctly
    Example: calculated has is incorrect, provided hash is correct
***TO-DO***
    test run, check that everything works.
*/

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Receiver {
    public static void main(String[] args) throws Exception {

        // Get Ciphers/Hasher/keys
        Cipher RSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        Cipher AES = Cipher.getInstance("AES/ECB/PKCS5Padding");
        MessageDigest hasher = MessageDigest.getInstance("SHA-256");
        PublicKey pubKey = readPubKeyFromFile();
        SecretKey secretKey = readSecretKeyFromFile();

        // initialize RSA/AES
        RSA.init(Cipher.DECRYPT_MODE, pubKey);
        AES.init(Cipher.DECRYPT_MODE, secretKey);

        // Get the file
        System.out.println("Input the file path and name of the file we will decrypt.");
        Scanner kb = new Scanner(System.in);
        File M = new File(kb.nextLine());
        kb.close();

        // decrypt DS//M with AES and store in message.ds-msg
        try (FileInputStream reader = new FileInputStream(M);
                FileOutputStream writer = new FileOutputStream("newmessage.ds-msg")) {
            byte[] temp = new byte[64];
            while (reader.available() > 64) {
                temp = reader.readNBytes(64);
                writer.write(AES.update(temp));
            }
            // handle last block with exact size array
            if (reader.available() > 0) {
                temp = new byte[reader.available()];
                reader.read(temp);
                writer.write(AES.doFinal(temp));
            }
        }

        // remove provided digital signature from message.ds-msg
        byte[] digitalSignature = new byte[128];
        byte[] mbuf = new byte[1024];
        try (FileInputStream reader = new FileInputStream("newmessage.ds-msg");
                FileOutputStream writer = new FileOutputStream("newmessage.msg")) {
            reader.readNBytes(digitalSignature, 0, 128);
            while (reader.available() > 1024) {
                mbuf = reader.readNBytes(1024);
                hasher.update(mbuf);
                writer.write(mbuf);
            }
            int last = reader.available();
            if ( last > 0) {
                byte[] mbuf2 = reader.readAllBytes();
                hasher.update(mbuf2);
                writer.write(mbuf2);
            }
        }
        try (FileOutputStream writer = new FileOutputStream("newmessage.ds")) {
            writer.write(digitalSignature);
        }

        // RSA the Digital Signature
        try (FileInputStream reader = new FileInputStream("newmessage.ds");
                FileOutputStream writer = new FileOutputStream("newmessage.dd")) {
            byte[] temp = new byte[16];
            while (reader.available() > 16) {
                temp = reader.readNBytes(16);
                writer.write(RSA.update(temp));
            }
            // handle last block with exact size array
            if (reader.available() > 0) {
                temp = new byte[reader.available()];
                reader.read(temp);
                writer.write(RSA.doFinal(temp));
            }
        }
        // Display Digital Digest
        System.out.println("Provided Value: ");
        byte[] providedHash = new byte[32];
        try (FileInputStream reader = new FileInputStream("newmessage.dd")) {
            reader.read(providedHash);
        }
        for (int i = 0, j = 0; i < providedHash.length; i++, j++) {
            System.out.format("%2X ", providedHash[i]);
            if (j >= 15) {
                System.out.println("");
                j = -1;
            }
        }
        System.out.println("");

        // Hash the file - 1024 byte increment
        byte[] sha256;
        try (FileInputStream reader = new FileInputStream("newmessage.msg")) {
            while (reader.available() > 1024) {
                hasher.update(reader.readNBytes(1024));
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
        if (sha256.equals(providedHash)) {
            System.out.println("Signature Verified");
        } else {
            System.out.println("Signature Invalid");
        }

    } // end of Receiver Class

    // read key parameters from a file and generate the public key
    public static PublicKey readPubKeyFromFile() throws IOException {

        InputStream in =
                // new FileInputStream("./KeyGen/XRSAPublic.key");
                new FileInputStream("XRSAPublic.key");
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
        byte[] storedkey = new byte[16];
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
