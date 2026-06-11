package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.AttributeList;
import de.di.data_profiling.structures.PositionListIndex;
import de.di.data_profiling.structures.UCC;

import java.util.*;

public class UCCProfiler {

    /**
     * Discovers all minimal, non-trivial unique column combinations in the provided relation.
     * @param relation The relation that should be profiled for unique column combinations.
     * @return The list of all minimal, non-trivial unique column combinations in ths provided relation.
     */
    public List<UCC> profile(Relation relation) {
        int numAttributes = relation.getAttributes().length;
        List<UCC> uniques = new ArrayList<>();
        List<PositionListIndex> currentNonUniques = new ArrayList<>();

        // Calculate all unary UCCs and unary non-UCCs
        for (int attribute = 0; attribute < numAttributes; attribute++) {
            AttributeList attributes = new AttributeList(attribute);
            PositionListIndex pli = new PositionListIndex(attributes, relation.getColumns()[attribute]);
            if (pli.isUnique())
                uniques.add(new UCC(relation, attributes));
            else
                currentNonUniques.add(pli);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all unique column combinations of size n>1 by traversing the lattice level-wise. Make sure to     //
        // generate only minimal candidates while moving upwards and to prune non-minimal ones. Hint: The class       //
        // AttributeList offers some helpful functions to test for sub- and superset relationships. Use PLI           //
        // intersection to validate the candidates in every lattice level. Advances techniques, such as random walks, //
        // hybrid search strategies, or hitting set reasoning can be used, but are optional to pass the assignment.
        // find minimal combinations; apriori; (bottom-up) go up until an ucc is found, as higher will also be unique

        // combine the current non uniques with a for loop and check by pli if they are unique
        int level = 2; // start at item sets of size 2

        // run until no more Non Uniques (which I don't think is possible) or the sets are the largest possible size
        while (!currentNonUniques.isEmpty() && level <= numAttributes) {
            List<PositionListIndex> pliCombos = new ArrayList<>();  // holds the combined item sets
            // find the relevant combinations
            ///Problem area/////////////////////////////////////////
            for (int i = 0; i < currentNonUniques.size(); i++) {
                List<PositionListIndex> currentItemSet = new ArrayList<>();
                for (int j = i; j < currentNonUniques.size(); j++) {
                    //currentItemSet.add(currentNonUniques.get(i));
                    currentItemSet.add(currentNonUniques.get(j));
            ////////////////////////////////////////////////////////
                    // if the item set is correct size, continue to get the combined PLI
                    if (currentItemSet.size() == level) {
                        // combine initial 2 sets
                        PositionListIndex combinedPLI = currentItemSet.get(0).intersect(currentItemSet.get(1));
                        // combine any sets past the first 2
                        for (int p = 2; p < currentItemSet.size(); p++) {
                            // update the current combinedPLI with next item
                            combinedPLI = combinedPLI.intersect(currentItemSet.get(p));
                        }
                        // check if the new combination is already in the uniques list
                        if (!uniques.contains(new UCC(relation, combinedPLI.getAttributes())))
                            pliCombos.add(combinedPLI);

                    }
                }
            }

            // evaluate combinations; same as above
            for (PositionListIndex pli : pliCombos){
                if (pli.isUnique()) {
                    if (!uniques.contains(new UCC(relation, pli.getAttributes())))
                        uniques.add(new UCC(relation, pli.getAttributes()));
                }
                else {
                    if (!currentNonUniques.contains(pli))
                        currentNonUniques.add(pli);
                }
            }
            level++; // move to next lattice level
        }
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return uniques;
    }
}
