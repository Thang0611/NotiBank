package com.NT.banknotireader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//
//public class TransactionParser {
//
//    // Phân tích thông báo giao dịch từ nội dung thông báo chung
//    public static String parseTransaction(String notification) {
//        boolean isIncome;
//        long amount;
//
//        // Tìm kiếm số tiền giao dịch với dấu "+" hoặc "-" trước số tiền
//        Matcher amountMatcher = Pattern.compile("([+-]) ([0-9,.]+)").matcher(notification);
//        String transactionDetails = "";
//
//        if (amountMatcher.find()) {
//            isIncome = "+".equals(amountMatcher.group(1)); // Xác định thu (income) hay chi (expense)
//            amount = Long.parseLong(amountMatcher.group(2).replaceAll(",", ""));
//        } else {
//            isIncome = false;
//            amount = 0;
//        }
//
//        // Tìm kiếm thông tin giao dịch (chuyển tiền, chuyển khoản)
//        Matcher transactionMatcher = Pattern.compile("GD: (.+?) chuyen tien|GD: (.+?) chuyen khoan").matcher(notification);
//        if (transactionMatcher.find()) {
//            transactionDetails = transactionMatcher.group(1) != null ? transactionMatcher.group(1) : transactionMatcher.group(2);
//        }
//
//        // Xây dựng kết quả trả về với thông tin giao dịch và số tiền
//        return (isIncome ? "Nhận từ" : "Chuyển đi") + ": " + transactionDetails + "\nSố tiền: " + NumberToWordsConverter.convert(amount) + " đồng";
//    }
//
//    // Phân tích thông báo giao dịch từ VietcomBank
//    public static String parseVietcomBankNotification(String notification) {
//        String transactionDetails = "";
//        // Tách các phần của thông báo để tìm số tiền giao dịch
//        for (String part : notification.split("[\\.\\n\\|\\:]")) {
//            if ((part.contains("+") || part.contains("-")) && part.contains("VND") && part.contains(",")) {
//                transactionDetails = part;
//            }
//        }
//
//        // Tìm kiếm số tiền trong giao dịch và xác định hướng (thu hay chi)
//        int lastCommaIndex = transactionDetails.lastIndexOf(44);
//        if (lastCommaIndex == -1) {
//            return transactionDetails;
//        }
//
//        int startIndex = lastCommaIndex;
//        for (int i = lastCommaIndex; i > 1; i--) {
//            char currentChar = transactionDetails.charAt(i);
//            int prevIndex = i - 1;
//            char prevChar = transactionDetails.charAt(prevIndex);
//            if (currentChar == '+' || currentChar == '-') {
//                startIndex = i;
//                break;
//            }
//            if (currentChar == ' ' && (prevChar == '+' || prevChar == '-')) {
//                startIndex = prevIndex;
//                break;
//            }
//        }
//
//        String trimmedAmount = transactionDetails.substring(startIndex, lastCommaIndex + 4).trim();
//        boolean isNegative = trimmedAmount.contains("-");
//        String cleanedAmount = trimmedAmount.replace(",", "").replace("+", "").replace("-", "");
//
//        return isNegative ? "Vừa chuyển đi: " + cleanedAmount + " đồng" : "Vừa nhận: " + cleanedAmount + " đồng";
//    }
//
//    // Phân tích thông báo giao dịch từ MSB
//    public static String parseMSBNotification(String notification) {
//        Matcher matcher = Pattern.compile("\\((\\+|\\-)\\)\\s([\\d,]+)").matcher(notification);
//        if (matcher.find()) {
//            String sign = matcher.group(1);
//            String amountStr = matcher.group(2);
//            if (amountStr != null && sign != null) {
//                String cleanedAmount = amountStr.replace(",", "");
//                if (sign.equals("+")) {
//                    return "Vừa nhận: " + cleanedAmount + " đồng";
//                } else if (sign.equals("-")) {
//                    return "Vừa chuyển đi: " + cleanedAmount + " đồng";
//                }
//            }
//        }
//        return "";
//    }
//
//    // Phân tích thông báo giao dịch từ TPBank
//    public static String parseTPBNotification(String notification) {
//        Matcher matcher = Pattern.compile("PS:(\\+|\\-)([\\d,.]+)VND").matcher(notification);
//        if (matcher.find()) {
//            String sign = matcher.group(1);
//            String amountStr = matcher.group(2);
//            if (sign != null && amountStr != null) {
//                String cleanedAmount = amountStr.replace(".", "");
//                if (sign.equals("+")) {
//                    return "Vừa nhận: " + cleanedAmount + " đồng";
//                } else if (sign.equals("-")) {
//                    return "Vừa chuyển đi: " + cleanedAmount + " đồng";
//                }
//            }
//        }
//        return "";
//    }
//
//    // Phân tích thông báo giao dịch từ TPBank (phiên bản 2)
//    public static String parseTPB2Notification(String notification) {
//        String transactionDetails = "";
//        // Tách các phần của thông báo để tìm số tiền giao dịch
//        for (String part : notification.split("[\\.\\n\\|]")) {
//            if ((part.contains("+") || part.contains("-")) && part.contains("VND")) {
//                transactionDetails = part;
//            }
//        }
//
//        int lastDotIndex = transactionDetails.lastIndexOf(46);
//        if (lastDotIndex == -1) {
//            return transactionDetails;
//        }
//
//        int startIndex = lastDotIndex;
//        for (int i = lastDotIndex; i > 1; i--) {
//            char currentChar = transactionDetails.charAt(i);
//            int prevIndex = i - 1;
//            char prevChar = transactionDetails.charAt(prevIndex);
//            if (currentChar == '+' || currentChar == '-') {
//                startIndex = i;
//                break;
//            }
//            if (currentChar == ' ' && (prevChar == '+' || prevChar == '-')) {
//                startIndex = prevIndex;
//                break;
//            }
//        }
//
//        String trimmedAmount = transactionDetails.substring(startIndex, lastDotIndex + 4).trim();
//        boolean isNegative = trimmedAmount.contains("-");
//        String cleanedAmount = trimmedAmount.replace(".", "").replace("+", "").replace("-", "");
//
//        return isNegative ? "Vừa chuyển đi: " + cleanedAmount + " đồng" : "Vừa nhận: " + cleanedAmount + " đồng";
//    }
//
//    // Phân tích thông báo giao dịch từ TCB
//    public static String parseTCBNotification(String notification) {
//        boolean isNegative = notification.contains("-");
//        String cleanedAmount = notification.replace(",", "").replace("VND", "").replace("+", "").replace("-", "");
//        return isNegative ? "Vừa chuyển đi: " + cleanedAmount + " đồng" : "Vừa nhận: " + cleanedAmount + " đồng";
//    }
//
//    // Phân tích thông báo giao dịch từ ABBank
//    public static String parseABBankNotification(String notification) {
//        boolean isNegative = notification.contains("-");
//        String cleanedAmount = notification.replace(".", "").replace("VND", "").replace("+", "").replace("-", "");
//        return isNegative ? "Vừa chuyển đi: " + cleanedAmount + " đồng" : "Vừa nhận: " + cleanedAmount + " đồng";
//    }
//
//    // Phân tích thông báo giao dịch từ VIB
//    public static String parseVIBNotification(String notification) {
//        Matcher matcher = Pattern.compile("phát sinh\\s(\\+|\\-)([\\d,.]+)\\s₫").matcher(notification);
//        if (matcher.find()) {
//            String sign = matcher.group(1);
//            String amountStr = matcher.group(2);
//            if (sign != null && amountStr != null) {
//                String cleanedAmount = amountStr.replace(",", "");
//                if (sign.equals("+")) {
//                    return "Vừa nhận: " + cleanedAmount + " đồng";
//                } else if (sign.equals("-")) {
//                    return "Vừa chuyển đi: " + cleanedAmount + " đồng";
//                }
//            }
//        }
//        return "";
//    }
//
//    // Phân tích thông báo giao dịch từ MoMo
//    public static String parseMomoNotification(String notification) {
//        String transactionAmount;
//        Pattern amountPattern = Pattern.compile("tiền (\\d+[,.]?\\d*)");
//        if (notification.contains("tiền:")) {
//            amountPattern = Pattern.compile("tiền: (\\d+[,.]?\\d*)");
//        }
//        Matcher matcher = amountPattern.matcher(notification);
//        if (!matcher.find() || (transactionAmount = matcher.group(1)) == null) {
//            return "";
//        }
//        return "Vừa nhận: " + transactionAmount.replace(".", "") + " đồng";
//    }
//
//
//    // Các phương thức tương tự cho các ngân hàng khác có thể tiếp tục ở đây.
//    public static String Sacombank(String str) {
//        Matcher matcher = Pattern.compile("Số tiền:\\s*([+-]?\\d{1,3}(?:,\\d{3})*)\\s*VND").matcher(str);
//        if (!matcher.find()) {
//            return "";
//        }
//        String group = matcher.group(1);
//        boolean contains = group.contains("-");
//        String replace = group.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String ShinhanBank(String str) {
//        Matcher matcher = Pattern.compile("Số tiền:\\s*([+-]?\\d{1,3}(?:,\\d{3})*)\\s*VND").matcher(str);
//        if (!matcher.find()) {
//            return "";
//        }
//        String group = matcher.group(1);
//        boolean contains = group.contains("-");
//        String replace = group.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String TPBank(String str) {
//        Matcher matcher = Pattern.compile("Số tiền:\\s*([+-]?\\d{1,3}(?:,\\d{3})*)\\s*VND").matcher(str);
//        if (!matcher.find()) {
//            return "";
//        }
//        String group = matcher.group(1);
//        boolean contains = group.contains("-");
//        String replace = group.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String Vietcombank(String str) {
//        Matcher matcher = Pattern.compile("Số tiền:\\s*([+-]?\\d{1,3}(?:,\\d{3})*)\\s*VND").matcher(str);
//        if (!matcher.find()) {
//            return "";
//        }
//        String group = matcher.group(1);
//        boolean contains = group.contains("-");
//        String replace = group.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String BIDV(String str) {
//        Matcher matcher = Pattern.compile("Số tiền:\\s*([+-]?\\d{1,3}(?:,\\d{3})*)\\s*VND").matcher(str);
//        if (!matcher.find()) {
//            return "";
//        }
//        String group = matcher.group(1);
//        boolean contains = group.contains("-");
//        String replace = group.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String Agribank(String str) {
//        Matcher matcher = Pattern.compile("Số tiền giao dịch:\\s*([+-]?\\d{1,3}(?:,\\d{3})*)\\s*VND").matcher(str);
//        if (!matcher.find()) {
//            return "";
//        }
//        String group = matcher.group(1);
//        boolean contains = group.contains("-");
//        String replace = group.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String MSB(String str) {
//        Matcher matcher = Pattern.compile("\\((\\+|\\-)\\)\\s([\\d,]+)").matcher(str);
//        if (matcher.find()) {
//            String group = matcher.group(1);
//            String group2 = matcher.group(2);
//            if (group2 != null && group != null) {
//                String replace = group2.replace(",", "");
//                if (group.equals("+")) {
//                    return "vừa nhận :" + replace + " đồng";
//                }
//                if (group.equals("-")) {
//                    return "vừa chuyển đi : " + replace + " đồng";
//                }
//            }
//        }
//        return "";
//    }
//
//    public static String TPB(String str) {
//        Matcher matcher = Pattern.compile("PS:(\\+|\\-)([\\d,.]+)VND").matcher(str);
//        if (matcher.find()) {
//            String group = matcher.group(1);
//            String group2 = matcher.group(2);
//            if (group != null && group2 != null) {
//                String replace = group2.replace(".", "");
//                if (group.equals("+")) {
//                    return "vừa nhận :" + replace + " đồng";
//                }
//                if (group.equals("-")) {
//                    return "vừa chuyển đi : " + replace + " đồng";
//                }
//            }
//        }
//        return "";
//    }
//
//    public static String TPB2(String str) {
//        int i = 0;
//        String str2 = "";
//        for (String str3 : str.split("[\\.\\n\\|]")) {
//            if ((str3.contains("+") || str3.contains("-")) && str3.contains("VND")) {
//                str2 = str3;
//            }
//        }
//        int lastIndexOf = str2.lastIndexOf(46);
//        if (lastIndexOf == -1) {
//            return str2;
//        }
//        for (int i2 = lastIndexOf; i2 > 1; i2--) {
//            char charAt = str2.charAt(i2);
//            int i3 = i2 - 1;
//            char charAt2 = str2.charAt(i3);
//            if (charAt == '+' || charAt == '-') {
//                i = i2;
//                break;
//            }
//            if (charAt == ' ' && (charAt2 == '+' || charAt2 == '-')) {
//                i = i3;
//                break;
//            }
//        }
//        String trim = str2.substring(i, lastIndexOf + 4).trim();
//        boolean contains = trim.contains("-");
//        String replace = trim.replace(".", "").replace("+", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String TCB(String str) {
//        boolean contains = str.contains("-");
//        String replace = str.replace(",", "").replace("VND", "").replace("+", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String ABBank(String str) {
//        boolean contains = str.contains("-");
//        String replace = str.replace(".", "").replace("VND", "").replace("+", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String VIB(String str) {
//        Matcher matcher = Pattern.compile("phát sinh\\s(\\+|\\-)([\\d,.]+)\\s₫").matcher(str);
//        if (matcher.find()) {
//            String group = matcher.group(1);
//            String group2 = matcher.group(2);
//            if (group != null && group2 != null) {
//                String replace = group2.replace(",", "");
//                if (group.equals("+")) {
//                    return "vừa nhận :" + replace + " đồng";
//                }
//                if (group.equals("-")) {
//                    return "vừa chuyển đi : " + replace + " đồng";
//                }
//            }
//        }
//        return "";
//    }
//
//    public static String Momo(String str) {
//        String group;
//        Pattern compile = Pattern.compile("tiền (\\d+[,.]?\\d*)");
//        if (str.contains("tiền:")) {
//            compile = Pattern.compile("tiền: (\\d+[,.]?\\d*)");
//        }
//        Matcher matcher = compile.matcher(str);
//        return (!matcher.find() || (group = matcher.group(1)) == null) ? "" : "vừa nhận :" + group.replace(".", "") + " đồng";
//    }
//
//    public static String ShareNoti(String str) {
//        int i = 0;
//        String str2 = "";
//        for (String str3 : str.split("[\\.\\n\\|\\:]")) {
//            if ((str3.contains("+") || str3.contains("-")) && ((str3.contains("₫") || str3.contains("đ")) && str3.contains(","))) {
//                str2 = str3;
//            }
//        }
//        int lastIndexOf = str2.lastIndexOf(44);
//        if (lastIndexOf == -1) {
//            return str2;
//        }
//        for (int i2 = lastIndexOf; i2 > 1; i2--) {
//            char charAt = str2.charAt(i2);
//            int i3 = i2 - 1;
//            char charAt2 = str2.charAt(i3);
//            if (charAt == '+' || charAt == '-') {
//                i = i2;
//                break;
//            }
//            if (charAt == ' ' && (charAt2 == '+' || charAt2 == '-')) {
//                i = i3;
//                break;
//            }
//        }
//        String trim = str2.substring(i, lastIndexOf + 4).trim();
//        boolean contains = trim.contains("-");
//        String replace = trim.replace(",", "").replace("+", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String VietPay(String str) {
//        Matcher matcher = Pattern.compile("giao dịch:(\\+|\\-)([\\d,.]+)VND").matcher(str);
//        if (!matcher.find()) {
//            return null;
//        }
//        String group = matcher.group(1);
//        String group2 = matcher.group(2);
//        if (group == null || group2 == null) {
//            return null;
//        }
//        String replace = group2.replace(".", "");
//        if (group.equals("+")) {
//            return "vừa nhận :" + replace + " đồng";
//        }
//        if (group.equals("-")) {
//            return "vừa chuyển đi : " + replace + " đồng";
//        }
//        return null;
//    }
//
//    public static String ViettelPay(String str) {
//        int i = 0;
//        String str2 = str.split("\n")[0];
//        int lastIndexOf = str2.lastIndexOf(46);
//        if (lastIndexOf == -1) {
//            return str2;
//        }
//        for (int i2 = lastIndexOf; i2 > 1; i2--) {
//            char charAt = str2.charAt(i2);
//            int i3 = i2 - 1;
//            char charAt2 = str2.charAt(i3);
//            if (charAt == '+' || charAt == '-') {
//                i = i2;
//                break;
//            }
//            if (charAt == ' ' && (charAt2 == '+' || charAt2 == '-')) {
//                i = i3;
//                break;
//            }
//        }
//        String trim = str2.substring(i, lastIndexOf + 4).trim();
//        boolean contains = trim.contains("-");
//        String replace = trim.replace(".", "").replace("+", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String ShareVCB(String str) {
//        int indexOf = str.indexOf(43);
//        int indexOf2 = str.indexOf(" lúc", indexOf);
//        if (indexOf == -1 || indexOf2 == -1) {
//            return null;
//        }
//        return "vừa nhận :" + str.substring(indexOf + 1, indexOf2).trim().replace(",", "") + " đồng";
//    }
//
//    public static String LPBank(String str) {
//        int indexOf = str.indexOf("Số tiền GD:");
//        int indexOf2 = str.indexOf(124, indexOf);
//        if (indexOf == -1 || indexOf2 == -1) {
//            return null;
//        }
//        String trim = str.substring(indexOf + 11, indexOf2).trim();
//        boolean contains = trim.contains("-");
//        String replace = trim.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String VPBank(String str) {
//        boolean contains = str.contains("-");
//        String replace = str.replace(",", "").replace("₫", "").replace("+", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String CoopBank(String str) {
//        Matcher matcher = Pattern.compile("P/S:\\s*([+-]?\\d{1,3}(?:,\\d{3})*)\\s*VND").matcher(str);
//        if (!matcher.find()) {
//            return "";
//        }
//        String group = matcher.group(1);
//        boolean contains = group.contains("-");
//        String replace = group.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String VietQR(String str) {
//        String replace = str.split("\\|")[1].replace("GD:", "");
//        boolean contains = replace.contains("-");
//        String replace2 = replace.replace("+", "").replace("-", "").replace(",", "");
//        return contains ? "vừa chuyển đi : " + replace2 + " đồng" : "vừa nhận :" + replace2 + " đồng";
//    }
//
//    public static String OCB(String str) {
//        int i = 0;
//        String str2 = "";
//        for (String str3 : str.split("[\\n]")) {
//            if ((str3.contains("+") || str3.contains("-")) && str3.contains("VND") && str3.contains(".")) {
//                str2 = str3;
//            }
//        }
//        int lastIndexOf = str2.lastIndexOf(46);
//        if (lastIndexOf == -1) {
//            return str2;
//        }
//        for (int i2 = lastIndexOf; i2 > 1; i2--) {
//            char charAt = str2.charAt(i2);
//            int i3 = i2 - 1;
//            char charAt2 = str2.charAt(i3);
//            if (charAt == '+' || charAt == '-') {
//                i = i2;
//                break;
//            }
//            if (charAt == ' ' && (charAt2 == '+' || charAt2 == '-')) {
//                i = i3;
//                break;
//            }
//        }
//        String trim = str2.substring(i, lastIndexOf + 4).trim();
//        boolean contains = trim.contains("-");
//        String replace = trim.replace(".", "").replace("+", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//
//    public static String HDBank(String str) {
//        Matcher matcher = Pattern.compile("([+-])(\\d{1,3}(?:,\\d{3})*)").matcher(str);
//        if (!matcher.find()) {
//            return null;
//        }
//        String group = matcher.group(2);
//        boolean contains = matcher.group(1).contains("-");
//        String replace = group.replace(",", "").replace("-", "");
//        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
//    }
//}
//


