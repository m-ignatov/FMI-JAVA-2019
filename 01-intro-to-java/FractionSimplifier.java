class FractionSimplifier {

    static String simplify(String fraction) {
        if (!fraction.matches("^[0-9]+/[0-9]+$")) {
            throw new IllegalArgumentException("Invalid fraction format");
        }

        String[] numbers = fraction.split("/");
        int a = Integer.parseInt(numbers[0]);
        int b = Integer.parseInt(numbers[1]);
        int gcd = gcd(a, b);
        a /= gcd;
        b /= gcd;
        if (b == 1) return String.valueOf(a);
        return String.format("%s/%s", a, b);
    }

    private static int gcd(int a, int b) {
        if (a == 0) return b;
        return gcd(b % a, a);
    }
}
