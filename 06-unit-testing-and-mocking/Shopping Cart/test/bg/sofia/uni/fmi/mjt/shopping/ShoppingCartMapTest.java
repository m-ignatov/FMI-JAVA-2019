package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.exceptions.ItemNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShoppingCartMapTest {

    private static final String NAME = "itemName";
    private static final String DESCRIPTION = "itemDescription";
    private static final double PRICE = 10.00;
    private static final double DELTA = 0.001;

    private ShoppingCart shoppingCart;

    @Before
    public void setup() {
        shoppingCart = new ShoppingCartMap();
    }

    @After
    public void cleanup() {
        shoppingCart = null;
    }

    @Test
    public void givenNullItemWhenAddItemThenDoNotAddToCart() {
        shoppingCart.addItem(null);

        assertEquals(0, shoppingCart.getSortedItems().size());
    }

    @Test
    public void givenTwoIdenticalItemsWhenGetUniqueItemsThenReturnOneUniqueItem() {
        Item apple = new Apple(NAME, DESCRIPTION, PRICE);
        shoppingCart.addItem(apple);
        shoppingCart.addItem(apple);

        Collection<Item> uniqueItems = shoppingCart.getUniqueItems();

        assertEquals(1, uniqueItems.size());
        assertTrue(uniqueItems.contains(apple));
    }

    @Test
    public void givenItemWhenAddItemThenAddSuccessfully() {
        Item apple = new Apple(NAME, DESCRIPTION, PRICE);

        shoppingCart.addItem(apple);

        Collection<Item> sortedItems = shoppingCart.getSortedItems();
        assertEquals(1, sortedItems.size());
        assertTrue(sortedItems.contains(apple));
    }

    @Test(expected = ItemNotFoundException.class)
    public void givenEmptyCartWhenRemoveItemThenThrowItemNotFoundException() throws ItemNotFoundException {
        Item apple = new Apple(NAME, DESCRIPTION, PRICE);
        shoppingCart.removeItem(apple);
    }

    @Test
    public void givenItemInCartWhenRemoveItemThenRemoveSuccessfully() throws ItemNotFoundException {
        Item apple = new Apple(NAME, DESCRIPTION, PRICE);
        shoppingCart.addItem(apple);

        shoppingCart.removeItem(apple);

        assertEquals(0, shoppingCart.getSortedItems().size());
    }

    @Test
    public void givenTwoIdenticalItemsInCartWhenRemoveOneItemThenOneItemLeft() throws ItemNotFoundException {
        Item apple = new Apple(NAME, DESCRIPTION, PRICE);
        shoppingCart.addItem(apple);
        shoppingCart.addItem(apple);

        shoppingCart.removeItem(apple);

        Collection<Item> items = shoppingCart.getSortedItems();
        assertEquals(1, items.size());
    }

    @Test
    public void givenTwoItemsWhenGetTotalThenReturnCorrectTotalPrice() {
        final double chocolatePrice = 10;
        Item apple = new Apple(NAME, DESCRIPTION, PRICE);
        Item chocolate = new Chocolate("name2", "description2", chocolatePrice);
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);

        double totalPrice = shoppingCart.getTotal();

        assertEquals(chocolatePrice + PRICE, totalPrice, DELTA);
    }

    @Test
    public void givenThreeItemsWhenGetSortedItemsThenReturnCorrectSortedList() {
        final double chocolatePrice = 10;
        Item apple = new Apple(NAME, DESCRIPTION, PRICE);
        Item chocolate = new Chocolate("name2", "description2", chocolatePrice);
        shoppingCart.addItem(apple);
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);

        Object[] items = shoppingCart.getSortedItems().toArray();

        assertEquals(2, items.length);
        assertEquals(apple, items[0]);
        assertEquals(chocolate, items[1]);
    }
}
