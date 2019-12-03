package com.example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ExampleTest {

    @Test
    public void test() {
        Example example = new Example();

        String actual = example.getMessage("World");

        assertEquals("Hello, World", actual);
    }
}
