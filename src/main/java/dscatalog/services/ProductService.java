package dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dscatalog.dto.CategoryDTO;
import dscatalog.dto.ProductDTO;
import dscatalog.entities.Category;
import dscatalog.entities.Product;
import dscatalog.repositories.CategoryRepository;
import dscatalog.repositories.ProductRepository;
import dscatalog.services.exceptions.DataBaseException;
import dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(PageRequest pageRequest) {
		Page<Product> listProduct = repository.findAll(pageRequest);
		return listProduct.map(x -> new ProductDTO(x));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("ID não encontrado"));
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {

		Product entity = new Product();
		copyDtoToEntity(dto,entity);

		entity = repository.save(entity);

		return new ProductDTO(entity);
	}



	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {

		try {
			// ao inves de usar .findById(id) para evitar duas consultas no database
			Product entity = repository.getReferenceById(id);
			copyDtoToEntity(dto,entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);

		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("ID não encontrado para a atualização");
		}

	}

	@Transactional
	public void delete(Long id) {
	    if (!repository.existsById(id)) {
	        throw new ResourceNotFoundException("ID não encontrado para deletar");
	    }
	    try {
	        repository.deleteById(id);
	    } catch (DataIntegrityViolationException e) {
	        throw new DataBaseException("Não é possível deletar uma categoria associada a produtos");
	    }
	}

	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		
		for (CategoryDTO catDto : dto.getCategories()) {
			Category category = categoryRepository.getReferenceById(catDto.getId());
			entity.getCategories().add(category);
		}
		
	}
	
}
