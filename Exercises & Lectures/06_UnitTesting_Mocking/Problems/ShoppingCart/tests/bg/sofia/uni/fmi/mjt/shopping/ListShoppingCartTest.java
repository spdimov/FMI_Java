package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ListShoppingCartTest {

    @Test
    public void testAddItemNotNull() {
        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });

        Item itemOne = new Apple("apple");
        Item itemTwo = new Chocolate("chocolate");

        cart.addItem(itemOne);
        cart.addItem(itemTwo);

        assertTrue(cart.items.contains(itemOne) && cart.items.contains(itemTwo));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemNull() {
        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });

        Item item = null;
        cart.addItem(item);
    }

    @Test
    public void testRemoveItemAdded() {
        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });

        Item item = new Apple("apple");
        cart.addItem(item);
        cart.removeItem(item);

        assertFalse(cart.items.contains(item));
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemNotAdded() {
        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });

        Item item = new Apple("apple");
        cart.removeItem(item);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemNull() {
        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });

        Item item = null;
        cart.removeItem(item);
    }


    @Test
    public void testGetTotalNoItems() {

        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });
        assertEquals(cart.getTotal(), 0.0, 0.01);
    }

    @Test
    public void testGetTotalCorrectList() {

        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                if (id.equals("apple")) {
                    return new ProductInfo("test", "test", 1);
                } else {
                    return new ProductInfo("test", "test", 2);
                }
            }
        });

        Item itemOne = new Apple("apple");
        Item itemTwo = new Chocolate("chocolate");

        cart.addItem(itemOne);
        cart.addItem(itemOne);
        cart.addItem(itemOne);
        cart.addItem(itemTwo);

        assertEquals(5, cart.getTotal(), 0.01);
    }

    @Test
    public void testGetUniqueItems() {
        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });

        final Item itemOne = new Apple("apple");
        final Item itemTwo = new Chocolate("chocolate");
        final Item itemThree = new Apple("apple");
        final Item itemFour = new Chocolate("chocolate");

        cart.addItem(itemOne);
        cart.addItem(itemTwo);
        cart.addItem(itemThree);
        cart.addItem(itemFour);

        Set<Item> expectedResult = new HashSet<>();
        expectedResult.add(itemOne);
        expectedResult.add(itemTwo);

        assertEquals(expectedResult.size(), cart.getUniqueItems().size());
        assertTrue(expectedResult.containsAll(cart.getUniqueItems()));
        assertTrue(cart.getUniqueItems().containsAll(expectedResult));
    }

    @Test
    public void testGetUniqueItemsEmpty() {
        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });

        Set<Item> expectedResult = new HashSet<>();

        assertEquals(expectedResult.size(), cart.getUniqueItems().size());
        assertTrue(expectedResult.containsAll(cart.getUniqueItems()));
        assertTrue(cart.getUniqueItems().containsAll(expectedResult));
    }

    @Test
    public void testGetSortedItems() {
        ListShoppingCart cart = new ListShoppingCart(new ProductCatalog() {
            @Override
            public ProductInfo getProductInfo(String id) {
                return null;
            }
        });

        final Item itemOne = new Apple("apple");
        final Item itemTwo = new Apple("apple");
        final Item itemThree = new Apple("apple");
        final Item itemFour = new Chocolate("chocolate");
        final Item itemFive = new Apple("green apple");
        final Item itemSix = new Apple("green apple");

        cart.addItem(itemOne);
        cart.addItem(itemTwo);
        cart.addItem(itemThree);
        cart.addItem(itemFour);
        cart.addItem(itemFive);
        cart.addItem(itemSix);
        cart.addItem(itemFour);

        Set<Item> expectedResult = new LinkedHashSet<>();
        expectedResult.add(itemOne);
        expectedResult.add(itemFive);
        expectedResult.add(itemFour);

        assertEquals(expectedResult, cart.getSortedItems());

    }


}



