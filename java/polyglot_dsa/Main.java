package polyglot_dsa;

import java.util.Map;

import polyglot_dsa.test.io.ConsoleInputScanner;
import polyglot_dsa.test.io.InputScanner;
import polyglot_dsa.test.menu.ConsoleMenu;
import polyglot_dsa.test.lists.SinglyLinkedListWithTailTest;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        Map<String, Runnable> options = main.config();
        try (InputScanner scanner = new ConsoleInputScanner()) {
            ConsoleMenu menu = new ConsoleMenu(options, scanner);
            menu.displayMenu();
        }
    }

    public Map<String, Runnable> config() {
        return Map.of(
                "1", new SinglyLinkedListWithTailTest()
        );
    }
}

