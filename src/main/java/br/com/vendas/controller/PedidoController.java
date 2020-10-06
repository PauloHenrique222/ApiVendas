package br.com.vendas.controller;

import br.com.vendas.dto.InformacaoItensDTO;
import br.com.vendas.dto.InformacaoPedidoDTO;
import br.com.vendas.dto.NovoStatusDTO;
import br.com.vendas.dto.PedidoDTO;
import br.com.vendas.entity.ItemPedido;
import br.com.vendas.entity.Pedido;
import br.com.vendas.enums.StatusPedido;
import br.com.vendas.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService){
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer save(@RequestBody @Valid PedidoDTO pedidoDTO){
        Pedido pedido = pedidoService.salvar(pedidoDTO);
        return pedido.getId();
    }

    @GetMapping("/{id}")
    public InformacaoPedidoDTO getById(@PathVariable("id") Integer id){
        return pedidoService
                .obterPedidoCompleto(id)
                .map(pedido -> converterPedido(pedido))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable("id") Integer id, @RequestBody NovoStatusDTO novoStatus){
        String status = novoStatus.getStatus();
        pedidoService.atualizarStatus(id, StatusPedido.valueOf(status));
    }

    private InformacaoPedidoDTO converterPedido(Pedido pedido){
       return InformacaoPedidoDTO.builder()
                .id(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .itens(converterItens(pedido.getItens()))
                .build();
    }

    private List<InformacaoItensDTO> converterItens(List<ItemPedido> itens){
        if(CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }
        return itens.stream().map(item ->
            InformacaoItensDTO
                    .builder()
                    .descricao(item.getProduto().getDescricao())
                    .quantidade(item.getQuantidade())
                    .precoUnitario(item.getProduto().getPreco())
                    .build()
        ).collect(Collectors.toList());
    }
}
