package org.example.application;

public final class Definitions {
    private Definitions() {}

    public record Point(int x, int y) {}

    public enum Cardinal {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    public enum Instruction {
        LEFT,
        RIGHT,
        FORWARD,
    }
}
