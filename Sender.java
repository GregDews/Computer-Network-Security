
/*
Greg Dews
To-Do
    still sifting through provided code. Modyfing to match needs
    need to create a loop to handle buffer issues and array size matching

sender 
    enKx-(SHA256(M))
    append message to that
    enkxy

*/
import java.io.*;
import java.util.Scanner;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.math.BigInteger;
import javax.crypto.Cipher;

public class Sender {
  public static void main(String[] args) throws Exception {

    // Get Ciphers/Hasher/keys
    Cipher RSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    Cipher AES = Cipher.getInstance("AES/ECB/PKCS1Padding");
    MessageDigest hasher = MessageDigest.getInstance("SHA-256");
    PrivateKey privKey = readPrivKeyFromFile();
    //Key secretKey = readSecretKeyFromFile();
    SecureRandom random = new SecureRandom();
    BufferedInputStream input;

    // initialize RSA
    RSA.init(Cipher.ENCRYPT_MODE, privKey, random);
    //AES.init(Cipher.ENCRYPT_MODE, secretKey, random);

    // Get the file
    System.out.println("Input the file path and name of the file we will encrypt.");
    Scanner kb = new Scanner(System.in);
    InputStream in = new FileInputStream(kb.nextLine());
    input = new BufferedInputStream(in);
    kb.close();

    // Hash the file - 1028 byte increment
    byte[] sha256;

    while(input.available() > 1028){
        hasher.update(input.readNBytes(1028));
    }
    if(input.available() > 0){
        hasher.update(input.readAllBytes());
    }
    sha256 = hasher.digest();

    // Display Hash Value
    System.out.println("SHA-256 Value: ");
    for (int i=0, j=0; i< sha256.length; i++, j++) {
      System.out.format("%2X ", sha256[i]) ;
      if (j >= 15) {
        System.out.println("");
        j=-1;
      }
    }
    System.out.println("");

    // append message to end of sha256 hash
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("symmetric.key")))) {
            writer.write("filler");
        }

    /* V V V V V V V V V V V V V V TO-DO V V V V V V V V V V V V V V V V V
    int block_size = RSA.getBlockSize();
     byte[] cipherBlock = new byte[block_size];
   

    // finalize last block
    byte[] cipherText = RSA.doFinal(input);

    // display ciphertext byte info
    System.out.println("cipherText: block size = " + cipher.getBlockSize());
    for (int i=0, j=0; i<cipherText.length; i++, j++) {
      System.out.format("%2X ", cipherText[i]) ;
      if (j >= 15) {
        System.out.println("");
        j=-1;
      }
    }
    System.out.println("");
  }

    // read input file - call

    // read key parameters from a file and generate the public key
    public static PublicKey readPubKeyFromFile() throws IOException {

        InputStream in =
                // Sender.class.getResourceAsStream(keyFileName);
                new FileInputStream("./KeyGen/XRSAPublic.key");
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
*/
input.close();
in.close();
}
    // read key parameters from a file and generate the private key
    public static PrivateKey readPrivKeyFromFile() throws IOException {

        InputStream in =
                // Sender.class.getResourceAsStream(keyFileName);
                new FileInputStream("./KeyGen/XRSAPrivate.key");
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
/*
    public static void readSecretKeyFromFile(){
        InputStream in =
                new FileInputStream("./KeyGen/AESSecret.key");
        try {
            byte[] privateKey = in.readAllBytes();

            System.out.println(
                    "Read from AESSecret.key: " + privateKey.toString() + "\n");

            KeySpec keySpec = new KeySpec();
            KeyFactory factory = KeyFactory.getInstance("AES");
            Key key = factory.;

            return key;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }
*/
}
