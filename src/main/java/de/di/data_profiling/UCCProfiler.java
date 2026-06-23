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

        // find the relevant combinations
        List<List<PositionListIndex>> lattice = new ArrayList<>(); // holds all possible combinations of PLIs 2-5
        for (int level = 2; level < currentNonUniques.size(); level++) {
            generatePLICombos(currentNonUniques,level,0,new ArrayList<>(),lattice);
        }

        // combine PLIs and test uniqueness
        for (List<PositionListIndex> current :lattice) {
            // combine PLI
            PositionListIndex combinedPLI = combinePLI(current.get(0).intersect(current.get(1)), current);
            // check if the new combination is already in the uniques list
            if (!uniques.contains(new UCC(relation, combinedPLI.getAttributes()))){
                // evaluate combinations;
                if (combinedPLI.isUnique()) {
                    testUniques(relation, combinedPLI, uniques);
                }}}
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return uniques;
    }

    /**
     * Create the list of PLIs to be combined and tested for uniqueness
     * @param set The original list of PLIs to be combined
     * @param level The current size of the PLI combos
     * @param startIdx The place in the set where the next PLI should be added from
     * @param current The current combo of PLIs being created
     * @param masterList The List of all the combos in the lattice
     */
    private void generatePLICombos(List<PositionListIndex> set, int level, int startIdx,
                                   List<PositionListIndex>current, List<List<PositionListIndex>> masterList){
        if (current.size() == level) {  // add combo to the lattice when the correct size
            masterList.add(new ArrayList<>(current));
            return;
        }

        // recursively adds each element and then deletes when proper size is reached
        for (int j = startIdx; j < set.size(); j++) {
            current.add(set.get(j));
            generatePLICombos(set,level,j+1,current,masterList);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Create a PLI combination from 3 or more PositionListIndexes
     * @param combinedPLI The original 2 PLIs already combined
     * @param current The list of PLIs to be combined
     * @return The final PLI combination of the list passed
     */
    private PositionListIndex combinePLI(PositionListIndex combinedPLI, List<PositionListIndex> current){
        // combine sets past first 2
        for (int p = 2; p < current.size(); p++) {
            // update the current combinedPLI with next item
            combinedPLI = combinedPLI.intersect(current.get(p));
        }
        return combinedPLI;
    }

    /**
     * Test if a UCC is minimal
     * @param relation The relation, so that a UCC object may be created
     * @param combinedPLI the PositionListIndex of the found UCC
     * @param uniques The list of UCCs already found
     */
    private void testUniques(Relation relation, PositionListIndex combinedPLI, List<UCC> uniques){
        Set<Integer> attributesSet = combinedPLI.getAttributes().getAttributeSet(); // get set of attributes
        List<Integer> attributeList = new ArrayList<>(attributesSet);               // convert to list
        Set<UCC> toTest = new HashSet<>();  // master list to hold all combos of attributes
        // create all combos of attributes within possible ucc from size 1 to attributeList.size()-1
        for (int a=1; a<attributeList.size();a++) {
            generateAttributeCombos(relation,attributeList,a,0,new ArrayList<>(),toTest);
        }

        Set<UCC> intersect = new HashSet<>(uniques);    // create a hashset of all already found UCC
        intersect.retainAll(toTest);                    // retain any UCCs in both
        if (intersect.isEmpty())                        // if nothing in intersect, UCC is a minimal UCC
            uniques.add(new UCC(relation, combinedPLI.getAttributes()));
    }

    /**
     * Create the list of attributes to test if a UCC is minimal
     * @param set The original list of attributes to be combined
     * @param level The current size of the combos
     * @param startIdx The place in the set where the next attribute should be added from
     * @param current The current combo of attributes being created
     * @param masterList The List of all the combos in the test list
     */
    private void generateAttributeCombos(Relation relation, List<Integer> set, int level, int startIdx,
                                         List<Integer> current, Set<UCC> masterList){
        if (current.size() == level) {
            // convert to int array for AttributeList
            int[] attArray = current.stream().mapToInt(Integer::intValue).toArray();
            AttributeList att = new AttributeList(attArray);    // create attribute list for UCC
            masterList.add(new UCC(relation, att));             // add UCC to test list
            return;
        }
        // recursively adds each element and then deletes when proper size is reached
        for (int j = startIdx; j < set.size(); j++) {
            current.add(set.get(j));
            generateAttributeCombos(relation,set,level,j+1,current,masterList);
            current.remove(current.size() - 1);
        }
    }
}
