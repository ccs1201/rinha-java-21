package com.ccs.rinha.rinhajava21.domain.repository;

import com.ccs.rinha.rinhajava21.domain.entity.Pessoa;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

    @Query("from Pessoa p left join fetch p.stack where p.id= :id")
    Optional<Pessoa> findByIdEager(UUID id);

    @Query(value = """
            from Pessoa p
            left join fetch p.stack
            where p.id in (
            select p.id from Pessoa p
            left join p.stack s
            where (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%')))
            and
            (:apelido is null or lower(p.apelido) like lower(concat('%', :apelido, '%')))
            and
            (:stack is null or lower(s) like lower(concat('%', :stack, '%')))
            )
             """)
    List<Pessoa> findByTermo(String nome, String apelido, String stack, PageRequest pageRequest);
}
