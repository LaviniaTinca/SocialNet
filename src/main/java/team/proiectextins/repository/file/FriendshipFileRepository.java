package team.proiectextins.repository.file;


import team.proiectextins.domain.Friendship;
import team.proiectextins.domain.Tuple;
import team.proiectextins.domain.validators.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    public FriendshipFileRepository(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getFriendshipDate();
    }

    @Override
    protected Friendship extractEntity(List<String> atributes) {
        Long id1 = Long.parseLong(atributes.get(0));
        Long id2 = Long.parseLong(atributes.get(1));
        LocalDate date = LocalDate.parse(atributes.get(2));
        Friendship friendship = new Friendship();
        Tuple t = new Tuple(id1, id2);
        friendship.setId(t);
        friendship.setFriendshipDate(date);
        return friendship;
    }


}


