package org.example.application;

import org.example.application.Definitions.Cardinal;
import org.example.application.Definitions.Instruction;
import org.example.application.Definitions.Point;
import org.example.application.InputParser.InvalidInputException.Reason;
import org.example.util.Strings;
import org.example.util.ThrowingSupplier;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Input parser for MartianRobots. Works over any buffered reader, e.g. file, or std-in.
 * <p>
 * Can parse the input into two variants.
 *  - Input: Fully parse all input until EOF/EmptyLine reached.
 *  - InputStream: Parse initial width, height, then only parse robots as requested.
 *                 Useful for larger inputs.
 */
public final class InputParser {
    /**
     *
     * @param width, width of the Mars model
     * @param height, height of the Mars model
     * @param robotSupplier, supplies robots, until EOF/EmptyLine is reached, then null.
     */
    public record InputStream(
            int width,
            int height,
            ThrowingSupplier<RobotInput, InvalidInputException> robotSupplier
    ) {}

    public record Input(
            int width,
            int height,
            List<RobotInput> robotInstructions
    ) {}

    public record RobotInput(
            Point startPoint,
            Cardinal startCardinal,
            List<Instruction> instructions
    ) {}

    private final BufferedReader reader;
    private boolean parsed;

    public InputParser(final BufferedReader reader) {
        this.reader = reader;
        this.parsed = false;
    }


    /**
     * Parse all contents of buffered reader.
     * @return A fully parsed set of input width, height and robot instructions.
     * @throws InvalidInputException if there are any errors in the input
     */
    public Input parseInput() throws InvalidInputException  {
        final InputStream stream = parseInputAsStream();
        final List<RobotInput> robotInstructions = new ArrayList<>();

        while (true) {
            final RobotInput i = stream.robotSupplier().get();
            if (i == null) {
                break;
            }
            robotInstructions.add(i);
        }

        return new Input(stream.width, stream.height, robotInstructions);
    }

    /**
     * Parse inital contents of buffered reader.
     * @return A stream variant of the input, where each robot instruction set is parsed as needed.
     * @throws InvalidInputException if the initial width, height input is invalid
     */
    public InputStream parseInputAsStream() throws InvalidInputException {
        // Can't parse twice
        if (parsed) {
            throw new IllegalStateException("Cannot parse twice");
        }
        parsed = true;

        // Parse width and height
        final String dimensionStr = readLine(this.reader);
        throwIfFailsCheck(
                !Strings.isBlankOrNull(dimensionStr),
                () -> new InvalidInputException(Reason.INVALID_INPUT, "Expected width and height to be supplied")
        );
        final String[] parts = dimensionStr.split(" ");
        throwIfFailsCheck(
                parts.length == 2,
                () -> new InvalidInputException(Reason.INVALID_INPUT,
                        String.format("Expected two positive numbers separated by space for width and height got %s", dimensionStr)
                )
        );

        final int width = parseInt(parts[0]);
        final int height = parseInt(parts[1]);
        throwIfFailsCheck(
                width > 0 && height > 0,
                () -> new InvalidInputException(Reason.INVALID_INPUT, "Expect both width and height to be greater than 0")
        );

        // Setup robot stream parser
        return new InputStream(
                width, height, this::parseNextRobotInput
        );
    }

    private RobotInput parseNextRobotInput() throws InvalidInputException {
        final String startStr = readLine(this.reader);
        if (Strings.isBlankOrNull(startStr)) {
            // End of input reached
            return null;
        }

        // Parse robot start line
        final String[] parts = startStr.split(" ");
        throwIfFailsCheck(parts.length == 3,
                () -> new InvalidInputException(Reason.INVALID_INPUT,
                        String.format("Expected robot start pos to have three parts: x y DIR. Got %s", startStr)
                )
        );
        final int x = parseInt(parts[0]);
        final int y = parseInt(parts[1]);
        throwIfFailsCheck(
                x >= 0 && y >= 0,
                () -> new InvalidInputException(Reason.INVALID_INPUT, "Expected robot start pos to be positive.")
        );


        final Cardinal cardinal = parseCardinal(parts[2]);

        // Parse robot instructions
        final String instructionsStr = readLine(this.reader);
        throwIfFailsCheck(
                !Strings.isBlankOrNull(instructionsStr),
                () -> new InvalidInputException(Reason.INVALID_INPUT, "Expected instructions list for robot bot gone none.")
        );

        final List<Instruction> instructions = parseInstructions(instructionsStr);

        return new RobotInput(
                new Point(x, y),
                cardinal,
                instructions
        );
    }

    private List<Instruction> parseInstructions(final String string) throws InvalidInputException {
        final List<Instruction> out = new ArrayList<>();

        for (final int ch : string.chars().toArray()) {
         final Instruction instruction = switch (ch) {
            case 'R' -> Instruction.RIGHT;
            case 'L' -> Instruction.LEFT;
            case 'F' -> Instruction.FORWARD;
            default -> throw new InvalidInputException(Reason.INVALID_INPUT, String.format("Expected robot instruction to be: L,R,F got %c", (char)ch));
         };

         out.add(instruction);
        }

        return out;
    }

    private static String readLine(final BufferedReader reader) throws InvalidInputException {
        try {
            return reader.readLine();
        } catch (final IOException e) {
            throw new InvalidInputException(Reason.IO_EXCEPTION, "Failed to read line from buffered reader", e);
        }
    }

    private static int parseInt(final String string) throws InvalidInputException {
        try {
            return Integer.parseInt(string);
        } catch (final NumberFormatException e) {
            throw new InvalidInputException(Reason.INVALID_INPUT, String.format("Unable to parse %s number", string), e);
        }
    }

    private static Cardinal parseCardinal(final String string) throws InvalidInputException {
        return switch (string) {
            case "N" -> Cardinal.NORTH;
            case "S" -> Cardinal.SOUTH;
            case "E" -> Cardinal.EAST;
            case "W" -> Cardinal.WEST;
            default -> throw new InvalidInputException(Reason.INVALID_INPUT, String.format("Expected cardinal input: N,S,E,W. But got %s", string));
        };
    }

    /**
     * Small internal helper to validate precondition or fail.
     */
    private static void throwIfFailsCheck(
            final boolean check,
            final Supplier<InvalidInputException> err
    ) throws InvalidInputException {
        if (!check) {
            throw err.get();
        }
    }

    /**
     * Wrapper class for all possible exceptions when parsing
     */
    public static final class InvalidInputException extends Exception {
        public enum Reason {
            IO_EXCEPTION,
            INVALID_INPUT
        }

        private final Reason reason;

        public InvalidInputException(final Reason reason, final String message) {
            super(message);
            this.reason = reason;
        }

        public InvalidInputException(final Reason reason, final String message, final Throwable cause) {
            super(message, cause);
            this.reason = reason;
        }

        public Reason getReason() {
            return reason;
        }
    }
}
