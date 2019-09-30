/*
Greg Dews
To-Do
    still sifting through provided code. Modyfing to match needs
    need to create a loop to handle buffer issues and array size matching
*/
import java.io.*;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.SecureRandom;

import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPrivateKeySpec;

import java.math.BigInteger;

import javax.crypto.Cipher;


public class Sender {
  public static void main(String[] args) throws Exception {

    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    PublicKey pubKey = readPubKeyFromFile("XPublic.key");
    SecureRandom random = new SecureRandom();
    cipher.init(Cipher.ENCRYPT_MODE, pubKey, random);

    // check size of file
     byte[] cipherBlock = new byte[cipher.getBlockSize()]
    // set size as # of blocks before partial block
    for(int i = 0; i < size; i++){
        cipherBlock = 2;
    }

    // finalize last block
    byte[] cipherText = cipher.doFinal(input);

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

// read input file as array?

  //read key parameters from a file and generate the public key 
  public static PublicKey readPubKeyFromFile(String keyFileName) 
      throws IOException {

    InputStream in = 
        //Sender.class.getResourceAsStream(keyFileName);
        new FileInputStream("./KeyGen/XRSAPublic.key");
    ObjectInputStream oin =
        new ObjectInputStream(new BufferedInputStream(in));

    try {
      BigInteger m = (BigInteger) oin.readObject();
      BigInteger e = (BigInteger) oin.readObject();

      System.out.println("Read from " + keyFileName + ": modulus = " + 
          m.toString() + ", exponent = " + e.toString() + "\n");

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


  //read key parameters from a file and generate the private key 
  public static PrivateKey readPrivKeyFromFile() 
      throws IOException {

    InputStream in = 
        //Sender.class.getResourceAsStream(keyFileName);
        new FileInputStream("./KeyGen/XRSAPrivate.key");
    ObjectInputStream oin =
        new ObjectInputStream(new BufferedInputStream(in));

    try {
      BigInteger m = (BigInteger) oin.readObject();
      BigInteger e = (BigInteger) oin.readObject();

      System.out.println("Read from Xprivate.key: modulus = " + 
          m.toString() + ", exponent = " + e.toString() + "\n");

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


}
