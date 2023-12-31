package com.ccs.rinha.rinhajava21.api.model;

import com.ccs.rinha.rinhajava21.domain.entity.Pessoa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PessaoInput(@NotBlank @Size(max = 100) String nome,
                          @NotBlank @Size(max = 32) String apelido,
                          LocalDate nascimento,
                          @NotEmpty List<@NotBlank @Size(max = 32) String> stack) {

    public Pessoa toPessoa() {

        return new Pessoa(UUID.randomUUID(), nome, apelido, nascimento,
                stack.stream()
                        .reduce("", (s, s2) -> s.concat(s2).concat(",")));
    }
}
