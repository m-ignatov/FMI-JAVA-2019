package bg.sofia.uni.fmi.mjt.authorship.detection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AuthorshipDetectorImpl implements AuthorshipDetector {

    private static final String PUNCTUATION_REGEX = "[^!.,:;\\-?<>#*'\"\\[(\\])\\n\\t\\\\]";

    private double[] weights;
    private Map<String, LinguisticSignature> signaturesDataset;

    public AuthorshipDetectorImpl(InputStream signaturesDataset, double[] weights) {
        this.weights = weights;
        this.signaturesDataset = loadSignaturesDataset(signaturesDataset);
    }

    @Override
    public LinguisticSignature calculateSignature(InputStream mysteryText) {
        validate(mysteryText);

        Map<FeatureType, Double> features = new EnumMap<>(FeatureType.class);

        String text = parseText(mysteryText);

        List<String> tokens = getTokens(text);
        List<String> sentences = getSentences(text);
        List<String> phrases = getPhrases(sentences);
        List<String> words = getWords(tokens);

        features.put(FeatureType.HAPAX_LEGOMENA_RATIO, getHapaxLegomenaRatio(words));
        features.put(FeatureType.AVERAGE_WORD_LENGTH, getAverageWordLength(words));
        features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY,
                getAverageSentenceComplexity(phrases.size(), sentences.size()));
        features.put(FeatureType.AVERAGE_SENTENCE_LENGTH,
                getAverageSentenceLength(words.size(), sentences.size()));
        features.put(FeatureType.TYPE_TOKEN_RATIO, getTypeTokenRatio(words));

        return new LinguisticSignature(features);
    }

    private String parseText(InputStream mysteryText) {
        String text = null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mysteryText))) {
            text = bufferedReader.lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    private List<String> getTokens(String text) {
        return Arrays.asList(text.split("\\s+"));
    }

    private List<String> getPhrases(List<String> sentences) {
        List<String> phrases = new ArrayList<>();

        sentences.forEach(sentence -> {
            String[] split = sentence.replaceAll("[\\n\\t]", "").split("[,:;]");
            if (split.length > 1) {
                phrases.addAll(Arrays.stream(split)
                        .filter(((Predicate<String>) String::isEmpty).negate())
                        .collect(Collectors.toList()));
            }
        });
        return phrases;
    }

    private List<String> getSentences(String text) {
        return Arrays.stream(text.split("[.!?]"))
                .map(s -> s = s.trim())
                .filter(((Predicate<String>) String::isEmpty).negate())
                .collect(Collectors.toList());
    }

    private List<String> getWords(List<String> tokens) {
        List<String> words = new ArrayList<>();
        tokens.forEach(token -> {
            if (token.matches(".*" + PUNCTUATION_REGEX + "+.*")) {
                words.add(token);
            }
        });
        return words;
    }

    private double getAverageWordLength(List<String> words) {
        List<String> wordsCleaned = words.stream()
                .map(AuthorshipDetectorImpl::cleanUp)
                .collect(Collectors.toList());

        int letterCount = wordsCleaned.stream().mapToInt(String::length).sum();
        int wordCount = wordsCleaned.size();
        return (double) letterCount / wordCount;
    }

    private static String cleanUp(String word) {
        return word.toLowerCase().replaceAll("^" + PUNCTUATION_REGEX + "$", "");
    }

    private double getTypeTokenRatio(List<String> words) {
        long count = words.stream().map(String::toLowerCase).distinct().count();
        return (double) count / words.size();
    }

    private double getHapaxLegomenaRatio(List<String> words) {
        Map<String, Long> collect = words.stream()
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        long count = collect.entrySet()
                .stream()
                .filter(stringLongEntry -> stringLongEntry.getValue() == 1)
                .count();

        return (double) count / words.size();
    }

    private double getAverageSentenceLength(int wordSize, int sentenceCount) {
        return (double) wordSize / sentenceCount;
    }

    private double getAverageSentenceComplexity(int phrasesCount, int sentenceCount) {
        return (double) phrasesCount / sentenceCount;
    }

    @Override
    public double calculateSimilarity(LinguisticSignature firstSignature,
                                      LinguisticSignature secondSignature) {
        validate(firstSignature);
        validate(secondSignature);

        Map<FeatureType, Double> firstSignatureFeatures = firstSignature.getFeatures();
        Map<FeatureType, Double> secondSignatureFeatures = secondSignature.getFeatures();
        double similaritySum = 0;

        for (FeatureType featureType : FeatureType.values()) {
            Double firstSignatureValue = firstSignatureFeatures.get(featureType);
            Double secondSignatureValue = secondSignatureFeatures.get(featureType);

            similaritySum += (Math.abs(firstSignatureValue - secondSignatureValue)
                    * weights[featureType.ordinal()]);
        }
        return similaritySum;
    }

    @Override
    public String findAuthor(InputStream mysteryText) {
        validate(mysteryText);

        String author = null;
        double minimumSimilarity = Double.MAX_VALUE;

        LinguisticSignature textLinguisticSignature = calculateSignature(mysteryText);

        for (Map.Entry<String, LinguisticSignature> entry : signaturesDataset.entrySet()) {
            double similarity = calculateSimilarity(textLinguisticSignature, entry.getValue());
            if (Double.compare(similarity, minimumSimilarity) < 0) {
                minimumSimilarity = similarity;
                author = entry.getKey();
            }
        }
        return author;
    }

    private Map<String, LinguisticSignature> loadSignaturesDataset(InputStream signaturesDataset) {
        Map<String, LinguisticSignature> authorSignatures = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(signaturesDataset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                String[] features = Arrays.copyOfRange(tokens, 1, tokens.length);

                authorSignatures.put(tokens[0], buildLinguisticSignature(features));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authorSignatures;
    }

    private LinguisticSignature buildLinguisticSignature(String[] signatures) {
        Map<FeatureType, Double> features = new EnumMap<>(FeatureType.class);
        for (FeatureType featureType : FeatureType.values()) {
            features.put(featureType, Double.parseDouble(signatures[featureType.ordinal()]));
        }
        return new LinguisticSignature(features);
    }

    private void validate(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }
}
