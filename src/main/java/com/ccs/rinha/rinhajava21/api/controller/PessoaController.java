package com.ccs.rinha.rinhajava21.api.controller;

import com.ccs.rinha.rinhajava21.api.model.PessaoInput;
import com.ccs.rinha.rinhajava21.domain.entity.Pessoa;
import com.ccs.rinha.rinhajava21.domain.repository.PessoaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping
public class PessoaController {

    private final PessoaRepository repository;
    private final PageRequest pageRequest = PageRequest.of(0, 50);

    public PessoaController(PessoaRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/pessoas")
    @ResponseStatus(CREATED)
    public CompletableFuture<Void> create(@RequestBody @Valid PessaoInput input) {

        return CompletableFuture.runAsync(() -> {
            try {
                repository.save(input.toPessoa());
            } catch (Exception e) {
                throw new ResponseStatusException(UNPROCESSABLE_ENTITY);
            }
        }, newVirtualThreadPerTaskExecutor());
    }

    @GetMapping("/pessoas/{id}")
    public CompletableFuture<Pessoa> findById(@PathVariable @NotNull UUID id) {
        return CompletableFuture
                .supplyAsync(() ->
                                repository.findByIdEager(id)
                                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND)),
                        newVirtualThreadPerTaskExecutor());
    }

    @GetMapping("/pessoas")
    @ResponseStatus(OK)
    public CompletableFuture<List<Pessoa>> findByTermo(@Nullable String nome,
                                                       @Nullable String apelido,
                                                       @Nullable String stack) {
        return CompletableFuture.supplyAsync(() ->
                        repository
                                .findByTermo(nome, apelido, stack, pageRequest)
                , newVirtualThreadPerTaskExecutor());
    }

    @GetMapping("/contagem-pessoas")
    @ResponseStatus(OK)
    public CompletableFuture<Long> contarPessoas() {
        return CompletableFuture
                .supplyAsync(() -> repository.count(), newVirtualThreadPerTaskExecutor());
    }
}
