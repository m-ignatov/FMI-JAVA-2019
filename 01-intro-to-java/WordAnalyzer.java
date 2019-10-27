package bg.uni.sofia.fmi.java.first;

import java.util.*;

public class WordAnalyzer {

    public static String getSharedLetters(String word1, String word2) {
        String w1 = word1.toLowerCase();
        String w2 = word2.toLowerCase();

        boolean[] list = new boolean[27];
        for (int i = 0; i < w1.length(); i++) {
            int letterNo = (w1.charAt(i) - 'a');
            list[letterNo] = true;
        }

        List<Character> dict = new ArrayList<>();
        for (int i = 0; i < w2.length(); i++) {
            if (list[w2.charAt(i) - 'a']) {
                dict.add(w2.charAt(i));
            }
        }

        dict.sort(Comparator.naturalOrder());

        StringBuilder sb = new StringBuilder();
        for (Character c : dict) {
            sb.append(c);
        }
        return sb.toString();
    }
}
