package polyglot_dsa.test.menu;

import java.util.Map;
import java.util.TreeMap;
import polyglot_dsa.test.io.InputScanner;

public class ConsoleMenu extends MenuInterface {
    private final Map<String, Runnable> options;
    private final InputScanner scanner;

    public ConsoleMenu(Map<String, Runnable> options, InputScanner scanner) {
        // Use TreeMap to automatically sort options by key (e.g., "1", "2", etc.)
        this.options = new TreeMap<>(options);
        this.scanner = scanner;
    }

    @Override
    public void displayMenu() {
        while (true) {
            System.out.println("\n--- Polyglot DSA Test Runner ---");
            for (Map.Entry<String, Runnable> entry : options.entrySet()) {
                String key = entry.getKey();
                Runnable action = entry.getValue();
                String name = getTestName(action);
                System.out.println(key + ". Run " + name);
            }
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            
            String choice = scanner.nextLine().trim();
            if ("0".equals(choice)) {
                System.out.println("Exiting...");
                break;
            }
            
            Runnable action = options.get(choice);
            if (action != null) {
                try {
                    action.run();
                } catch (Exception e) {
                    System.err.println("Error during execution: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid option! Try again.");
            }
        }
    }

    private String getTestName(Runnable action) {
        String className = action.getClass().getSimpleName();
        // Fallback for lambda expressions or anonymous classes
        if (className.contains("$$") || className.isEmpty()) {
            return "Custom Test";
        }
        if (className.endsWith("Test")) {
            className = className.substring(0, className.length() - 4);
        }
        // Split camelCase into readable space-separated words
        return className.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
    }
}
