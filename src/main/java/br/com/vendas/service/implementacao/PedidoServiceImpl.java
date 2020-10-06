package br.com.vendas.service.implementacao;

import br.com.vendas.dto.ItemPedidoDTO;
import br.com.vendas.dto.PedidoDTO;
import br.com.vendas.entity.Cliente;
import br.com.vendas.entity.ItemPedido;
import br.com.vendas.entity.Pedido;
import br.com.vendas.entity.Produto;
import br.com.vendas.enums.StatusPedido;
import br.com.vendas.exception.PedidoNaoEncontradoException;
import br.com.vendas.exception.RegraNegocioException;
import br.com.vendas.repository.ClienteRepository;
import br.com.vendas.repository.ItemPedidoRepository;
import br.com.vendas.repository.PedidoRepository;
import br.com.vendas.repository.ProdutoRepository;
import br.com.vendas.service.PedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public PedidoServiceImpl (
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository,
            ItemPedidoRepository itemPedidoRepository
    ){
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO pedidoDTO) {

        Pedido pedido = new Pedido();
        pedido.setDataPedido(LocalDate.now());
        pedido.setTotal(pedidoDTO.getTotal());
        Cliente cliente = clienteRepository
                .findById(pedidoDTO.getCliente())
                .orElseThrow(()-> new RegraNegocioException("Código de Cliente inválido " +
                        pedidoDTO.getCliente()));
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);
        pedidoRepository.save(pedido);
        List<ItemPedido> itensPedido = converterItens(pedido, pedidoDTO.getItens());
        itemPedidoRepository.saveAll(itensPedido);
        pedido.setItens(itensPedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        //Também pode usar o findById
        return pedidoRepository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizarStatus(Integer id, StatusPedido status) {
        pedidoRepository.findById(id)
                .map(pedido ->{
                    pedido.setStatus(status);
                    return pedidoRepository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException());
    }


    private List<ItemPedido> converterItens(Pedido pedido, List<ItemPedidoDTO> itens){
        if(itens.isEmpty()){
           throw new RegraNegocioException("Não é possivel realizar um pedido sem itens");
        }
        return itens
                .stream()
                .map(itemPedidoDTO -> {
                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setPedido(pedido);
                    itemPedido.setQuantidade(itemPedidoDTO.getQuantidade());
                    Produto produto = produtoRepository
                            .findById(itemPedidoDTO.getProduto())
                            .orElseThrow(() ->
                                    new RegraNegocioException("Código do produto inválido "
                                            + itemPedidoDTO.getProduto()));
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());
    }
}
