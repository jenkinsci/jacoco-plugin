package hudson.plugins.jacoco.model;

import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType.BRANCH;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType.CLAZZ;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType.COMPLEXITY;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType.INSTRUCTION;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType.LINE;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType.METHOD;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageValue.COVERED;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageValue.MISSED;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageValue.PERCENTAGE;
import static hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageValue.values;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;
import static org.junit.Assert.assertEquals;

public class CoverageGraphLayoutTest {

    private Locale localeBackup;

    @Before
    public void setUp() {
        localeBackup = Locale.getDefault();
    }

    @After
    public void tearDown() {
        Locale.setDefault(localeBackup);
    }

    @Test
    public void type() {
        Locale.setDefault(ENGLISH);

        assertEquals("New Coverage Types", 6, CoverageType.values().length);
        assertEquals("instructions", INSTRUCTION.getMessage());
        assertEquals("line", LINE.getMessage());
        assertEquals("branch", BRANCH.getMessage());
        assertEquals("method", METHOD.getMessage());
        assertEquals("class", CLAZZ.getMessage());
        assertEquals("complexity", COMPLEXITY.getMessage());

        Locale.setDefault(GERMAN);
        assertEquals("Anweisungen", INSTRUCTION.getMessage());
        assertEquals("Zeilen", LINE.getMessage());
        assertEquals("Branch", BRANCH.getMessage());
        assertEquals("Methoden", METHOD.getMessage());
        assertEquals("Klassen", CLAZZ.getMessage());
        assertEquals("KomplexitÃ¤t", COMPLEXITY.getMessage()); // TODO there might be an encoding issue with our resources?

    }

    @Test
    public void value() {
        Locale.setDefault(ENGLISH);
        assertEquals("New Coverage Value", 3, values().length);
        assertEquals("line covered", COVERED.getMessage(LINE));
        assertEquals("line missed", MISSED.getMessage(LINE));
        assertEquals("line", PERCENTAGE.getMessage(LINE));

        Locale.setDefault(GERMAN);
        assertEquals("Zeilen abgedeckt", COVERED.getMessage(LINE));
        assertEquals("Zeilen nicht abgedeckt", MISSED.getMessage(LINE));
        assertEquals("Zeilen", PERCENTAGE.getMessage(LINE));
    }
}