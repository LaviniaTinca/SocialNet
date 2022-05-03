package team.proiectextins.repository.file;


import team.proiectextins.domain.FriendRequest;
import team.proiectextins.domain.Status;
import team.proiectextins.domain.Tuple;
import team.proiectextins.domain.validators.Validator;

import java.time.LocalDate;
import java.util.List;

public class FriendRequestFileRepository extends AbstractFileRepository<Tuple<Long, Long>, FriendRequest> {
    /**
     * constructor
     *
     * @param fileName  csv - numele fisierului din care citim
     * @param validator - validator
     */
    public FriendRequestFileRepository(String fileName, Validator<FriendRequest> validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(FriendRequest entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getStatus() + ";" + entity.getDate();
    }

    @Override
    protected FriendRequest extractEntity(List<String> atributes) {
        Long from = Long.parseLong(atributes.get(0));
        Long to = Long.parseLong(atributes.get(1));
        String s = atributes.get(2);
        LocalDate date = LocalDate.parse(atributes.get(3));
        FriendRequest friendRequest = new FriendRequest();
        Tuple t = new Tuple(from, to);
        friendRequest.setId(t);
        friendRequest.setDate(date);
        friendRequest.setStatus(Status.valueOf(s));
        return friendRequest;
    }
}