public class TransactionParser {

    public static String parseTransaction(String str) {
        boolean isIncoming = false;
        long amount = 0;
        String transactionDetail = "";

        Matcher matcher = Pattern.compile("([+-]) ([0-9,.]+)").matcher(str);
        if (matcher.find()) {
            isIncoming = "+".equals(matcher.group(1));
            amount = Long.parseLong(matcher.group(2).replaceAll(",", ""));
        }

        Matcher matcher2 = Pattern.compile("GD: (.+?) chuyen tien|GD: (.+?) chuyen khoan").matcher(str);
        if (matcher2.find()) {
            transactionDetail = matcher2.group(1) != null ? matcher2.group(1) : matcher2.group(2);
        }

        return (isIncoming ? "Nhận từ" : "Chuyển đi") + ": " + transactionDetail + "\n Số tiền: " + NumberToWordsConverter.convert(amount) + " đồng";
    }

    public static String VietcomBank(String str) {
        String lineWithAmount = "";
        for (String part : str.split("[\\.\\n\\|\\:]")) {
            if ((part.contains("+") || part.contains("-")) && part.contains("VND") && part.contains(",")) {
                lineWithAmount = part;
            }
        }

        int lastIndex = lineWithAmount.lastIndexOf(',');
        if (lastIndex == -1) return lineWithAmount;

        int startIndex = 0;
        for (int i = lastIndex; i > 1; i--) {
            char current = lineWithAmount.charAt(i);
            char prev = lineWithAmount.charAt(i - 1);
            if (current == '+' || current == '-') {
                startIndex = i;
                break;
            }
            if (current == ' ' && (prev == '+' || prev == '-')) {
                startIndex = i - 1;
                break;
            }
        }

        String amountStr = lineWithAmount.substring(startIndex, lastIndex + 4).trim();
        boolean isOutgoing = amountStr.contains("-");
        String amount = amountStr.replace(",", "").replace("+", "").replace("-", "");
        return isOutgoing ? "vừa chuyển đi : " + amount + " đồng" : "vừa nhận :" + amount + " đồng";
    }

