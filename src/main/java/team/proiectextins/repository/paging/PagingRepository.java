package team.proiectextins.repository.paging;


import team.proiectextins.domain.Entity;
import team.proiectextins.repository.Repository;

public interface PagingRepository<ID,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}

