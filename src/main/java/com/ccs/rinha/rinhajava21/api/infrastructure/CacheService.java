package com.ccs.rinha.rinhajava21.api.infrastructure;

import com.ccs.rinha.rinhajava21.domain.entity.Pessoa;
import com.ccs.rinha.rinhajava21.domain.repository.PessoaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
public class CacheService {

    private final PessoaRepository repository;

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    private final Map<UUID, Pessoa> mapPessoas = new ConcurrentHashMap<>(25000);

    private final Set<String> setApelidos = ConcurrentHashMap.newKeySet(25000);
    private static final PageRequest pageRequest = PageRequest.of(0, 50);

    public CacheService(PessoaRepository repository) {
        this.repository = repository;
    }

    public void insert(Pessoa pessoa) {
        if (setApelidos.contains(pessoa.getApelido())) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY);
        }
        try {
            repository.saveAndFlush(pessoa);
            setApelidos.add(pessoa.getApelido());
            mapPessoas.put(pessoa.getId(), pessoa);
        } catch (Exception e) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY);
        }
    }

    public Optional<Pessoa> findById(UUID id) {
        log.info("FindBy id: ".concat(id.toString()));
        var p = Optional.ofNullable(mapPessoas.get(id));
        if (p.isEmpty()) {
            p = findByIdInBD(id);
        }
        return p;
    }

    public List<Pessoa> findByTermo(String termo) {
        log.info("findByTermo: ".concat(termo));
        var pe = mapPessoas
                .values()
                .parallelStream()
                .filter(p -> p.getApelido().contains(termo) ||
                             p.getNome().contains(termo) ||
                             p.getStack().contains(termo))
                .limit(50)
                .collect(Collectors.toList());

        if (pe.isEmpty()) {
            pe = findByTermoInBD(termo);
        }
        return pe;
    }

    public long count() {
        log.info("count...");
        return mapPessoas.size();
    }

    private List<Pessoa> findByTermoInBD(String termo) {
        return repository.findByTermo(termo, pageRequest);
    }

    private Optional<Pessoa> findByIdInBD(UUID id) {
        return repository.findById(id);
    }

    //    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 5)
    protected void batchInsert() {

        var toPersist = mapPessoas
                .values()
                .parallelStream()
                .filter(pessoa -> !pessoa.isPersistido())
                .limit(500)
                .peek(pessoa -> pessoa.setPersistido(true))
                .collect(Collectors.toList());

        repository.saveAll(toPersist);
    }
}