    public static String MSB(String str) {
        Matcher matcher = Pattern.compile("\\((\\+|\\-)\\)\\s([\\d,]+)").matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String value = matcher.group(2);
            if (sign != null && value != null) {
                String amount = value.replace(",", "");
                return sign.equals("+") ? "vừa nhận :" + amount + " đồng" : "vừa chuyển đi : " + amount + " đồng";
            }
        }
        return "";
    }

    public static String TPB(String str) {
        Matcher matcher = Pattern.compile("PS:(\\+|\\-)([\\d,.]+)VND").matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String value = matcher.group(2);
            if (sign != null && value != null) {
                String amount = value.replace(".", "");
                return sign.equals("+") ? "vừa nhận :" + amount + " đồng" : "vừa chuyển đi : " + amount + " đồng";
            }
        }
        return "";
    }

    public static String TPB2(String str) {
        String lineWithAmount = "";
        for (String part : str.split("[\\.\\n\\|]")) {
            if ((part.contains("+") || part.contains("-")) && part.contains("VND")) {
                lineWithAmount = part;
            }
        }

        int lastIndex = lineWithAmount.lastIndexOf('.');
        if (lastIndex == -1) return lineWithAmount;

        int startIndex = 0;
        for (int i = lastIndex; i > 1; i--) {
            char current = lineWithAmount.charAt(i);
            char prev = lineWithAmount.charAt(i - 1);
            if (current == '+' || current == '-') {
                startIndex = i;
                break;
            }
            if (current == ' ' && (prev == '+' || prev == '-')) {
                startIndex = i - 1;
                break;
            }
        }

        String amountStr = lineWithAmount.substring(startIndex, lastIndex + 4).trim();
        boolean isOutgoing = amountStr.contains("-");
        String amount = amountStr.replace(".", "").replace("+", "").replace("-", "");
        return isOutgoing ? "vừa chuyển đi : " + amount + " đồng" : "vừa nhận :" + amount + " đồng";
    }

    public static String TCB(String str) {
        boolean isOutgoing = str.contains("-");
        String amount = str.replace(",", "").replace("VND", "").replace("+", "").replace("-", "");
        return isOutgoing ? "vừa chuyển đi : " + amount + " đồng" : "vừa nhận :" + amount + " đồng";
    }

    public static String ABBank(String str) {
        boolean isOutgoing = str.contains("-");
        String amount = str.replace(".", "").replace("VND", "").replace("+", "").replace("-", "");
        return isOutgoing ? "vừa chuyển đi : " + amount + " đồng" : "vừa nhận :" + amount + " đồng";
    }

    public static String VIB(String str) {
        Matcher matcher = Pattern.compile("phát sinh\\s(\\+|\\-)([\\d,.]+)\\s₫").matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String value = matcher.group(2);
            if (sign != null && value != null) {
                String amount = value.replace(",", "");
                return sign.equals("+") ? "vừa nhận :" + amount + " đồng" : "vừa chuyển đi : " + amount + " đồng";
            }
        }
        return "";
    }

    public static String Momo(String str) {
        Pattern pattern = Pattern.compile("tiền (\\d+[,.]?\\d*)");
        if (str.contains("tiền:")) {
            pattern = Pattern.compile("tiền: (\\d+[,.]?\\d*)");
        }
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String value = matcher.group(1);
            return value != null ? "vừa nhận :" + value.replace(".", "") + " đồng" : "";
        }
        return "";
    }

    public static String ShareNoti(String str) {
        String lineWithAmount = "";
        for (String part : str.split("[\\.\\n\\|\\:]")) {
            if ((part.contains("+") || part.contains("-")) && (part.contains("₫") || part.contains("đ")) && part.contains(",")) {
                lineWithAmount = part;
            }
        }

        int lastIndex = lineWithAmount.lastIndexOf(',');
        if (lastIndex == -1) return lineWithAmount;

        int startIndex = 0;
        for (int i = lastIndex; i > 1; i--) {
            char current = lineWithAmount.charAt(i);
            char prev = lineWithAmount.charAt(i - 1);
            if (current == '+' || current == '-') {
                startIndex = i;
                break;
            }
            if (current == ' ' && (prev == '+' || prev == '-')) {
                startIndex = i - 1;
                break;
            }
        }

        String amountStr = lineWithAmount.substring(startIndex, lastIndex + 4).trim();
        boolean isOutgoing = amountStr.contains("-");
        String amount = amountStr.replace(",", "").replace("+", "").replace("-", "");
        return isOutgoing ? "vừa chuyển đi : " + amount + " đồng" : "vừa nhận :" + amount + " đồng";
    }

    public static String VietQR(String str) {
        String[] parts = str.split("\\|");
        if (parts.length < 2) return "";
        String value = parts[1].replace("GD:", "");
        boolean isOutgoing = value.contains("-");
        String amount = value.replace("+", "").replace("-", "").replace(",", "");
        return isOutgoing ? "vừa chuyển đi : " + amount + " đồng" : "vừa nhận :" + amount + " đồng";
    }

    // Còn các phương thức khác như ViettelPay, LPBank, VPBank, CoopBank, ShareVCB, OCB bạn muốn mình tiếp tục không?
    public static String ViettelPay(String str) {
        Pattern pattern = Pattern.compile("GD:\\s(\\+|\\-)([\\d,.]+)\\sVND");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String amount = matcher.group(2);
            if (sign != null && amount != null) {
                String value = amount.replace(",", "");
                return sign.equals("+") ? "vừa nhận :" + value + " đồng" : "vừa chuyển đi : " + value + " đồng";
            }
        }
        return "";
    }

    public static String LPBank(String str) {
        Pattern pattern = Pattern.compile("GD: (\\+|\\-)([\\d,.]+) VND");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String amount = matcher.group(2);
            String value = amount.replace(",", "");
            return sign.equals("+") ? "vừa nhận :" + value + " đồng" : "vừa chuyển đi : " + value + " đồng";
        }
        return "";
    }

    public static String VPBank(String str) {
        boolean contains = str.contains("-");
        String replace = str.replace(",", "").replace("₫", "").replace("+", "").replace("-", "");
        return contains ? "vừa chuyển đi : " + replace + " đồng" : "vừa nhận :" + replace + " đồng";
    }

    public static String CoopBank(String str) {
        Pattern pattern = Pattern.compile("GD: (\\+|\\-)([\\d,.]+) VND");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String amount = matcher.group(2);
            String value = amount.replace(",", "");
            return sign.equals("+") ? "vừa nhận :" + value + " đồng" : "vừa chuyển đi : " + value + " đồng";
        }
        return "";
    }
    public static String ShareVCB(String str) {
        Pattern pattern = Pattern.compile("GD: (\\+|\\-)([\\d,.]+) VND");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String amount = matcher.group(2);
            String value = amount.replace(",", "");
            return sign.equals("+") ? "vừa nhận :" + value + " đồng" : "vừa chuyển đi : " + value + " đồng";
        }
        return "";
    }
    public static String OCB(String str) {
        Pattern pattern = Pattern.compile("GD: (\\+|\\-)([\\d,.]+) VND");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String amount = matcher.group(2);
            String value = amount.replace(",", "");
            return sign.equals("+") ? "vừa nhận :" + value + " đồng" : "vừa chuyển đi : " + value + " đồng";
        }
        return "";
    }
    public static String HDBank(String str) {
        Pattern pattern = Pattern.compile("([+-])(\\d{1,3}(?:,\\d{3})*)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String sign = matcher.group(1);
            String amount = matcher.group(2).replace(",", "");
            return sign.equals("-") ? "vừa chuyển đi : " + amount + " đồng" : "vừa nhận :" + amount + " đồng";
        }
        return "";
    }
    public static String VietPay(String str) {
        Matcher matcher = Pattern.compile("giao dịch:(\\+|\\-)([\\d,.]+)VND").matcher(str);
        if (!matcher.find()) {
            return null;
        }
        String group = matcher.group(1);
        String group2 = matcher.group(2);
        if (group == null || group2 == null) {
            return null;
        }
        String replace = group2.replace(".", "");
        if (group.equals("+")) {
            return "vừa nhận :" + replace + " đồng";
        }
        if (group.equals("-")) {
            return "vừa chuyển đi : " + replace + " đồng";
        }
        return null;
    }

//    public static String VietcomBank(String content) {
//        String amountLine = "";
//        for (String line : content.split("[\\.\\n\\|\\:]")) {
//            if ((line.contains("+") || line.contains("-")) && line.contains("VND") && line.contains(",")) {
//                amountLine = line;
//                break;
//            }
//        }
//
//        if (amountLine.isEmpty()) return "";
//
//        Matcher matcher = Pattern.compile("([+-])\\s?(\\d{1,3}(?:,\\d{3})*)").matcher(amountLine);
//        if (matcher.find()) {
//            String sign = matcher.group(1);
//            String amount = matcher.group(2).replace(",", "");
//            return sign.equals("-") ? "vừa chuyển đi : " + amount + " đồng" : "vừa nhận : " + amount + " đồng";
//        }
//
//        return "";
//    }
}
