package de.di.data_profiling.structures;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class PositionListIndex {

    private final AttributeList attributes;
    private final List<IntArrayList> clusters;
    private final int[] invertedClusters;

    public PositionListIndex(final AttributeList attributes, final String[] values) {
        this.attributes = attributes;
        this.clusters = this.calculateClusters(values);
        this.invertedClusters = this.calculateInverted(this.clusters, values.length);
    }

    public PositionListIndex(final AttributeList attributes, final List<IntArrayList> clusters, int relationLength) {
        this.attributes = attributes;
        this.clusters = clusters;
        this.invertedClusters = this.calculateInverted(this.clusters, relationLength);
    }

    private List<IntArrayList> calculateClusters(final String[] values) {
        Map<String, IntArrayList> invertedIndex = new HashMap<>(values.length);
        for (int recordIndex = 0; recordIndex < values.length; recordIndex++) {
            invertedIndex.putIfAbsent(values[recordIndex], new IntArrayList());
            invertedIndex.get(values[recordIndex]).add(recordIndex);
        }
        return invertedIndex.values().stream().filter(cluster -> cluster.size() > 1).collect(Collectors.toList());
    }

    private int[] calculateInverted(List<IntArrayList> clusters, int relationLength) {
        int[] invertedClusters = new int[relationLength];
        Arrays.fill(invertedClusters, -1);
        for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++)
            for (int recordIndex : clusters.get(clusterIndex))
                invertedClusters[recordIndex] = clusterIndex;
        return invertedClusters;
    }

    public boolean isUnique() {
        return this.clusters.isEmpty();
    }

    public int relationLength() {
        return this.invertedClusters.length;
    }

    public PositionListIndex intersect(PositionListIndex other) {
        List<IntArrayList> clustersIntersection = this.intersect(this.clusters, other.getInvertedClusters());
        AttributeList attributesUnion = this.attributes.union(other.getAttributes());

        return new PositionListIndex(attributesUnion, clustersIntersection, this.relationLength());
    }

    private List<IntArrayList> intersect(List<IntArrayList> clusters, int[] invertedClusters) {
        List<IntArrayList> clustersIntersection = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Calculate the intersection of one PLI's clusters and another PLI's (conveniently already inverted)         //
        // invertedClusters. The clustersIntersection is a new list that stores the intersection result. Note that    //
        // the clusters are "Stripped Partitions", which means that only clusters of size >1 are part of the result.  //

        String[] combo = new String[invertedClusters.length];
        for (int row = 0; row<invertedClusters.length;row++){
            for (int oci = 0; oci<clusters.size();oci++) { // outer cluster index
                for (int ici = 0; ici < clusters.get(oci).size(); ici++) { // inner cluster index
                    if (clusters.get(oci).get(ici).equals(row)){ //gets the probe value for a row
                        combo[row] = "("+clusters.indexOf(clusters.get(oci))+","+invertedClusters[row]+")"; // combines the values of row into a tuple
                    }
                }
            }
        }
        // creates a set of the possible combinations to be compares
        Set<String> possibilities = new LinkedHashSet<>(Arrays.asList(combo));
        String[] compare = possibilities.toArray(new String[0]);
        for (int i = 0; i<possibilities.size();i++) {
            IntArrayList temp = new IntArrayList();
            for (int j = 0; j < combo.length; j++) {
                if (compare[i] == null) {
                    break;

                } else if (combo[j] != null){
                    if (compare[i].equals(combo[j])) {
                        temp.add(j);
                    }
                }
            }
            if (temp.size() > 1) {
                clustersIntersection.add(temp);
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return clustersIntersection;
    }
}