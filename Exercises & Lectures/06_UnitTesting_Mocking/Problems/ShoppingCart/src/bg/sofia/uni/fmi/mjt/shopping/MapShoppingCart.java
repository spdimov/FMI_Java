package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MapShoppingCart implements ShoppingCart {

    public Map<Item, Integer> items;
    public ProductCatalog catalog;

    public MapShoppingCart(ProductCatalog catalog) {
        items = new HashMap<>();
        this.catalog = catalog;
    }

    public Collection<Item> getUniqueItems() {
        Collection<Item> i = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            i.add(entry.getKey());
        }
        return i;
    }

    @Override
    public void addItem(Item item) {
        if (item != null) {
            if (items.containsKey(item)) {
                Integer i = items.get(item);
                if (i == null) {
                    items.put(item, 0);
                } else {
                    items.put(item, ++i);
                }
            } else {
                items.put(item, 1);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (items.containsKey(item)) {
            Integer occurrences = items.get(item);
            if (--occurrences == 0) {
                items.remove(item);
            } else {
                items.put(item, occurrences - 1);
            }
        } else {
            throw new ItemNotFoundException();
        }
    }

    @Override
    public double getTotal() {
        int total = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            ProductInfo info = catalog.getProductInfo(entry.getKey().getId());
            total += info.price() * entry.getValue();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Set<Item> sortedItems = new TreeSet<Item>(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1.equals(o2)) {
                    return 0;
                } else if (items.get(o1).compareTo(items.get(o2)) >= 0) {
                    return 1;
                } else if (items.get(o1).compareTo(items.get(o2)) < 0) {
                    return -1;
                }
                return 0;
            }
        });

        sortedItems.addAll(items.keySet());

        return sortedItems;
    }

}
