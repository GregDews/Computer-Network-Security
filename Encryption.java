
/*
 * CS3750-001
 * Homework 2
 * Greg Dews & Larsen Close
 * 
 * Encryption - TEA (Tiny Encryption Algorithm)
 * Input:  Keyboard (kb)
 * Output: Standard out (System.out)
 *          Will have system out dumped into text file via cs3750a server command
 * 
 *      To-Do
 *  encrypt() private method - create routine for encryption
 *  displayResults() private method - create printf statements
*/
import java.util.Scanner;

public class Encryption {

    // Interface Variables
    Scanner kb;

    // Algorithm Constants
    int deltaOne = 0x11111111;
    int deltaTwo = 0x22222222;

    // Display Variables
    int l0, l1, l2, r0, r1, r2;
    int cryptext;

    public Encryption() {
        kb = new Scanner(System.in);
    }

    public void encrypt() {
        System.out.println("Please input K[i] in Hex String (without '0x')");
        int data = pullHexUInt();
        int key = pullHexUInt();

        cryptext = encrypt(data, key);

        displayResults();
        
    }

    // private encryption function data = 64-bit, key = 128-bit
    // returns 0 on error
    private int encrypt(int data, int key){
        int scrambled = 0;
        // Stub - see homework for bitwise meddling

        return scrambled;
    }

    // requests hex string input, returns unsigned int
    private int pullHexUInt() {
        Boolean retry;
        int uint = 0;
        do {

            retry = false;
            try {
                uint = Integer.parseUnsignedInt(kb.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("That was not a valid Hex String \n" + "Please try again.");
                retry = true;
            }

        } while (retry == true);

        return uint;
    } // End of pullHexUInt

    // display formatted results
    private void displayResults(){
        // Stub - See homework requirements for format
    }

    public static void main(String[] args) {
        Encryption javaSucks = new Encryption();
        javaSucks.encrypt();
    }
}
