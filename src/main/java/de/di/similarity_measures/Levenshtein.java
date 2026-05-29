package de.di.similarity_measures;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class Levenshtein implements SimilarityMeasure {

    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    // The choice of whether Levenshtein or DamerauLevenshtein should be calculated.
    private final boolean withDamerau;

    /**
     * Calculates the Levenshtein similarity of the two input strings.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The (Damerau) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String string1, final String string2) {
        double levenshteinSimilarity = 0;

        int[] upperupperLine = new int[string1.length() + 1];   // line for Demarau lookups
        int[] upperLine = new int[string1.length() + 1];        // line for regular Levenshtein lookups
        int[] lowerLine = new int[string1.length() + 1];        // line to be filled next by the algorithm

        // Fill the first line with the initial positions (= edits to generate string1 from nothing)
        for (int i = 0; i <= string1.length(); i++)
            upperLine[i] = i;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Use the three provided lines to successively calculate the Levenshtein matrix with the dynamic programming //
        // algorithm. Depending on whether the inner flag withDamerau is set, the Damerau extension rule should be    //
        // used during calculation or not. Hint: Implement the Levenshtein algorithm here first, then copy the code   //
        // to the String tuple function and adjust it a bit to work on the arrays - the algorithm is the same.        //
        // dealing with silly circumstances
        if (string1.isEmpty() && string2.isEmpty()) {
            levenshteinSimilarity = 1;
        }
        else if (string1.isEmpty() || string2.isEmpty()) {
            levenshteinSimilarity = 0;

        }else {
            if (withDamerau) {
                for (int i = 1; i <= string2.length(); i++) {
                    lowerLine[0] = i;
                    for (int j = 1; j <= string1.length(); j++) {
                        if (string1.charAt(j - 1) == (string2.charAt(i - 1))) {
                            lowerLine[j] = upperLine[j - 1];
                        } else {
                            // avoid using upperupperLine when not applicable
                            if (j >= 2 && i >= 2 && (string1.charAt(j - 1) == (string2.charAt(i - 2)) && (string1.charAt(j - 2) == (string2.charAt(i - 1))))) {
                                lowerLine[j] = 1 + Math.min(Math.min(upperLine[j], upperLine[j - 1]), Math.min(lowerLine[j - 1], upperupperLine[j - 2]));
                            } else {
                                lowerLine[j] = 1 + Math.min(Math.min(upperLine[j], upperLine[j - 1]), lowerLine[j - 1]);
                            }

                        }
                    }
                    upperupperLine = Arrays.copyOf(upperLine, upperLine.length);
                    upperLine = Arrays.copyOf(lowerLine, lowerLine.length);
                }
                levenshteinSimilarity = 1 - (double) lowerLine[lowerLine.length - 1] / Math.max(string1.length(), string2.length());
            } else {
                for (int i = 1; i <= string2.length(); i++) {
                    lowerLine[0] = i;
                    for (int j = 1; j <= string1.length(); j++) {
                        if (string1.charAt(j - 1) == (string2.charAt(i - 1))) {
                            lowerLine[j] = upperLine[j - 1];
                        } else {
                            int temp = Math.min(Math.min(upperLine[j], upperLine[j - 1]), lowerLine[j - 1]);
                            lowerLine[j] = temp + 1;
                        }
                    }
                    upperLine = Arrays.copyOf(lowerLine, lowerLine.length);
                }
                levenshteinSimilarity = 1 - (double) lowerLine[lowerLine.length - 1] / Math.max(string1.length(), string2.length());
            }
        }
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return levenshteinSimilarity;
    }

    /**
     * Calculates the Levenshtein similarity of the two input string lists.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * For string lists, we consider each list as an ordered list of tokens and calculate the distance as the number of
     * token insertions, deletions, replacements (and swaps) that transform one list into the other.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The (multiset) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        double levenshteinSimilarity = 0;

        int[] upperupperLine = new int[strings1.length + 1];   // line for Damerau lookups
        int[] upperLine = new int[strings1.length + 1];        // line for regular Levenshtein lookups
        int[] lowerLine = new int[strings1.length + 1];        // line to be filled next by the algorithm

        // Fill the first line with the initial positions (= edits to generate string1 from nothing)
        for (int i = 0; i <= strings1.length; i++)
            upperLine[i] = i;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Use the three provided lines to successively calculate the Levenshtein matrix with the dynamic programming //
        // algorithm. Depending on whether the inner flag withDamerau is set, the Damerau extension rule should be    //
        // used during calculation or not. Hint: Implement the Levenshtein algorithm above first, then copy the code  //
        // to this function and adjust it a bit to work on the arrays - the algorithm is the same.                    //
        if (withDamerau){   // calculation with Damerau-Levenshtein distance
            for (int i = 1; i <= strings1.length; i++) {        // iterate down through letters of first string array
                lowerLine[0] = i;                               // set first digit of lowerLine to pass #
                for (int j = 1; j <= strings2.length; j++) {    // iterate over through letter of second string
                    // choose diagonal value when letters match
                    if (strings1[i - 1].equals(strings2[j - 1])) {
                        lowerLine[j] = upperLine[j - 1];
                    // choose action when letters do not match
                    } else {
                        // avoid using upperupperLine when not applicable
                        if (j>=2 && i>= 2 && strings1[i - 1].equals(strings2[j - 2]) && strings1[i - 2].equals(strings2[j - 1])){
                            lowerLine[j] = 1 + Math.min(Math.min(upperLine[j], upperLine[j - 1]), Math.min(lowerLine[j - 1], upperupperLine[j - 2]));
                        } else {
                            lowerLine[j] = 1 + Math.min(Math.min(upperLine[j],upperLine[j-1]),lowerLine[j-1]);}
                    }
                }
                upperupperLine = Arrays.copyOf(upperLine, upperLine.length);    // move upperLine up
                upperLine = Arrays.copyOf(lowerLine, lowerLine.length);         // move lowerLine up
            }
            // calculate Levenshtein Similarity
            levenshteinSimilarity = 1 - (double) lowerLine[lowerLine.length - 1]/Math.max(strings1.length,strings2.length);
        }
        else {  // calculation without Damerau-Levenshtein distance
            for (int i = 1; i <= strings1.length; i++) {    // iterate down through letters of first string array
                lowerLine[0] = i;                           // set first digit of lowerLine to pass #
                for (int j = 1; j <= strings2.length; j++){ // iterate over through letter of second string
                    // choose diagonal value when letters match
                    if (strings1[i - 1].equals(strings2[j - 1])) {
                        lowerLine[j] = upperLine[j - 1];
                    // chose action when letters do not match
                    } else {
                        lowerLine[j] = 1 + Math.min(Math.min(upperLine[j], upperLine[j - 1]), lowerLine[j - 1]);
                    }
                }
                upperLine = Arrays.copyOf(lowerLine, lowerLine.length); // move lines up
            }
            // calculate Levenshtein Similarity
            levenshteinSimilarity = 1 - (double) lowerLine[lowerLine.length - 1]/Math.max(strings1.length,strings2.length);
        }
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return levenshteinSimilarity;
    }
}
