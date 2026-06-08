package polyglot_dsa.test.lists;

import polyglot_dsa.lists.SinglyLinkedListWithTail;

public class SinglyLinkedListWithTailTest implements Runnable {
    @Override
    public void run() {
        System.out.println("\nRunning tests: SinglyLinkedListWithTail");
        try {
            testEmptyList();
            testAddElements();
            testRemoveElements();
            testSingleElementList();
            System.out.println("All tests passed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }

    private void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new RuntimeException(message + " | Expected: " + expected + ", Actual: " + actual);
    }

    private void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new RuntimeException(message + " | Expected: " + expected + ", Actual: " + actual);
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Validation failed: " + message);
        }
    }

    private void testEmptyList() {
        System.out.print("Testing empty list... ");
        SinglyLinkedListWithTail list = new SinglyLinkedListWithTail();
        assertEquals(0, list.getSize(), "Empty list size must be 0");
        assertTrue(list.getHead() == null, "Empty list head must be null");
        assertTrue(list.getTail() == null, "Empty list tail must be null");
        assertEquals("null", list.toString(), "Empty list string representation must be 'null'");
        System.out.println("OK");
    }

    private void testAddElements() {
        System.out.print("Testing add elements... ");
        SinglyLinkedListWithTail list = new SinglyLinkedListWithTail();
        list.add(10);
        assertEquals(1, list.getSize(), "Size must be 1 after add(10)");
        assertEquals(10, list.getHead().getValue(), "Head must be 10");
        assertEquals(10, list.getTail().getValue(), "Tail must be 10");

        list.add(20);
        assertEquals(2, list.getSize(), "Size must be 2 after add(20)");
        assertEquals(10, list.getHead().getValue(), "Head must remain 10");
        assertEquals(20, list.getTail().getValue(), "Tail must be 20");

        list.add(30);
        assertEquals(3, list.getSize(), "Size must be 3 after add(30)");
        assertEquals(10, list.getHead().getValue(), "Head must remain 10");
        assertEquals(30, list.getTail().getValue(), "Tail must be 30");
        assertEquals("10 -> 20 -> 30 -> null", list.toString(), "Incorrect string representation");
        System.out.println("OK");
    }

    private void testRemoveElements() {
        System.out.print("Testing remove elements... ");
        SinglyLinkedListWithTail list = new SinglyLinkedListWithTail();
        list.add(10);
        list.add(20);
        list.add(30);

        list.remove();
        assertEquals(2, list.getSize(), "Size must be 2 after remove");
        assertEquals(20, list.getHead().getValue(), "New head must be 20");
        assertEquals(30, list.getTail().getValue(), "Tail must remain 30");

        list.remove();
        assertEquals(1, list.getSize(), "Size must be 1 after remove");
        assertEquals(30, list.getHead().getValue(), "New head must be 30");
        assertEquals(30, list.getTail().getValue(), "Tail must be 30");

        list.remove();
        assertEquals(0, list.getSize(), "Size must be 0 after removing all");
        assertTrue(list.getHead() == null, "Head must be null");
        assertTrue(list.getTail() == null, "Tail must be null");
        System.out.println("OK");
    }

    private void testSingleElementList() {
        System.out.print("Testing single element list... ");
        SinglyLinkedListWithTail list = new SinglyLinkedListWithTail();
        list.add(42);
        assertEquals(1, list.getSize(), "Size must be 1");
        assertTrue(list.getHead() == list.getTail(), "Head and tail must point to same reference");
        list.remove();
        assertEquals(0, list.getSize(), "Size must be 0");
        assertTrue(list.getHead() == null, "Head must be null");
        assertTrue(list.getTail() == null, "Tail must be null");
        
        // Removing from empty list should not throw error
        list.remove();
        assertEquals(0, list.getSize(), "Size must remain 0");
        System.out.println("OK");
    }
}
