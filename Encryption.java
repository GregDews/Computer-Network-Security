
/*
 * CS3750-001
 * Homework 2
 * Greg Dews & Larsen Close
 * 
 * Encryption - TEA (Tiny Encryption Algorithm)
 * Input:  Keyboard (kb)
 * Output: Standard out (print)
 *          Will have system out dumped into text file via cs3750a server command
 * 
 *      To-Do
 *  Test algorithm
 *  displayResults() private method - create printf statements
*/
import java.util.Scanner;

public class Encryption {

    // Interface Variables
    Scanner kb;

    // Algorithm Constants
    int delta[] = new int[3];

    // Encryption Variables
    int[] L = new int[3], 
          R = new int[3],
          K = new int[4];

    public Encryption() {
        kb = new Scanner(System.in);
    }

    public void encrypt() {
        pullHexUInt(K, "K", 4);
        pullHexUInt(L,"L", 1);
        pullHexUInt(R,"R", 1);
        encryptTEA();
        displayResults();
    }

    // private encryption function data = 64-bit, key = 128-bit
    private void encryptTEA(){
        for(int pass = 1; pass <= 2; pass++){
            L[pass] = R[pass-1];
            R[pass] = L[pass] + bigF(R[pass-1], K[pass-1], K[pass], delta[pass]); 
        }
    }

    // subroutine function of encryption
    private int bigF(int r, int k1, int k2, int d){
        int result = 0;
        result = ((r<<4) + k1) ^ ((r>>5) + k2) ^ (r + d);
        return result;
    }

    // requests hex string input, load int array with provided values
    private void pullHexUInt(int[] arr, String letter, int length) {
        Boolean retry; 
        for(int i = 0; i < length; i++){
        System.out.println("Please input "+ letter +
                            "["+ i +"] in Hex String (without '0x')");
        do {
            retry = false;
            try {
                arr[i] = Integer.parseUnsignedInt(kb.nextLine(),16);
            } catch (NumberFormatException e) {
                System.out.println("That was not a valid Hex String \n" + "Please try again.");
                retry = true;
            }

        } while (retry == true);
        }
    } // End of pullHexUInt

    // display formatted results
    private void displayResults(){
        for (int i = 0; i < 3; i++) {
            System.out.printf("L[%d] = %s    R[%d] = %s %n",
             i, Integer.toHexString(L[i]), i, Integer.toHexString(R[i]));
        }
    }

    public static void main(String[] args) {
        Encryption javaSucks = new Encryption();
        javaSucks.encrypt();
    }
}
