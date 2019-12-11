import java.math.BigDecimal;
import java.util.Arrays;

/**
 * The VigenereDecipher is a program that accepts a ciphertext with a given
 * key length to perform cryptanalysis in order to find a key and break the
 * ciphertext while providing the decoded plaintext.
 *
 * @author Emil Ivanov
 * @version 1.0
 */
public class VigenereDecipher {

    static String encodedMessage;

    static int keyLen;

    final static double frequencies[] = {
      0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015,
      0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749,
      0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758,
      0.00978, 0.02360, 0.00150, 0.01974, 0.00074
    };

    public static void main(String[] args) {
        encodedMessage = args[0];
        keyLen = Integer.parseInt(args[1]);

        char[] cipherArray = new char[encodedMessage.length()];
        char[] keyArray = new char[keyLen];

        //convert string to char array (per character element)
        encodedMessage.getChars(0, encodedMessage.length(), cipherArray, 0);

        int[] alphabeticalCipher = mapCipherToAlphabetArray(cipherArray);
        doFrequencyAnalysis(alphabeticalCipher, keyLen, keyArray);

        System.out.println("Key found: " + charArrayToString(keyArray)
          + " using length - " + keyLen);
        System.out.println("Possible plaintext solution: "
          + decrypt(encodedMessage, charArrayToString(keyArray)));
    }

    /**
     * Simple decryption using the previously found key.
     * @param cipher to decrypt
     * @param key to use to decrypt
     * @return plaintext
    */
    private static String decrypt(String cipher, final String key) {
        StringBuilder res = new StringBuilder();
        for (int i = 0, j = 0; i < cipher.length(); i++) {
            char c = cipher.charAt(i);
            if (c < 'A' || c > 'Z') continue;
            res.append((char) ((c - key.charAt(j) + 26) % 26 + 'A'));
            j = ++j % key.length();
        }
        return res.toString();
    }

    /**
     * This function performs cryptanalysis on a cipher using the key length.
     * It does this my splitting the ciphertext into groups of length - key
     * length. Then, it performs frequency analysis on each individual letter
     * from a group, finding the best frequency match using the best possible
     * shift while later matching it to the english alphabet letter frequency
     * through basic mathematics.
     *
     *
     * @param cipherText the ciphertext we would like to decrypt
     * @param keyLen length of the key used to decrypt the ciphertext
     * @param key key array where we store the generate key
    */
    private static void doFrequencyAnalysis(final int[] cipherText, int keyLen, char[] key) {
        double sum, d;
        double  [] shiftedCipherFrequencies = new double [26];
        double  [] cipherFrequencies = new double [26];
        int i, j, shift;

        //perform frequency cryptanalysis for every index of the key
        for (j = 0; j < keyLen; j++) {

            //clear array for next shift
            Arrays.fill(cipherFrequencies, 0);

            for(int s = 0; s < cipherFrequencies.length; s++){
                cipherFrequencies[s] = 0;
            }

            //insert frequency of letters into index correlating the frequencies array
            for (i = j; i < cipherText.length; i += keyLen)
                cipherFrequencies[cipherText[i]]++;

            shift = findBestShift(cipherFrequencies);

            try{
                key[j] = (char)(shift + 'A');
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
            shiftedCipherFrequencies = leftShiftArray(cipherFrequencies, shift);
        }
        sum = sumOfArray(shiftedCipherFrequencies);

        for (i = 0; i < 26; i++) {
            d = shiftedCipherFrequencies[i] / sum - frequencies[i];
        }

    }

    /**
     * This function will find the best possible shift for an existing array of
     * cipher frequencies. It will do these calculations by dividing the
     * frequency of the cipher letter in the group by the sum of the total
     * number of letters occurring in the group. The result is multiplied by
     * the english alphabetical frequency of that letter. This process is
     * repeated 26 times to find the best possible shift which is then returned.
     *
     * @param cipherFrequency array containing letter frequencies of the cipher
     *                        mapped to the index correlating to the alphabet
     *                        frequency array so that they match later on in
     *                        arithmetic operations
     *
     * @return the best possible shift
    */
    private static int findBestShift(final double[] cipherFrequency) {
        double sum = 0, fitPerShift, d, best_fit = 1e100;
        BigDecimal bigDecimal = new BigDecimal(best_fit);
        int i, leftShift, bestShift = 0;

        //find sum
        for (i = 0; i < 26; i++)
            sum += cipherFrequency[i];

        for (leftShift= 0; leftShift < 26; leftShift++) {
            fitPerShift = 0;

            for (i = 0; i < 26; i++) {
                d = cipherFrequency[(i + leftShift) % 26] / sum - VigenereDecipher.frequencies[i];
                //as per the vigerene decipher goes, we multiply together every frequency of the letter
                //in the cipher group with the frequency of the letter in the english alphabet while
                //adding all the results together for each letter to come up with the fit for a shift
                fitPerShift += d * d / VigenereDecipher.frequencies[i];
            }

            if(bigDecimal.doubleValue() > fitPerShift) {
                bigDecimal = BigDecimal.valueOf(fitPerShift);
                bestShift = leftShift;
            }
        }

        return bestShift;
    }

    /**
     * This functions maps cipher characters to an array which matches the
     * indexing of the alphabet array. For example, if the first element in
     * the array is 'A', then that would result in 0 as it represents the
     * first letter in the alphabet.
     *
     * @param cipherArray array of cipher text broken into array
     * @return a mapped alphabet value array
    */
    private static int[] mapCipherToAlphabetArray(char[] cipherArray){
        int[] alphabetCipher = new int[cipherArray.length];
        int len = 0;
        //turn the cipher letters into corresponding alphabetic values
        for (char c : cipherArray)
            if (Character.isUpperCase(c))
                alphabetCipher[len++] = c - 'A';

        return alphabetCipher;
    }

    /**
     * Turn array of characters into string using StringBuilder
     * @param charArray array of characters to turn to string
     * @return stringified version of array
    */
    private static String charArrayToString(char[] charArray){
        StringBuilder string = new StringBuilder();
        for (char c : charArray) {
            string.append(c);
        }

        return string.toString();
    }

    /**
     * Left shift a given array using the modulus operator to calculate
     * the position of the next element
     *
     * @param array array to left shift
     * @param leftShift amount of left shift to do
     * @return a left shifted array
    */
    private static double[] leftShiftArray(double[] array, int leftShift){
        double[] shiftedArray = new double[array.length];
        for (int i = 0; i < 26; i++)
            shiftedArray[i] += array[(i + leftShift) % 26];

        return shiftedArray;
    }

    /**
     * Perform a sum of elements in an array
     * @param array to sum
     * @return sum
    */
    private static double sumOfArray(double[] array){
        double sum = 0;
        for (double element: array) {
            sum += element;
        }

        return sum;
    }
}
