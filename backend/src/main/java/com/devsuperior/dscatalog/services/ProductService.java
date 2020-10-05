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

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourcesNotFoundException;

/* 
 * Anotation - registra a classe como um componente que vai participar 
 * do sistema de injeção de depeência do String
 */
@Service
public class ProductService {
	
	/*
	 * Através desta dependência conseguiremos acessar o Repository e 
	 * chamar no banco de dados as categorias.
	 */
	@Autowired
    private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
    
	/*
	 * Transactional garante a integridade da transação
	 */
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		Page<Product> list =  repository.findAll(pageRequest);
		/*
		 * Expressão lambda utilizando funções a alta ordem (stream).
		 * map transforma o elemento orginal no caso Product em ProductDTO
		 * 
		 */
		return list.map(x -> new ProductDTO(x));
		}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id)  {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourcesNotFoundException("Entidade não encontrada!"));
		return new ProductDTO(entity, entity.getCategories());
}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
		
	}


	@Transactional
	public ProductDTO update(Long id ,ProductDTO dto) {
		try {
			Product entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
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
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for(CategoryDTO catDto : dto.getCategories()) {
			Category category = categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category);
		}
	}
}
