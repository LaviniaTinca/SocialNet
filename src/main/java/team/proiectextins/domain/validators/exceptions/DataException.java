package team.proiectextins.domain.validators.exceptions;


public class DataException extends RuntimeException {
    public DataException() {
    }

    public DataException(String message) {
        super(message);
    }
}
