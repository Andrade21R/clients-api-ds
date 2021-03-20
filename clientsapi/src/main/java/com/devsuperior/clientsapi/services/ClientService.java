package com.devsuperior.clientsapi.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.clientsapi.entities.Client;
import com.devsuperior.clientsapi.entities.dto.ClientDTO;
import com.devsuperior.clientsapi.repository.ClientRepository;
import com.devsuperior.clientsapi.services.exceptions.DataBaseException;
import com.devsuperior.clientsapi.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repo;

	@Transactional(readOnly = true)
	public Page<ClientDTO> findAllPaged(PageRequest paganation) {
		Page<Client> listDto = repo.findAll(paganation);
		return listDto.map(x -> new ClientDTO(x));
	}

	@Transactional(readOnly = true)
	public Client findById(Long id) {
		Optional<Client> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
	}

	@Transactional
	public Client insert(ClientDTO dto) {
		Client entity = dto.toEntity();
		entity = repo.save(entity);
		return entity;
	}

	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		try {
			Client enity = repo.getOne(id);
			enity.setName(dto.getName());
			enity.setIncome(dto.getIncome());
			enity.setBirthDate(dto.getBirthDate());
			enity.setChildren(dto.getChildren());
			enity.setCpf(dto.getCpf());
			repo.save(enity);
			return new ClientDTO(enity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id no found -> " + id);
		}
	}

	public void delete(Long id) {
		try {
			repo.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found -> " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		}
	}

}
