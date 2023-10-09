package com.ccs.rinha.rinhajava21.api.model;

import com.ccs.rinha.rinhajava21.domain.entity.Pessoa;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PessaoInput(@NotNull String nome,
                          @NotNull String apelido,
                          LocalDate nascimento,
                          List<String> stack) {

    public Pessoa toPessoa() {
        return new Pessoa(nome, apelido, nascimento, stack);
    }
}
