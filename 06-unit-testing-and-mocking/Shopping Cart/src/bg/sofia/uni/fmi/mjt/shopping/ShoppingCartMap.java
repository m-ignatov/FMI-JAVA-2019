package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.exceptions.ItemNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.*;

public class ShoppingCartMap implements ShoppingCart {

    private Map<Item, Integer> items;

    public ShoppingCartMap() {
        this.items = new HashMap<>();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return items.keySet();
    }

    @Override
    public void addItem(Item item) {
        if (item != null) {
            Integer occurrences = items.get(item);
            items.put(item, (occurrences == null) ? 1 : occurrences + 1);
        }
    }

    @Override
    public void removeItem(Item item) throws ItemNotFoundException {
        if (item != null) {
            if (!items.containsKey(item)) {
                throw new ItemNotFoundException();
            }
            Integer occurrences = items.get(item);
            if (occurrences == 1) {
                items.remove(item);
            } else {
                items.put(item, occurrences - 1);
            }
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Item item : items.keySet()) {
            total += item.getPrice();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        List<Map.Entry<Item, Integer>> list = new ArrayList<>(items.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Collection<Item> result = new LinkedList<>();
        for (Map.Entry<Item, Integer> entry : list) {
            result.add(entry.getKey());
        }
        return result;
    }
}
