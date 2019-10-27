class FunnelChecker {

    static boolean isFunnel(String str1, String str2) {
        if (str1.length() != str2.length() + 1) {
            return false;
        }

        int lastEqualIndex = compareTo(str1, str2);
        return str2.equals(str1.substring(0, lastEqualIndex)
                .concat(str1.substring(lastEqualIndex + 1)));
    }

    static int compareTo(String s1, String s2) {
        int minSize = Math.min(s1.length(), s2.length());
        for (int i = 0; i < minSize; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return i;
            }
        }
        return minSize;
    }
}
