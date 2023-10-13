package com.ccs.rinha.rinhajava21.api.model;

import com.ccs.rinha.rinhajava21.domain.entity.Pessoa;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record PessoaResponse(
        UUID id,
        String nome,
        String apelido,
        LocalDate nascimento,
        List<String> stack) {

    public static PessoaResponse toResponse(Pessoa pessoa) {
        return new PessoaResponse(pessoa.getId(),
                pessoa.getNome(),
                pessoa.getApelido(),
                pessoa.getNascimento(),
                Arrays.stream(pessoa.getStack().split(",")).toList()
        );
    }
}
