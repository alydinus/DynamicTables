package kg.spring.project.exception;

import lombok.Getter;

@Getter
public class NullValueForNonNullColumnException extends RuntimeException {
    private String path;
    public NullValueForNonNullColumnException(String s, String path) {
        super(s);
        this.path = path;
    }
}
