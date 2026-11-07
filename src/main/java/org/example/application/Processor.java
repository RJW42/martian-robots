package org.example.application;

import org.example.application.Definitions.Cardinal;
import org.example.application.Definitions.Instruction;
import org.example.application.Definitions.Point;

import java.util.List;

public class Processor {
    public record Output(
            Point point,
            Cardinal cardinal,
            boolean wasLost
    ) {}

    private final int width;
    private final int height;


    public Processor(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public Output processRobotInstruction(
            final Point startPoint,
            final Cardinal startCardinal,
            final List<Instruction> instructions
    ) {
        int currX = startPoint.x();
        int currY = startPoint.y();
        boolean wasLost = false;
        Cardinal currCardinal = startCardinal;

        for (final Instruction instruction : instructions) {
            switch (instruction) {
                case FORWARD -> {
                    final Point offset = getOffset(currCardinal);
                    final int newX = currX + offset.x();
                    final int newY = currY + offset.y();

                    if (newX < 0 || newX >= this.width || newY < 0 || newY >= this.height) {
                        wasLost = true;
                        break;
                    }

                    currX = newX;
                    currY = newY;
                }
                case LEFT -> currCardinal = rotateLeft(currCardinal);
                case RIGHT -> currCardinal = rotateRight(currCardinal);
            }
        }

        return new Output(
                new Point(currX, currY),
                currCardinal,
                wasLost
        );
    }

    private Point getOffset(final Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> new Point(0, 1);
            case SOUTH -> new Point(0, -1);
            case EAST -> new Point(1, 0);
            case WEST -> new Point(-1, 0);
        };
    }

    private Cardinal rotateLeft(final Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> Cardinal.WEST;
            case WEST -> Cardinal.SOUTH;
            case SOUTH -> Cardinal.EAST;
            case EAST -> Cardinal.NORTH;
        };
    }

    private Cardinal rotateRight(final Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> Cardinal.EAST;
            case EAST -> Cardinal.SOUTH;
            case SOUTH -> Cardinal.WEST;
            case WEST -> Cardinal.NORTH;
        };
    }
}
