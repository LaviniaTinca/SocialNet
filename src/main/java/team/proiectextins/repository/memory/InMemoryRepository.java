package team.proiectextins.repository.memory;


import team.proiectextins.domain.Entity;
import team.proiectextins.domain.validators.Validator;
import team.proiectextins.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private final Map<ID, E> entities;
    private final Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public E findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("Id-ul nu poate fi null");
        return entities.get(id);
    }

    @Override
    public E findOne(String string) {
        return null;
    }

    @Override
    public List<E> findMatching(Long currentUser, Long friend) {
        return null;
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi null");
        validator.validate(entity);
        if (entities.get(entity.getId()) != null)
            return entity;
        entities.put(entity.getId(), entity);
        return null;
    }

    @Override
    public E delete(ID id) {
        if (id == null)
            throw new IllegalArgumentException("Id-ul nu poate fi null!");
        return entities.remove(id);
    }

    //    @Override
//    public E update(E entity) {
//        if(entity == null)
//            throw new IllegalArgumentException("entity must not be null!");
//        validator.validate(entity);
//        if(entities.containsKey(entity.getId())){
//            entities.put(entity.getId(),entity);
//            return null;
//        }
//        return entity;
//    }
    @Override
    public E update(E entity) {

        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        validator.validate(entity);

        //entities.put(entity.getId(),entity);

        if (entities.get(entity.getId()) != null) {
            entities.put(entity.getId(), entity);
            return null;
        }
        return entity;

    }
}

