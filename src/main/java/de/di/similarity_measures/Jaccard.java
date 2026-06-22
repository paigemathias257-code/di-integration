package de.di.similarity_measures;

import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class Jaccard implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
    private final boolean bagSemantics;

    /**
     * Calculates the Jaccard similarity of the two input strings. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String string1, String string2) {
        string1 = (string1 == null) ? "" : string1;
        string2 = (string2 == null) ? "" : string2;

        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the Jaccard similarity of the two string lists. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String[] strings1, String[] strings2) {
        double jaccardSimilarity = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Calculate the Jaccard similarity of the two String arrays. Note that the Jaccard similarity needs to be    //
        // calculated differently depending on the token semantics: set semantics remove duplicates while bag         //
        // semantics consider them during the calculation. The solution should be able to calculate the Jaccard       //
        // similarity either of the two semantics by respecting the inner bagSemantics flag.                          //


        if (bagSemantics) {
            ArrayList<String> intersect = new ArrayList<>(); // initialize intersect

            // build a frequency map for the first string array
            Map<String, Integer> counts = new HashMap<>();
            for (String str : strings1) {   // increments through first string
                // only adds a string if not null
                if (str != null) {
                    // adds string to map and increments its frequency (or add one to zero if not already present)
                    counts.put(str, counts.getOrDefault(str, 0) + 1);
                }
            }

            // track matching strings inside a dynamic list
            for (String str : strings2) {
                // finds if string is in map and has a frequency higher than 0
                if (str != null && counts.containsKey(str) && counts.get(str) > 0) {
                    intersect.add(str); // adds to intersect if in strings1 and frequency is greater than 0
                    // decrement the frequency count in the map so that if there are more instances of a token in one,
                    // it is not counted too many times
                    counts.put(str, counts.get(str) - 1);
                }
            }

            int union = strings1.length + strings2.length;  // calculate union
            jaccardSimilarity = (double) intersect.size()/union;

        } else {
            // convert to HashSet to remove duplicates
            Set<String> stringSet1 = new HashSet<>(Arrays.asList(strings1));
            Set<String> stringSet2 = new HashSet<>(Arrays.asList(strings2));
            // HashSet to contain the intersection
            Set<String> intersectSet = new HashSet<>(stringSet1);
            intersectSet.retainAll(stringSet2); // retains only the tokens in both

            // calculate the union
            int union = stringSet1.size() + stringSet2.size()- intersectSet.size();
            jaccardSimilarity = (double) intersectSet.size()/union;
        }
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return jaccardSimilarity;
    }
}
