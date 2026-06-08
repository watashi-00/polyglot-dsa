package polyglot_dsa.test.io;

public interface InputScanner extends AutoCloseable {
    String nextLine();
    boolean hasNextLine();
    @Override
    void close();
}
