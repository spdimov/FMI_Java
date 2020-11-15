package bg.sofia.uni.fmi.mjt.warehouse;

import bg.sofia.uni.fmi.mjt.warehouse.exceptions.CapacityExceededException;
import bg.sofia.uni.fmi.mjt.warehouse.exceptions.ParcelNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MJTExpressWarehouse<L, P> implements DeliveryServiceWarehouse<L, P> {

    private final int capacity;
    private final int retentionPeriod;
    private Map<L, P> labelToParcel;
    private Map<L, LocalDateTime> labelToTimeSubmitted;

    public MJTExpressWarehouse(int capacity, int retentionPeriod) {
        this.capacity = capacity;
        this.retentionPeriod = retentionPeriod;
        labelToParcel = new HashMap<>();
        labelToTimeSubmitted = new HashMap<>();
    }

    @Override
    public void submitParcel(L label, P parcel, LocalDateTime submissionDate) throws CapacityExceededException {

        if (label == null || parcel == null || submissionDate == null) {
            throw new IllegalArgumentException("Null value as argument");
        } else if (submissionDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException();
        }

        if (labelToParcel.size() >= capacity) {
            removeExpiredRetentionTimeItems();
            if(labelToParcel.size() >= capacity){
                throw new CapacityExceededException("Full capacity");
            }
        }
        labelToParcel.put(label, parcel);
        labelToTimeSubmitted.put(label, submissionDate);
    }

    @Override
    public P getParcel(L label) {
        if (label == null) {
            throw new IllegalArgumentException("null argument");
        }

        return labelToParcel.get(label);
    }

    @Override
    public P deliverParcel(L label) throws ParcelNotFoundException {
        if (label == null) {
            throw new IllegalArgumentException("null argument");
        }

        P deliveredItem = labelToParcel.get(label);
        if (deliveredItem == null) {
            throw new ParcelNotFoundException("Parcel not found");
        }

        labelToParcel.remove(label);
        labelToTimeSubmitted.remove(label);
        return deliveredItem;
    }

    @Override
    public double getWarehouseSpaceLeft() {
        double spaceLeft = (double) labelToParcel.size() / capacity;
        return Math.round((1.0-spaceLeft) * 100) / 100f;
    }

    @Override
    public Map<L, P> getWarehouseItems() {
        return labelToParcel;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedBefore(LocalDateTime before) {
        if (before == null) {
            throw new IllegalArgumentException();
        }

        Map<L, P> deletedItems = new HashMap<>();

        for (Map.Entry<L, LocalDateTime> labelToTimeSubmittedElement : labelToTimeSubmitted.entrySet()) {
            if (labelToTimeSubmittedElement.getValue().isBefore(before)) {
                L deletedItemLabel = labelToTimeSubmittedElement.getKey();
                deletedItems.put(deletedItemLabel, labelToParcel.get(deletedItemLabel));
                labelToParcel.remove(deletedItemLabel);
            }
        }

        for (Map.Entry<L, P> deletedItem : deletedItems.entrySet()) {
            labelToTimeSubmitted.remove(deletedItem.getKey());
        }

        return deletedItems;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedAfter(LocalDateTime after) {
        if (after == null) {
            throw new IllegalArgumentException();
        }

        Map<L, P> deletedItems = new HashMap<>();

        for (Map.Entry<L, LocalDateTime> labelToTimeSubmittedElement : labelToTimeSubmitted.entrySet()) {
            if (labelToTimeSubmittedElement.getValue().isAfter(after)) {
                L deletedItemLabel = labelToTimeSubmittedElement.getKey();
                deletedItems.put(deletedItemLabel, labelToParcel.get(deletedItemLabel));
                labelToParcel.remove(deletedItemLabel);
            }
        }

        for (Map.Entry<L, P> deletedItem : deletedItems.entrySet()) {
            labelToTimeSubmitted.remove(deletedItem.getKey());
        }

        return deletedItems;
    }

    private void removeExpiredRetentionTimeItems(){
        Set<L> removedLabels= new HashSet<>();

        for (Map.Entry<L,LocalDateTime> labelToTimeSubmittedElement: labelToTimeSubmitted.entrySet()) {
            long daysBetween = ChronoUnit.DAYS.between(labelToTimeSubmittedElement.getValue(),LocalDateTime.now());
            if((long) retentionPeriod < daysBetween){
                removedLabels.add(labelToTimeSubmittedElement.getKey());
                labelToParcel.remove(labelToTimeSubmittedElement.getKey());
            }
        }
        for(L removedLabel:removedLabels){
            labelToTimeSubmitted.remove(removedLabel);
        }
    }
}

