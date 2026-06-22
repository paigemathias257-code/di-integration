package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.AttributeList;
import de.di.data_profiling.structures.IND;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.w3c.dom.Attr;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class INDProfiler {

    /**
     * Discovers all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     *
     * @param relations The relations that should be profiled for inclusion dependencies.
     * @return The list of all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     */
    public List<IND> profile(List<Relation> relations, boolean discoverNary) {
        List<IND> inclusionDependencies = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all inclusion dependencies and return them in inclusion dependencies list. The boolean flag       //
        // discoverNary indicates, whether only unary or both unary and n-ary INDs should be discovered. To solve     //
        // this assignment, only unary INDs need to be discovered. Discovering also n-ary INDs is not optional.       //

        Map<String, Set<String>> database = new HashMap<>();            // convert all columns into map

        for (Relation relation : relations) {                           // access each table
            int attributeCount = 0;                                     // keep track of columns for attribute label
            for (String[] column : relation.getColumns()) {             // access columns
                List<String> columnAsList = Arrays.asList(column);      // convert column to a list (usable form to convert to set)
                Set<String> columnAsSet = new HashSet<>(columnAsList);  // convert list to set to remove duplicates
                // add converted column set and key each column as "table-attribute"
                database.put(relation.getName() + "-" + relation.getAttributes()[attributeCount], columnAsSet);
                attributeCount++;   // increment the count to be correct attribute for key
            }
        }

        for (String colA : database.keySet()) {             // get key of column A
            for (String colB : database.keySet()) {         // get key of column B
                if (!colA.equals(colB)) {                   //  exclude INDs within tables
                    // create sets of column elements by accessing column by key
                    Set<String> setA = database.get(colA);
                    Set<String> setB = database.get(colB);

                    // see if all elements in B are in A
                    if (setB.containsAll(setA)) {
                        // this was somehow to easiest way I could find to get the root relations
                        String[] aNameSplit = colA.split("-");    // get name of relation table by splitting key
                        String[] bNameSplit = colB.split("-");
                        List<Relation> relationA = new ArrayList<>();   // make a list to hold the proper relation
                        List<Relation> relationB = new ArrayList<>();

                        // iterate through the relations until a matching name is found
                        for (Relation relation : relations) {
                            if (relation.getName().equals(aNameSplit[0])) relationA.add(relation);
                        }
                        // repeat for column B
                        for (Relation relation : relations) {
                            if (relation.getName().equals(bNameSplit[0])) relationB.add(relation);
                        }

                        // find attribute
                        int attributeA = 0;
                        int attributeB = 0;

                        // iterate through attributes of relation to find proper attribute for column
                        for (String columnName : relationA.get(0).getAttributes()) {
                            if (columnName.equals(aNameSplit[1])) break;
                            attributeA++;
                        }
                        // repeat for column B
                        for (String columnName : relationB.get(0).getAttributes()) {
                            if (columnName.equals(bNameSplit[1])) break;
                            attributeB++;
                        }

                        // create IND and add to list
                        IND newIND = new IND(relationB.get(0), attributeB, relationA.get(0), attributeA);
                        inclusionDependencies.add((newIND));
                    }
                }
            }
        }
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (discoverNary)
                // Here, the lattice search would start if n-ary IND discovery would be supported.
                throw new RuntimeException("Sorry, n-ary IND discovery is not supported by this solution.");
            // less n-ary INDs than unary?

            return inclusionDependencies;
        }

        private List<Set<String>> toColumnSets(String[][] columns){
            return Arrays.stream(columns)
                    .map(column -> new HashSet<>(new ArrayList<>(List.of(column))))
                    .collect(Collectors.toList());
        }
    }

