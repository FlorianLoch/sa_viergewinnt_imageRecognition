package de.dhbw.mbfl.imagedetection.ImagePartitioning;

import de.dhbw.mbfl.imagedetection.ImageAnalysisException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by florian on 20.02.15.
 */
public class PartitionedImage implements Iterable<ImagePartition> {
    private ArrayList<ImagePartition> partitions;

    public PartitionedImage() {
        this.partitions = new ArrayList<ImagePartition>();
    }

    private PartitionedImage(ArrayList<ImagePartition> partitions) {
        this.partitions = partitions;
    }

    public boolean add(ImagePartition partition) {
        return this.partitions.add(partition);
    }

    public ImagePartition get(int index) {
        return this.partitions.get(index);
    }

    public int size() {
        return this.partitions.size();
    }

    @Override
    public Iterator<ImagePartition> iterator() {
        return this.partitions.iterator();
    }

    public ImagePartition remove(int index) {
        return partitions.remove(index);
    }

    public boolean remove(Object o) {
        return partitions.remove(o);
    }

    public void filterForNBiggestPartitions(int n) {
        int delta = this.size() - n;

        if (delta <= 0) return;

        while (delta > 0) {
            removeSmallestPartition();
            delta--;
        }
    }

    /**
     *
     * @param acceptedDeviationPerc Value between 0-1.0
     */
    public void filterByDeviationFromMedian(double acceptedDeviationPerc) {
        int median = this.getSizeOfMedianElement();
        double acceptedDeviation = median * acceptedDeviationPerc;

        int avgSize = this.getSizeOfMedianElement();

        ArrayList<ImagePartition> filteredPartitions = new ArrayList<ImagePartition>();

        for (ImagePartition partition : this.partitions) {
            double deviation = Math.abs(avgSize - partition.size());
            if (deviation <= acceptedDeviation) {
                filteredPartitions.add(partition);
            }
        }

        this.partitions = filteredPartitions;
    }

    public int getSizeOfMedianElement() {
        PartitionedImage sortedClone = this.sortPartitionsBySize();

        int medianIndex = (int) Math.floor(sortedClone.size() / 2);

        return sortedClone.get(medianIndex).size();
    }

    private void removeSmallestPartition() {
        ImagePartition smallest = this.getSmallesPartition();
        this.remove(smallest);
    }

    public ImagePartition getSmallesPartition() {
        ImagePartition smallest = Collections.min(this.partitions, new Comparator<ImagePartition>() {
            @Override
            public int compare(ImagePartition o1, ImagePartition o2) {
                if (o1.size() < o2.size()) return -1;
                if (o1.size() == o2.size()) return 0;
                return 1;
            }
        });

        return smallest;
    }

    /**
     * Sorts the partitions in a way that the left-bottom positioned
     * partition is first in the list and the one positioned top-right is the last one
     */
    public PartitionedImage sortPartitionsAccordingToBoard(int expectedPartitionsPerRow) throws ImageAnalysisException {
        if (this.size() % expectedPartitionsPerRow != 0) {
            throw new ImageAnalysisException("The amount of found partitions is not a multiple of " + expectedPartitionsPerRow);
        }

        PartitionedImage result = new PartitionedImage();
        PartitionedImage clone = new PartitionedImage(this.partitions); //This is needed so the order of this.partitions of the orignal instance does not get changed

        Collections.sort(clone.partitions, new Comparator<ImagePartition>() {
            @Override
            public int compare(ImagePartition o1, ImagePartition o2) {
                int y1 = o1.getCenter().y;
                int y2 = o2.getCenter().y;

                if (y1 > y2) return -1;
                if (y1 == y2) return 0;
                return +1;
            }
        });

        //Now sort in groups of expectedPartitionsPerRow
        for (int i = 0; i < this.size(); i = i + expectedPartitionsPerRow) {
            ArrayList<ImagePartition> row = new ArrayList<ImagePartition>();

            for (int j = i; j < i + expectedPartitionsPerRow; j++) {
                row.add(clone.get(j));
            }

            Collections.sort(row, new Comparator<ImagePartition>() {
                @Override
                public int compare(ImagePartition o1, ImagePartition o2) {
                    int x1 = o1.getCenter().x;
                    int x2 = o2.getCenter().x;

                    if (x1 < x2) return -1;
                    if (x1 == x2) return 0;
                    return +1;
                }
            });

            for (ImagePartition part : row) {
                result.add(part);
            }
        }

        return result;
    }

    public PartitionedImage sortPartitionsBySize() {
        PartitionedImage clone = new PartitionedImage(this.partitions);

        Collections.sort(clone.partitions, new Comparator<ImagePartition>() {
            @Override
            public int compare(ImagePartition o1, ImagePartition o2) {
                if (o1.size() < o2.size()) return -1;
                if (o1.size() == o2.size()) return 0;
                return +1;
            }
        });

        return clone;
    }
}
