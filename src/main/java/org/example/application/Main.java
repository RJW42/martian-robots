package org.example.application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Main {
    static void main(final String[] args) {
        // Valid Input
        if (args.length > 1) {
            System.err.printf(
                    """
                    Invalid use of martian-robots. Expected either no arguments, for std-in mode. Or a file name.
                    
                    E.g.
                        %s file-name.txt
                    """,
                    args[0]
            );
            System.exit(-1);
        }

        // Parse Input
        final InputParser.InputStream input;
        try {
            input = parseInput(args.length == 1 ? args[0] : null);
        } catch (final FileNotFoundException e) {
            System.err.printf("Unable to read from given file %s\n", args[0]);
            System.err.println(e);
            System.exit(-1);
            return;
        } catch (final InputParser.InvalidInputException e) {
            System.err.printf("Invalid inp\n%s\n", e.getMessage());
            System.exit(-1);
            return;
        }

        // Process input
        final Processor processor = new Processor(input.width(), input.height());

        while (true) {
            final InputParser.RobotInput rInput;

            try {
                rInput = input.robotSupplier().get();
            } catch (final InputParser.InvalidInputException e) {
                System.err.printf("Invalid input when processing robots instructions:\n%s\n", e.getMessage());
                continue;
            }

            if (rInput == null) {
                break;
            }

            final Processor.Output output = processor.processRobotInstruction(
                    rInput.startPoint(),
                    rInput.startCardinal(),
                    rInput.instructions()
            );

            System.out.printf(
                    "%d %d %s%s\n",
                    output.point().x(),
                    output.point().y(),
                    output.cardinal().asChar(),
                    output.wasLost() ? " LOST" : ""
            );
        }

    }


    static InputParser.InputStream parseInput(final String fileName) throws FileNotFoundException, InputParser.InvalidInputException {
        final BufferedReader reader;
        if (fileName == null) {
            reader = new BufferedReader(new InputStreamReader(System.in));
        } else {
            reader = new BufferedReader(new FileReader(fileName));
        }

        return new InputParser(reader).parseInputAsStream();
    }
}