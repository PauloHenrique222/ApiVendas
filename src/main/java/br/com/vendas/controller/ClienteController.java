package br.com.vendas.controller;

import br.com.vendas.entity.Cliente;
import br.com.vendas.repository.ClienteRepository;
import io.swagger.annotations.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@Api("Api Clientes")
public class ClienteController {


    private ClienteRepository clienteRepository;


    public ClienteController (ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }


    @GetMapping("/{id}")
    @ApiOperation("Obter detalhe do cliente")
    @ApiResponses({
          @ApiResponse(code = 200, message = "Cliente encontrado"),
          @ApiResponse(code = 404, message = "cliente não encontado para o id informado")
    })
    public Cliente getClienteId(@PathVariable("id") Integer id){
        return clienteRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @GetMapping
    @ApiOperation("Salva novo cliente")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cliente salvo com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação")
    })
    public List<Cliente> findCliente(Cliente cliente){
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING );

        Example example = Example.of(cliente, matcher);
        return clienteRepository.findAll(example);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente saveCliente(@RequestBody @Valid Cliente cliente){
        return clienteRepository.save(cliente);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void updadeCliente(@RequestBody @Valid Cliente cliente){
        clienteRepository
                .findById(cliente.getId())
                .map(clienteExistente -> {
                            clienteRepository.save(cliente);
                            return clienteExistente;
                        }
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCliente(@PathVariable("id") Integer id){
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()){
            clienteRepository.delete(cliente.get());
            return;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
    }

 }

