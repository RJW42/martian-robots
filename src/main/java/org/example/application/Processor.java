package org.example.application;

import org.example.application.Definitions.Cardinal;
import org.example.application.Definitions.Instruction;
import org.example.application.Definitions.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Processor {
    public record Output(
            Point point,
            Cardinal cardinal,
            boolean wasLost
    ) {}

    private final int width;
    private final int height;
    private final Set<Point> scents;


    public Processor(
            final int width,
            final int height
    ) {
        this.width = width;
        this.height = height;
        this.scents = new HashSet<>();
    }

    /**
     * Process all instructions against the given starting point and direction.
     * <p>
     * Updates internal representation of scents for lost robots. Meaning this
     * function is not pure.
     * @param startPoint starting point of the robot.
     * @param startCardinal starting cardinality of the robot.
     * @param instructions list of instructions to follow.
     * @return The final state of the robot.
     */
    public Output processRobotInstruction(
            final Point startPoint,
            final Cardinal startCardinal,
            final List<Instruction> instructions
    ) {
        Point currP = startPoint;
        boolean wasLost = false;
        Cardinal currCardinal = startCardinal;

        for (final Instruction instruction : instructions) {
            switch (instruction) {
                case FORWARD -> {
                    final Point offset = getOffset(currCardinal);
                    final int newX = currP.x() + offset.x();
                    final int newY = currP.y() + offset.y();
                    final Point newPoint = new Point(newX, newY);

                    if (scents.contains(newPoint)) {
                        // Skip previously lost robot here.
                        continue;
                    }

                    if (
                            newX < 0 || newX > this.width ||
                            newY < 0 || newY > this.height
                    ) {
                        // Robot fell off world.
                        scents.add(newPoint);
                        wasLost = true;
                        break;
                    }

                    currP = newPoint;
                }
                case LEFT -> currCardinal = rotateLeft(currCardinal);
                case RIGHT -> currCardinal = rotateRight(currCardinal);
            }

            if (wasLost) {
                break;
            }
        }

        return new Output(
                currP,
                currCardinal,
                wasLost
        );
    }

    private static Point getOffset(final Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> new Point(0, 1);
            case SOUTH -> new Point(0, -1);
            case EAST -> new Point(1, 0);
            case WEST -> new Point(-1, 0);
        };
    }

    private static Cardinal rotateLeft(final Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> Cardinal.WEST;
            case WEST -> Cardinal.SOUTH;
            case SOUTH -> Cardinal.EAST;
            case EAST -> Cardinal.NORTH;
        };
    }

    private static Cardinal rotateRight(final Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> Cardinal.EAST;
            case EAST -> Cardinal.SOUTH;
            case SOUTH -> Cardinal.WEST;
            case WEST -> Cardinal.NORTH;
        };
    }
}
