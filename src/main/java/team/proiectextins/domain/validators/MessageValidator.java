package team.proiectextins.domain.validators;


import team.proiectextins.domain.Message;
import team.proiectextins.domain.validators.exceptions.ValidationException;

import java.util.Objects;

public class MessageValidator implements Validator<Message> {
    public MessageValidator() {
    }

    @Override
    public void validate(Message msg) throws ValidationException {
        if (msg.getText().length() == 0) {
            throw new ValidationException("Mesajul trebuie sa nu fie gol");
        }

        if (Objects.equals(msg.getFrom(), msg.getTo())) {
            throw new ValidationException("Mesajul nu poate avea acelasi destinatar ca si trimitator");
        }
    }
}


