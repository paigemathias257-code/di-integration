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
        ArrayList<String> intersect = new ArrayList<String>();
        int tokenLength = 0;
        /*for (int a = 0; a < strings1[0].length(); a++){
           tokenLength++;
        }*/
        int union = (strings1.length*this.tokenizer.getTokenSize()) + (strings2.length*this.tokenizer.getTokenSize());
        if (bagSemantics) {
            for (int i = 0; i < strings1.length; i++) {
                for (int j = 0; j < strings2.length; j++) {
                    if (strings1[i].equals(strings2[j])) {
                        intersect.add(strings1[i]);
                    }
                }
            }
            jaccardSimilarity = (double) intersect.size()/union;

        } else {
            Set<String> temp1 = new HashSet<String>(Arrays.asList(strings1));
            Set<String> temp2 = new HashSet<String>(Arrays.asList(strings2));
            temp1.retainAll(temp2);

            String[] result = temp1.toArray(new String[temp1.size()]);


            jaccardSimilarity = (double) result.length/union;

        }


        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return jaccardSimilarity;
    }
}
