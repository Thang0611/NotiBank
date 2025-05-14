package com.example.banknotireader;

import java.text.DecimalFormat;

    public class NumberToWordsConverter {
    private static final String[] UNITS = {"", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] TEENS = {"mười", "mười một", "mười hai", "mười ba", "mười bốn", "mười lăm", "mười sáu", "mười bảy", "mười tám", "mười chín"};
    private static final String[] TENS = {"", "mười", "hai mươi", "ba mươi", "bốn mươi", "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"};
    private static final String[] THOUSANDS = {"", "nghìn", "triệu", "tỷ"};

    public static String convert(long number) {
        if (number == 0) {
            return "không";
        }

        String formattedNumber = new DecimalFormat("000000000000").format(number);
        int[] segments = new int[4];

        for (int i = 0; i < 4; i++) {
            segments[i] = Integer.parseInt(formattedNumber.substring(i * 3, (i + 1) * 3));
        }

        StringBuilder result = new StringBuilder();

        if (segments[0] > 0) {
            result.append(convertLessThanOneThousand(segments[0])).append(" tỷ ");
        }
        if (segments[1] > 0) {
            result.append(convertLessThanOneThousand(segments[1])).append(" triệu ");
        }
        if (segments[2] > 0) {
            result.append(convertLessThanOneThousand(segments[2])).append(" nghìn ");
        }
        if (segments[3] > 0) {
            result.append(convertLessThanOneThousand(segments[3]));
        }

        return result.toString().trim().replaceAll("\\s{2,}", " ");
    }

    private static String convertLessThanOneThousand(int number) {
        int hundreds = number / 100;
        int tensAndUnits = number % 100;
        StringBuilder result = new StringBuilder();

        if (hundreds > 0) {
            result.append(UNITS[hundreds]).append(" trăm ");
        }

        if (tensAndUnits < 10) {
            result.append(UNITS[tensAndUnits]);
        } else if (tensAndUnits < 20) {
            result.append(TEENS[tensAndUnits - 10]);
        } else {
            int tens = tensAndUnits / 10;
            int units = tensAndUnits % 10;
            result.append(TENS[tens]);

            if (units > 0) {
                result.append(" ").append(UNITS[units]);
            }
        }

        return result.toString().trim();
    }

    public static String convertNumberToWords(long number) {
        if (number == 0) {
            return "không";
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (number > 0) {
            long segment = number % 1000;
            if (segment != 0) {
                result.insert(0, convertLessThanOneThousand2(segment) + THOUSANDS[i] + " ");
            }
            i++;
            number /= 1000;
        }

        return result.toString().trim();
    }

    private static String convertLessThanOneThousand2(long number) {
        int hundreds = (int) (number / 100);
        int tensAndUnits = (int) (number % 100);
        StringBuilder result = new StringBuilder();

        if (hundreds > 0) {
            result.append(UNITS[hundreds]).append(" trăm ");
        }

        if (tensAndUnits < 10) {
            result.append(UNITS[tensAndUnits]);
        } else if (tensAndUnits < 20) {
            result.append(TEENS[tensAndUnits - 10]);
        } else {
            int tens = tensAndUnits / 10;
            int units = tensAndUnits % 10;
            result.append(TENS[tens]);

            if (units > 0) {
                result.append(" ").append(UNITS[units]);
            }
        }

        return result.toString().trim();
    }


}
