package fr.jamailun.reignofcubes2.utils;

import java.util.*;

public class RandomBag<T> {

    private final Random random = new Random();
    private final List<Entry<T>> bag = new ArrayList<>();
    private double changeSum = 0;

    public void add(T t, double chance) {
        if(chance <= 0)
            return;
        bag.add(new Entry<>(t, chance));
        changeSum += chance;
    }

    public void clear() {
        bag.clear();
        changeSum = 0;
    }

    public T next() {
        if(bag.isEmpty())
            return null;
        double rand = random.nextDouble(0, changeSum);

        int index = 0;
        double sum = 0;
        do {
            sum += bag.get(index).chance();
            if(sum >= rand)
                return bag.get(index).elem();
            index++;
        }while(true);
    }

    private record Entry<T>(T elem, double chance) {}

}
