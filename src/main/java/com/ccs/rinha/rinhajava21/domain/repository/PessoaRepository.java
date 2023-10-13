package com.ccs.rinha.rinhajava21.domain.repository;

import com.ccs.rinha.rinhajava21.domain.entity.Pessoa;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

    @Query("from Pessoa p where p.id= :id")
    Optional<Pessoa> findByIdEager(UUID id);

    //    @Query(value = """
//            from Pessoa p
//            left join fetch p.stack
//            where p.id in (
//            select p.id from Pessoa p
//            left join p.stack s
//            where (:t is null or lower(p.nome) like lower(concat('%', :t, '%')))
//            and
//            (:t is null or lower(p.apelido) like lower(concat('%', :t, '%')))
//            and
//            (:t is null or lower(s) like lower(concat('%', :t, '%')))
//            )
//             """)
    @Query(value = """ 
            from Pessoa p      
            where (:t is null or lower(p.nome) like lower(concat('%', :t, '%')))
            and
            (:t is null or lower(p.apelido) like lower(concat('%', :t, '%')))
            and
            (:t is null or lower(p.stack) like lower(concat('%', :t, '%')))
            """)
    @Transactional(readOnly = true)
    List<Pessoa> findByTermo(String t, PageRequest pageRequest);
}
