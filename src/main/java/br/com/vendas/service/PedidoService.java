package br.com.vendas.service;

import br.com.vendas.dto.PedidoDTO;
import br.com.vendas.entity.Pedido;
import br.com.vendas.enums.StatusPedido;

import java.util.Optional;

public interface PedidoService {

    Pedido salvar(PedidoDTO pedidoDTO);

    Optional<Pedido> obterPedidoCompleto(Integer id);

    void atualizarStatus(Integer id, StatusPedido status);
}
