package com.ccs.rinha.rinhajava21.api.controller;

import com.ccs.rinha.rinhajava21.api.model.PessaoInput;
import com.ccs.rinha.rinhajava21.domain.entity.Pessoa;
import com.ccs.rinha.rinhajava21.domain.repository.PessoaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static org.springframework.http.HttpStatus.*;

//@RestController
//@RequestMapping
public class ControllerSincrono {

    private final PessoaRepository repository;
    private final PageRequest pageRequest = PageRequest.of(0, 50);
    private static final String path = "/pessoas/";

    public ControllerSincrono(PessoaRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/pessoas")
    @ResponseStatus(CREATED)
    public ResponseEntity<?> create(@RequestBody @Valid PessaoInput input) {

        try {
            return ResponseEntity
                    .created(URI
                            .create(
                                    path.concat(repository.save(input.toPessoa()).getId().toString())))
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/pessoas/{id}")
    public Pessoa findById(@PathVariable @NotNull UUID id) throws InterruptedException {

        return repository.findByIdEager(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @GetMapping("/pessoas")
    @ResponseStatus(OK)
    public List<Pessoa> findByTermo(@Nullable String t) {

        if (Objects.isNull(t)) {
            throw new ResponseStatusException(BAD_REQUEST);
        }

        return repository.findByTermo(t, pageRequest);
    }

    @GetMapping("/contagem-pessoas")
    @ResponseStatus(OK)
    public Long contarPessoas() {
        return repository.count();
    }
}
