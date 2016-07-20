package py.com.excelsis.sicca.util;
/***
 * 
 * GENERADOR DE COD EJEMPLO AAA000, AAA001
 * ****/
public class LicenseNumber {

    int numericSum;
    int letterSum;

    public LicenseNumber() {
        numericSum = letterSum = 0;
    }

    public LicenseNumber(String number) {
        if (!number.matches("^[A-Za-z]{3}[0-9]{3}$"))
         throw new IllegalArgumentException("Number doesn't match license format");
        numericSum = Integer.valueOf(number.substring(3));
        letterSum = value(number, 0) * 26 * 26 + value(number, 1) * 26 + 
           value(number, 2);
    }

    public void increment() {
        increment(1);
    }

    public void increment(int inc) {
        numericSum += inc;
        if (numericSum >= 1000) {
         letterSum += numericSum/1000;
         numericSum %= 1000;     
        }
    }

    public String toString() {
        char[] letters = new char[3];
        int n = letterSum;
        for (int i = 0; i < 3; i++) {
         letters[2-i] = (char)('A' + (n%26));
         n /= 26;
        }
        return new String(letters) + String.format("%03d", numericSum);
    }

    private int value(String s, int index) {
        return Character.toUpperCase(s.charAt(index)) - 'A';
    }
}