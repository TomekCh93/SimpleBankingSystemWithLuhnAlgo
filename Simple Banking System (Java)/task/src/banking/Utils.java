package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    private static final Random rnd = new Random();

    private Utils() {
    }

    public static int calculateLuhnCheckSum(String format) {
        List<Integer> tempNums = new ArrayList<>();
        for (int i = 0; i < format.length(); i++) {
            int intValue = Character.getNumericValue(format.charAt(i));
            if ((i + 1) % 2 != 0) {
                intValue = intValue << 1;
                tempNums.add(intValue > 9 ? intValue - 9 : intValue);
            } else {
                tempNums.add(intValue);
            }
        }
        int sum = tempNums.stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (sum % 10 != 0) {
            return 10 - (sum % 10);
        }

        return 0;


    }

    public static String generateNewPIN() {
        int rndInt = rnd.nextInt(9999);
        return String.format("%04d", rndInt);

    }

    public static String generateNewCardNumber() {
        String BIN = "400000";
        Long rndLong = rnd.nextLong(999999999L);
        String format = String.format("%s%09d", BIN, rndLong);
        return format + calculateLuhnCheckSum(format);
    }

    public static boolean verifyCardNumber(String card) {
        if (!card.isEmpty() && card.length() == 16) {
            return calculateLuhnCheckSum(card.substring(0, 15)) == Integer.parseInt(card.substring(15, 16));
        }
        return false;
    }

}
