package com.devsuperior.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;

/* 
 * Anotation - registra a classe como um componente que vai participar 
 * do sistema de injeção de depeência do String
 */
@Service
public class CategoryService {
	
	/*
	 * Através desta dependência conseguiremos acessar o Repository e 
	 * chamar no banco de dados as categorias.
	 */
	@Autowired
    private CategoryRepository repository;
    
	/*
	 * Transactional garante a integridade da transação
	 */
	@Transactional(readOnly = true)
	public List<Category> findAll(){
		return repository.findAll();
		
	}
}
