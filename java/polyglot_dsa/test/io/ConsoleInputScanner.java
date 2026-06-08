package polyglot_dsa.test.io;

import java.util.Scanner;

public class ConsoleInputScanner implements InputScanner {
    private final Scanner scanner;

    public ConsoleInputScanner() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }

    @Override
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    @Override
    public void close() {
        scanner.close();
    }
}
