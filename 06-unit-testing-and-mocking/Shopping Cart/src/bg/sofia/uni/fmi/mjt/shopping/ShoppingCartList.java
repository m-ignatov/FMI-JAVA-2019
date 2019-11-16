package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.exceptions.ItemNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.*;

public class ShoppingCartList implements ShoppingCart {

    private List<Item> items;

    public ShoppingCartList() {
        this.items = new ArrayList<>();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return new HashSet<>(items);
    }

    @Override
    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    @Override
    public void removeItem(Item item) throws ItemNotFoundException {
        if (item != null) {
            if (!items.contains(item)) {
                throw new ItemNotFoundException();
            }
            items.remove(item);
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Map<Item, Integer> temp = getMap();

        Map<Item, Integer> itemsMap =
                new TreeMap<>((o1, o2) -> temp.get(o2).compareTo(temp.get(o1)));
        itemsMap.putAll(temp);
        return itemsMap.keySet();
    }

    private Map<Item, Integer> getMap() {
        Map<Item, Integer> itemsMap = new HashMap<>();
        for (Item item : items) {
            Integer count = itemsMap.get(item);
            itemsMap.put(item, (count == null) ? 1 : count + 1);
        }
        return itemsMap;
    }
}

