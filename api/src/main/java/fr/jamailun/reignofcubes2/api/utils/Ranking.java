package fr.jamailun.reignofcubes2.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public final class Ranking<T> {

    private final Function<T, Integer> scoreGetter;
    private final List<Entry> ranking = new ArrayList<>();

    public Ranking(Function<T, Integer> scoreGetter) {
        this.scoreGetter = scoreGetter;
    }

    @SafeVarargs
    public final <X extends T> void update(X... elements) {
        for(X t : elements)
            updateMono(t);
        Collections.sort(ranking);
    }
    public final <X extends T> void update(Collection<X> collection) {
        for(X t : collection)
            updateMono(t);
        Collections.sort(ranking);
    }

    private void updateMono(T element) {
        Optional<Entry> entry = find(element);
        if(entry.isEmpty()) {
            add(element);
            return;
        }
        entry.get().updateScore();
    }

    public void add(T element) {
        Entry entry = new Entry(element, scoreGetter.apply(element));
        ranking.add(entry);
        Collections.sort(ranking);
    }

    public void remove(T element) {
        ranking.removeIf(e -> e.equals(element));
    }

    public int size() {
        return ranking.size();
    }

    public void clear() {
        ranking.clear();
    }

    public Optional<T> getElementAtRank(int index) {
        if(index < 0 || index >= size())
            return Optional.empty();
        return Optional.of(ranking.get(index).element);
    }

    public Optional<Integer> getRankOf(T elem) {
        for(int i = 0; i < ranking.size(); i++) {
            if(ranking.get(i).element.equals(elem))
                return Optional.of(i);
        }
        return Optional.empty();
    }

    private Optional<Entry> find(T element) {
        return ranking.stream()
                .filter(e -> e.element.equals(element))
                .findFirst();
    }

    private class Entry implements Comparable<Entry> {
        private final T element;
        private int score;
        private Entry(T element, int score) {
            this.element = element;
            this.score = score;
        }

        @Override
        public int compareTo(@NotNull Ranking<T>.Entry other) {
            return other.score - score;
        }

        private void updateScore() {
            score = scoreGetter.apply(element);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) return false;
            if(obj == this) return true;
            if(obj instanceof Ranking<?>.Entry en) {
                return Objects.equals(en.element, element);
            }
            return false;
        }
    }

}
