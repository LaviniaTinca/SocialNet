package team.proiectextins.domain.validators;

import team.proiectextins.domain.User;
import team.proiectextins.domain.validators.exceptions.ValidationException;
//package domain.validators;


public class UserValidator implements Validator<User> {
    public UserValidator() {

    }

    @Override
    public void validate(User entity) throws ValidationException {
        //to do
        String erros = "";
        if (entity.getFirstName().equals("")) {
            erros += "First name must not be null\n";
        }
        if (entity.getLastName().equals("")) {
            erros += "Last name must not be null\n";
        }
        if (!erros.equals("")) {
            throw new ValidationException(erros);
        }
    }
}


