package team.proiectextins.repository.file;


import team.proiectextins.domain.User;
import team.proiectextins.domain.validators.Validator;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<Long, User> {

    public UserFileRepository(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }

    @Override
    protected User extractEntity(List<String> atributes) {
        User user = new User(atributes.get(1), atributes.get(2));
        user.setId(Long.parseLong(atributes.get(0)));
        return user;
    }

    /**
     * Creates an ID for the next user
     *
     * @return the next free ID
     */
    protected Long nextID() {
        Long max = 0L;
        for (User user : findAll()) {
            if (user.getId() > max) {
                max = user.getId();
            }
        }
        max = max + 1L;
        return max;
    }

    @Override
    public User save(User user) {
        user.setId(nextID());
        return super.save(user);
    }


}


