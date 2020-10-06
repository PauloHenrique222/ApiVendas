package br.com.vendas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformacaoItensDTO {

    private String descricao;
    private BigDecimal precoUnitario;
    private Integer quantidade;

}
