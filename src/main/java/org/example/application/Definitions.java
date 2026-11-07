package org.example.application;

public final class Definitions {
    private Definitions() {}

    public record Point(int x, int y) {}

    public enum Cardinal {
        NORTH,
        SOUTH,
        EAST,
        WEST;

        public char asChar()  {
            return switch (this) {
                case NORTH -> 'N';
                case SOUTH -> 'S';
                case EAST -> 'E';
                case WEST -> 'W';
            };
        }
    }

    public enum Instruction {
        LEFT,
        RIGHT,
        FORWARD,
    }
}
