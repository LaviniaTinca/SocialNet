package team.proiectextins.domain.validators;


import team.proiectextins.domain.validators.exceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}