package kg.spring.project.exception;

import lombok.Getter;

@Getter
public class MissingColumnException extends RuntimeException {
    private String path;
    public MissingColumnException(String s, String path) {
        super(s);
        this.path = path;
    }
}
