package fr.jamailun.reignofcubes2.api.utils;

import lombok.Getter;

@Getter
public final class MinMax {
    private final double min, max;
    public MinMax(double a, double b) {
        min = Math.min(a, b);
        max = Math.max(a, b);
    }
    public boolean contains(double value) {
        return min <= value && value <= max;
    }
}
