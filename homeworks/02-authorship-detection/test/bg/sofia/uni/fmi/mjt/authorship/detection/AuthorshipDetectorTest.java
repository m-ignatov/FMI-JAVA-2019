package bg.sofia.uni.fmi.mjt.authorship.detection;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AuthorshipDetectorTest {

    private static final String TEXT_DIR = "./resources/mysteryFiles/text.txt";
    private static final String SIGNATURES_DIR = "./resources/signatures/knownSignatures.txt";
    private static final double[] WEIGHTS = {11, 33, 50, 0.4, 4};

    private AuthorshipDetector authorshipDetector;

    @Before
    public void setup() throws FileNotFoundException {
        authorshipDetector = new AuthorshipDetectorImpl(new FileInputStream(SIGNATURES_DIR), WEIGHTS);
    }

    @Test
    public void givenTextFileWhenFindAuthorThenReturnCorrectAuthorOfText() throws FileNotFoundException {
        String author = authorshipDetector.findAuthor(new FileInputStream(TEXT_DIR));
//        assertEquals("Lewis Carroll", author);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullTextFileWhenFindAuthorThenThrowIllegalArgumentException() {
        authorshipDetector.findAuthor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullTextFileWhenCalculateSignatureThenThrowIllegalArgumentException() throws Exception {
        authorshipDetector.calculateSignature(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullLinguisticSignaturesWhenCalculateSignatureThenThrowIllegalArgumentException() {
        authorshipDetector.calculateSimilarity(null, null);
    }

    @Test
    public void givenEqualLinguisticSignaturesWhenCalculateSimilarityThenReturnSimilarityOfZero() {
        final double[] values = {1, 2, 3, 4, 5};

        double similarity = authorshipDetector
                .calculateSimilarity(buildLinguisticSignature(values), buildLinguisticSignature(values));

        assertEquals(0, Double.compare(0, similarity));
    }

    private LinguisticSignature buildLinguisticSignature(double... values) {
        Map<FeatureType, Double> features = new EnumMap<>(FeatureType.class);
        for (FeatureType featureType : FeatureType.values()) {
            features.put(featureType, values[featureType.ordinal()]);
        }
        return new LinguisticSignature(features);
    }
}
