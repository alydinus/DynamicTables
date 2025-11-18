package kg.spring.project.exception;

public class DuplicateTableException extends RuntimeException {
    public DuplicateTableException(String msg) {
        super(msg);
    }
}
