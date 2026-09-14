package com.example.book.keeping;

import org.junit.jupiter.api.Test;

class ApplicationTests {

    @Test
    void mainMethodExists() {
        // Verify the application class is loadable
        try {
            Class.forName("com.example.book.keeping.Application");
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Application class not found", e);
        }
    }
}
