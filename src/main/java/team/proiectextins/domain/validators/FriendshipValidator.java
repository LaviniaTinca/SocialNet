package team.proiectextins.domain.validators;


import team.proiectextins.domain.Friendship;
import team.proiectextins.domain.validators.exceptions.ValidationException;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (Objects.equals(entity.getId().getLeft(), entity.getId().getRight())) {
            throw new ValidationException("IDs must be different!");
        }
    }
}


