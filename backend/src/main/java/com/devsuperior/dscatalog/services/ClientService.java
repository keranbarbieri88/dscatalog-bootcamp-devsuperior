package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ClientDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Client;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ClientRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourcesNotFoundException;

/* 
 * Anotation - registra a classe como um componente que vai participar 
 * do sistema de injeção de depeência do String
 */
@Service
public class ClientService {
	
	/*
	 * Através desta dependência conseguiremos acessar o Repository e 
	 * chamar no banco de dados as categorias.
	 */
	@Autowired
    private ClientRepository repository;
	
	@Autowired
	private ProductRepository productRepository;
    
	/*
	 * Transactional garante a integridade da transação
	 */
	@Transactional(readOnly = true)
	public Page<ClientDTO> findAllPaged(PageRequest pageRequest){
		Page<Client> list =  repository.findAll(pageRequest);
		/*
		 * Expressão lambda utilizando funções a alta ordem (stream).
		 * map transforma o elemento orginal no caso Client em ClientDTO
		 * 
		 */
		return list.map(x -> new ClientDTO(x));
		}

	@Transactional(readOnly = true)
	public ClientDTO findById(Long id)  {
		Optional<Client> obj = repository.findById(id);
		Client entity = obj.orElseThrow(() -> new ResourcesNotFoundException("Entidade não encontrada!"));
		return new ClientDTO(entity, entity.getProducts());
	
}

	@Transactional
	public ClientDTO insert(ClientDTO dto) {
		Client entity = new Client();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ClientDTO(entity);
		
	}


	@Transactional
	public ClientDTO update(Long id ,ClientDTO dto) {
		try {
			Client entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ClientDTO(entity);
		}
		catch (EntityNotFoundException e){
			throw new ResourcesNotFoundException("Id não encontrado!" + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e){
			throw new ResourcesNotFoundException("Id não encontrado!" + id);
			
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de integridade!");
		}
	}
	private void copyDtoToEntity(ClientDTO dto, Client entity) {
		entity.setName(dto.getName());
		entity.setCpf(dto.getCpf());
		entity.setIncome(dto.getIncome());
		entity.setBirthDate(dto.getBirthDate());
		entity.setChildren(dto.getChildren());

		entity.getProducts().clear();
		for(ProductDTO prodDto : dto.getProducts()) {
			Product product = productRepository.getOne(prodDto.getId());
			entity.getProducts().add(product);
		}
	}
}
