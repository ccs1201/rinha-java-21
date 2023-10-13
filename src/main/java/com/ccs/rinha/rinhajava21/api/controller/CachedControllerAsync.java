package com.ccs.rinha.rinhajava21.api.controller;

import com.ccs.rinha.rinhajava21.api.infrastructure.CacheService;
import com.ccs.rinha.rinhajava21.api.model.PessaoInput;
import com.ccs.rinha.rinhajava21.api.model.PessoaResponse;
import com.ccs.rinha.rinhajava21.domain.repository.PessoaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping
public class CachedControllerAsync {

    private static final String path = "/pessoas/";
    private final long TIME_OUT = 61;
    private final Executor EXECUTOR;
    private final CacheService service;
    private final PessoaRepository repository;

    public CachedControllerAsync(@Qualifier("virtual") Executor executor, CacheService service, PessoaRepository repository) {
        EXECUTOR = executor;
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("/pessoas")
    @ResponseStatus(CREATED)
    public CompletableFuture<ResponseEntity<Object>> create(@RequestBody @Valid PessaoInput input) {
        return CompletableFuture.supplyAsync(() -> {
            var pessoa = input.toPessoa();
            service.insert(pessoa);
//            if (service.insert(pessoa)) {
            return ResponseEntity
                    .created(URI
                            .create(
                                    path.concat(pessoa.getId().toString())))
                    .build();
//            } else {
//
//                throw new ResponseStatusException(UNPROCESSABLE_ENTITY);
//            }
        }, EXECUTOR).orTimeout(TIME_OUT, TimeUnit.SECONDS);
    }

    @GetMapping("/pessoas/{id}")
    public CompletableFuture<PessoaResponse> findById(@PathVariable @NotNull UUID id) {
        return CompletableFuture
                .supplyAsync(() ->
                                service.findById(id)
                                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND)),
                        EXECUTOR
                ).orTimeout(TIME_OUT, TimeUnit.SECONDS)
                .thenApply(PessoaResponse::toResponse);
    }

    @GetMapping("/pessoas")
    @ResponseStatus(OK)
    public CompletableFuture<List<PessoaResponse>> findByTermo(@Nullable String t) {
        if (Objects.isNull(t)) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        return CompletableFuture.supplyAsync(() ->
                                service.findByTermo(t)
                        , EXECUTOR)
                .orTimeout(TIME_OUT, TimeUnit.SECONDS)
                .thenApply(pessoas -> pessoas
                        .parallelStream()
                        .map(PessoaResponse::toResponse)
                        .collect(Collectors.toList())
                );
    }

    @GetMapping("/contagem-pessoas")
    @ResponseStatus(OK)
    public Long contarPessoas() {
        System.out.println("### No cache: " + service.count());
        System.out.println("### No banco: " + repository.count());
        return service.count();
    }
}
