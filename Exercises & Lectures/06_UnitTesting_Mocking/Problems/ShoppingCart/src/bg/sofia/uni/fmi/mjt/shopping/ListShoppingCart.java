package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class ListShoppingCart implements ShoppingCart {

    ArrayList<Item> items;
    ProductCatalog catalog;

    public ListShoppingCart(ProductCatalog catalog) {
        this.catalog = catalog;
        this.items = new ArrayList<>();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return new TreeSet<>(items);
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Null argument");
        }

        items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Null argument");
        }

        if (!items.contains(item)) {
            throw new ItemNotFoundException();
        }

        items.remove(item);
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            ProductInfo info = catalog.getProductInfo(item.getId());
            total += info.price();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Map<Item, Integer> itemToQuantity = create_map();
        Map<Item, Integer> sortedItems = new TreeMap<>(new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                if (item1.equals(item2)) {
                    return 0;
                } else if (itemToQuantity.get(item1).compareTo(itemToQuantity.get(item2)) >= 0) {
                    return 1;
                } else if (itemToQuantity.get(item1).compareTo(itemToQuantity.get(item2)) < 0) {
                    return -1;
                }
                return 0;
            }
        });
        sortedItems.putAll(itemToQuantity);
        return sortedItems.keySet();
    }

    public Map<Item, Integer> create_map() {
        HashMap<Item, Integer> itemToQuantity = new HashMap<Item, Integer>();
        for (Item item : items) {
            boolean condition = itemToQuantity.containsKey(item);
            itemToQuantity.put(item, condition ? itemToQuantity.get(item) + 1 : 1);
        }
        return itemToQuantity;
    }
}

