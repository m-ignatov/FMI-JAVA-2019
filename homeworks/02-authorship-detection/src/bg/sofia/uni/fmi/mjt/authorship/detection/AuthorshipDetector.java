package bg.sofia.uni.fmi.mjt.authorship.detection;

import java.io.InputStream;

public interface AuthorshipDetector {

    /**
     * Returns the linguistic signature for the given input stream @mysteryText based on the following features:
     * 1. Average Word Complexity
     * 2. Type Token Ratio
     * 3. Hapax Legomena Ratio
     * 4. Average Sentence Length
     * 5. Average Sentence Complexity
     *
     * @throws IllegalArgumentException if @mysteryText is null
     */
    LinguisticSignature calculateSignature(InputStream mysteryText);

    /**
     * Returns a non-negative real number indicating the similarity between @firstSignature and @secondSignature.
     * The calculation should be done using the formula in the assignment.
     * <p>
     * The smaller the number, the more similar the signatures. Zero indicates identical signatures.
     *
     * @throws IllegalArgumentException if @firstSignature or @secondSignature is null
     */
    double calculateSimilarity(LinguisticSignature firstSignature, LinguisticSignature secondSignature);

    /**
     * Returns the name of the author that best matches the given @mysteryText
     *
     * @throws IllegalArgumentException if @mysteryText is null
     */
    String findAuthor(InputStream mysteryText);
}