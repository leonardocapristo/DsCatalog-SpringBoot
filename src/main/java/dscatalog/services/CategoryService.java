package dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dscatalog.dto.CategoryDTO;
import dscatalog.entities.Category;
import dscatalog.repositories.CategoryRepository;
import dscatalog.services.exceptions.DataBaseException;
import dscatalog.services.exceptions.SourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	


	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAll(PageRequest pageRequest ) {
		Page<Category> listCategory = repository.findAll(pageRequest);
		return listCategory.map(x -> new CategoryDTO(x));
	}
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> categoryOptinal = repository.findById(id);
		Category category = categoryOptinal.orElseThrow(() -> new SourceNotFoundException("ID não encontrado"));
		return new CategoryDTO(category);
	}
	
	
	@Transactional
	public CategoryDTO save(CategoryDTO dto) {

	    Category entity = new Category();
	    entity.setName(dto.getName());

	    entity = repository.save(entity);


	    return new CategoryDTO(entity);
	}

	
	@Transactional
	public CategoryDTO update(Long id,CategoryDTO dto) {
		
		try {
			Category entity = repository.getReferenceById(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new SourceNotFoundException("id nao encontrado");
		}
		
		
	}
	@Transactional
	public void delete(Long id) {
	    try {
	         repository.findById(id).orElseThrow(() -> new SourceNotFoundException("ID não encontrado"));

	    } catch (DataIntegrityViolationException e) {

	        throw new DataBaseException("Integridade do banco de dados violada");
	    }
	    
	    repository.deleteById(id);
	}

	

}
