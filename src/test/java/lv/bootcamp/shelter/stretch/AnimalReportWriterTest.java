package lv.bootcamp.shelter.stretch;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.task4.AnimalSorter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Stretch goal: Testing file output
 * <p>
 * Practice:
 * - Writing to temp files and reading them back
 * - String content assertions
 * - Cleanup with Files.deleteIfExists
 * <p>
 * Instructions:
 * These tests verify that AnimalReportWriter produces correct output.
 * This task is optional — attempt it after completing tasks 1–6.
 */
@DisplayName("AnimalReportWriter (stretch)")
class AnimalReportWriterTest {

    private final AnimalReportWriter writer = new AnimalReportWriter();

    private final Animal buddy = new Animal("Buddy", "Dog", 3, true, LocalDate.of(2026, 1, 15));
    private final Animal luna = new Animal("Luna", "Cat", 2, true, LocalDate.of(2026, 1, 10));
    private final Animal max = new Animal("Max", "Dog", 5, false, LocalDate.of(2026, 1, 20));
    private final Animal bella = new Animal("Bella", "Cat", 1, true, LocalDate.of(2026, 1, 5));

    @Test
    @DisplayName("writes report file that contains total count")
    void shouldWriteTotalCount() throws IOException {
        List<Animal> animals = List.of(buddy, max, luna);
        Path output = Files.createTempFile("report-test", ".txt");

        writer.writeReport(animals, output);
        String content = Files.readString(output, StandardCharsets.UTF_8);

        assertThat(content).contains("Total animals: 3");
        Files.deleteIfExists(output);
    }

    @Test
    @DisplayName("writes per-species breakdown in alphabetical order")
    void shouldWriteSpeciesBreakdown() throws IOException {
        List<Animal> animals = List.of(buddy, max, luna, bella);
        Path output = Files.createTempFile("report-test", ".txt");

        writer.writeReport(animals, output);
        String content = Files.readString(output, StandardCharsets.UTF_8);

        assertThat(content).containsSubsequence("Cat: 2 total, 2 vaccinated", "Dog: 2 total, 1 vaccinated");
        Files.deleteIfExists(output);
    }

    @Test
    @DisplayName("writes oldest animal per species")
    void shouldWriteOldestPerSpecies() throws IOException {
        List<Animal> animals = List.of(buddy, max, luna, bella);
        Path output = Files.createTempFile("report-test", ".txt");

        writer.writeReport(animals, output);
        String content = Files.readString(output, StandardCharsets.UTF_8);

        assertThat(content).contains("Dog: Max (age 5)");
        Files.deleteIfExists(output);
    }

    @Test
    @DisplayName("species breakdown lines match grouped animals (stream assertion)")
    void shouldContainGroupedSpeciesLines() throws IOException {
        List<Animal> animals = List.of(buddy, max, luna, bella);
        Path output = Files.createTempFile("report-test", ".txt");

        writer.writeReport(animals, output);
        String content = Files.readString(output, StandardCharsets.UTF_8);

        assertThat(content.lines())
                .filteredOn(line -> line.contains("total"))
                .containsExactly("Cat: 2 total, 2 vaccinated", "Dog: 2 total, 1 vaccinated");
        Files.deleteIfExists(output);
    }

    @Test
    @DisplayName("spy: writeReport is called")
    void shouldCallWriteReportOnSpy() throws IOException {
        AnimalReportWriter spyWriter = spy(new AnimalReportWriter());
        List<Animal> animals = List.of(buddy, max);
        Path output = Files.createTempFile("report-test", ".txt");

        spyWriter.writeReport(animals, output);

        verify(spyWriter).writeReport(animals, output);
        Files.deleteIfExists(output);
    }
}
