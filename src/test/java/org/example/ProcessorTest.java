package org.example;

import org.example.application.Definitions.Cardinal;
import org.example.application.Definitions.Instruction;
import org.example.application.Definitions.Point;
import org.example.application.Processor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessorTest {

    @Test
    public void testProcessor_robotSucceeds() {
        // Given
        final var processor = new Processor(5, 5);
        final var instructions = List.of(
                Instruction.FORWARD,
                Instruction.LEFT,
                Instruction.FORWARD,
                Instruction.RIGHT,
                Instruction.FORWARD
        );

        // When
        final var output = processor.processRobotInstruction(
                new Point(3, 3),
                Cardinal.NORTH,
                instructions
        );

        // Then
        assertFalse(output.wasLost());
        assertEquals(Cardinal.NORTH, output.cardinal());
        assertEquals(new Point(2, 5), output.point());
    }

    @Test
    public void testProcessor_robotLoss() {
        // Given
        final var processor = new Processor(5, 5);
        final var instructions = List.of(
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.RIGHT,
                Instruction.FORWARD
        );

        // When
        final var output = processor.processRobotInstruction(
                new Point(3, 3),
                Cardinal.NORTH,
                instructions
        );

        // Then
        assertTrue(output.wasLost());
        assertEquals(Cardinal.NORTH, output.cardinal());
        assertEquals(new Point(3, 5), output.point());
    }

    @Test
    public void testProcessor_robotSucceedsAfterLoss() {
        // Given
        final var processor = new Processor(5, 5);
        final var instructions = List.of(
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.FORWARD,
                Instruction.RIGHT,
                Instruction.RIGHT,
                Instruction.FORWARD
        );

        // When
        final var r1 = processor.processRobotInstruction(
                new Point(3, 3),
                Cardinal.NORTH,
                instructions
        );

        final var r2 = processor.processRobotInstruction(
                new Point(3, 3),
                Cardinal.NORTH,
                instructions
        );

        // Then
        assertTrue(r1.wasLost());
        assertEquals(Cardinal.NORTH, r1.cardinal());
        assertEquals(new Point(3, 5), r1.point());
        assertFalse(r2.wasLost());
        assertEquals(Cardinal.SOUTH, r2.cardinal());
        assertEquals(new Point(3, 4), r2.point());
    }
}
