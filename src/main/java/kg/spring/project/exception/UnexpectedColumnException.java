package kg.spring.project.exception;

import lombok.Getter;

@Getter
public class UnexpectedColumnException extends RuntimeException {
    private String path;
    public UnexpectedColumnException(String s, String path) {
        super(s);
        this.path = path;
    }
}
