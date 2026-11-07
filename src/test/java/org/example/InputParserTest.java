package org.example;


import org.example.application.InputParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.example.application.Definitions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InputParserTest {

    @Test
    public void testInputParser_succeeds() throws Exception {
        // Given
        final var str = """
                5 3
                1 1 E
                RFL
                3 2 N
                FFF
                """;

        // When
        final var input = parse(str);

        // Expect
        assertEquals(5, input.width());
        assertEquals(3, input.height());
        assertEquals(2, input.robotInstructions().size());

        final var i1 = input.robotInstructions().get(0);
        assertEquals(new Point(1, 1), i1.startPoint());
        assertEquals(Cardinal.EAST, i1.startCardinal());
        assertEquals(
                List.of(
                        Instruction.RIGHT, Instruction.FORWARD, Instruction.LEFT
                ),
                i1.instructions()
        );

        final var i2 = input.robotInstructions().get(1);
        assertEquals(new Point(3, 2), i2.startPoint());
        assertEquals(Cardinal.NORTH, i2.startCardinal());
        assertEquals(
                List.of(
                        Instruction.FORWARD, Instruction.FORWARD, Instruction.FORWARD
                ),
                i2.instructions()
        );
    }


    @ParameterizedTest
    @ValueSource(
            strings = {
                    """
                    
                    """,
                    """
                    5
                    """,
                    """
                    5 -1
                    """,
                    """
                    5 3
                    1 1 X
                    """,
                    """
                    5 3
                    1 -1 E
                    """,
                    """
                    5 3
                    1 1 E
                    """,
                    """
                    5 3
                    1 1 E
                    X
                    """
            }
    )
    public void testInputParser_failsInvalidInput(final String input) throws Exception {
        try {
            // When
            parse(input);
        } catch (final InputParser.InvalidInputException e) {
            // success
            return;
        }

        fail(String.format("Expected input to fail with %s, but didn't", input));
    }

    public InputParser.Input parse(final String s) throws InputParser.InvalidInputException {
        return new InputParser(new BufferedReader(new StringReader(s))).parseInput();
    }
}
