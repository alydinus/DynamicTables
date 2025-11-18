package kg.spring.project.exception;

import lombok.Getter;

@Getter
public class TableNotFoundException extends RuntimeException {
    private String path;
    public TableNotFoundException(String msg, String path) {
        super(msg);
        this.path = path;
    }
}
