package br.com.vendas.controller;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ApiError {

    private List<String> error;

    public ApiError(String messageError) {
        this.error = Arrays.asList(messageError);
    }

    public ApiError(List<String> error) {
        this.error = error;
    }
}
