package team.proiectextins.domain.validators;


import team.proiectextins.domain.FriendRequest;
import team.proiectextins.domain.validators.exceptions.FriendshipException;
import team.proiectextins.domain.validators.exceptions.ValidationException;

import java.util.Objects;

public class FriendRequestValidator implements Validator<FriendRequest> {
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if (Objects.equals(entity.getId().getLeft(), entity.getId().getRight())) {
            throw new FriendshipException("Hei! imi trimit mie cerere de prietenie?");
        }
    }
}


