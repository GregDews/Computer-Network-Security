import java.io.*;
import java.util.*;
import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.math.BigInteger;
import javax.crypto.Cipher;

public class KeyGeneration {
    public static void main(String[] args) throws Exception {
  
        //Generate a pair of keys for x
        SecureRandom randomX = new SecureRandom();
        KeyPairGenerator generatorX = KeyPairGenerator.getInstance("RSA");
        generatorX.initialize(1024, randomX);  //1024: key size in bits
        KeyPair pairX = generatorX.generateKeyPair();
        Key xPublicKey = pairX.getPublic();
        Key xPrivateKey = pairX.getPrivate();

        //get the parameters of the X keys: modulus and exponet
        KeyFactory factoryX = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec xPubKSpec = factoryX.getKeySpec(xPublicKey, 
            RSAPublicKeySpec.class);
        RSAPrivateKeySpec xPrivKSpec = factoryX.getKeySpec(xPrivateKey, 
            RSAPrivateKeySpec.class);

        //save the parameters of the X keys to the files
        saveToFile("XRSAPublic.key", xPubKSpec.getModulus(), 
            xPubKSpec.getPublicExponent());
        saveToFile("XRSAPrivate.key", xPrivKSpec.getModulus(), 
            xPrivKSpec.getPrivateExponent());

        //Generate a pair of keys for y
        SecureRandom randomY = new SecureRandom();
        KeyPairGenerator generatorY = KeyPairGenerator.getInstance("RSA");
        generatorY.initialize(1024, randomY);  //1024: key size in bits
        KeyPair pairY = generatorY.generateKeyPair();
        Key yPublicKey = pairY.getPublic();
        Key yPrivateKey = pairY.getPrivate();
        
        //get the parameters of the Y keys: modulus and exponet
        KeyFactory factoryY = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec yPubKSpec = factoryY.getKeySpec(yPublicKey, 
            RSAPublicKeySpec.class);
        RSAPrivateKeySpec yPrivKSpec = factoryY.getKeySpec(yPrivateKey, 
            RSAPrivateKeySpec.class);

        //save the parameters of the Y keys to the files
        saveToFile("YRSAPublic.key", yPubKSpec.getModulus(), 
            yPubKSpec.getPublicExponent());
        saveToFile("YRSAPrivate.key", yPrivKSpec.getModulus(), 
            yPrivKSpec.getPrivateExponent());

        //symmetric key creation
        Scanner input = new Scanner(System.in);
        String symmetricKey = "abcd";
        while (symmetricKey.length() != 16) {
            System.out.print("Please enter a 16 character string. ");
            symmetricKey = input.nextLine();
        }
        System.out.print(symmetricKey);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("symmetric.key")))) {
            writer.write(symmetricKey);
        }
        input.close();
        
    }

    //save the prameters of the public and private keys to file
    public static void saveToFile(String fileName,
        BigInteger mod, BigInteger exp) throws IOException {

        System.out.println("Write to " + fileName + ": modulus = " + 
        mod.toString() + ", exponent = " + exp.toString() + "\n");

        ObjectOutputStream oout = new ObjectOutputStream(
        new BufferedOutputStream(new FileOutputStream(fileName)));

        try {
        oout.writeObject(mod);
        oout.writeObject(exp);
        } catch (Exception e) {
        throw new IOException("Unexpected error", e);
        } finally {
        oout.close();
        }
    }
}