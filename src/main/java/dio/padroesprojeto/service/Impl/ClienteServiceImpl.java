package dio.padroesprojeto.service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dio.padroesprojeto.model.Cliente;
import dio.padroesprojeto.model.ClienteRepository;
import dio.padroesprojeto.model.Endereco;
import dio.padroesprojeto.model.EnderecoRepository;
import dio.padroesprojeto.service.ClienteService;
import dio.padroesprojeto.service.ViaCepService;

@Service
public class ClienteServiceImpl implements ClienteService{
    
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;
    
    @Override
    public Iterable<Cliente> buscarTodos() {
        //buscar todos os clientes
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        //buscar cliente por id
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.get();
    }

    @Override
    public void inserir(Cliente cliente) {
        //verificar se o endereço do cliente existe
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            //caso não exista, integrar com viacep e persistir o retorno
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });
        cliente.setEndereco(endereco);
        //inserir cliente, vinculando o endereço (novo ou existente)
        clienteRepository.save(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        // Buscar Cliente por ID, caso exista
        Optional<Cliente> clienteBd = clienteRepository.findById(id);
        if (clienteBd.isPresent()) {
            salvarClienteComCep(cliente);
        }
    }
    
    @Override
    public void deletar(Long id) {
        // Deletar Cliente por ID.
		clienteRepository.deleteById(id);
    }

    private void salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clienteRepository.save(cliente);
	}
}
