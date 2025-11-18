package kg.spring.project.exception;

import lombok.Getter;

@Getter
public class DataNotFoundException extends RuntimeException {
    private String path;
    public DataNotFoundException(String s, String path) {
        super(s);
        this.path = path;
    }
}
