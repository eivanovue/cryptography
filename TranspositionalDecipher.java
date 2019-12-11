import java.util.Arrays;

/**
 * This class decrypts transpositional encryption by splitting
 * an even numbered array into parts. It then is able to transpose
 * the arrays into one reading successive characters of columns
 * from left to right
 *
 * @author Emil Ivanov
 * @version 1.0
 */
public class TranspositionalDecipher {


    static String encodedMessage;
    static int columns;

    public static void main(String[] args) {

        encodedMessage = args[0];
        columns = Integer.parseInt(args[1]);
        char[] encodedArray = encodedMessage.toCharArray();
        char[][] arrayToTranspose = split(encodedArray, columns);

        char[] transposedArray = transpose(arrayToTranspose);

        System.out.println("Columns used: " + columns);
        System.out.println("Possible plaintext solution: " + charArrayToString(transposedArray));

    }

    /**
     * This function takes an array and transposes. It reads
     * successive characters from left to right so it is not
     * able to read read characters in an arbitrary order
     *
     * @param split array to transpose
     * @return transposed array
     */
    private static char[] transpose(char[][] split){
        char[] transposed = new char[encodedMessage.length()];
        int pos = 0;
        for(int i = 0; i < split[0].length; i++){
            for (char[] chars : split) {
                transposed[pos++] = chars[i];
            }
        }
        return transposed;
    }

    /**
     * This function splits an array into a two dimensional column array
     *
     * @param array to split
     * @param columns to split by
     * @return a 2d array split
    */
    private static char[][] split(char[] array, int columns){
        int splitArrayBy = array.length / columns;
        char[][] split = new char[columns][splitArrayBy];
        int currentSplit = 0;

            for (int i = 0; i < split.length; i++){
                split[i] = Arrays.copyOfRange(array, currentSplit, currentSplit += splitArrayBy);
            }

        return split;
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
}
