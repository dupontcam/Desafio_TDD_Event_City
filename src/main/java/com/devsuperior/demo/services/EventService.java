package com.devsuperior.demo.services;

import com.devsuperior.demo.dto.EventDTO;
import com.devsuperior.demo.entities.City;
import com.devsuperior.demo.entities.Event;
import com.devsuperior.demo.repositories.EventRepository;
import com.devsuperior.demo.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Transactional(readOnly = true)
    public Page<EventDTO> findAllPaged(Pageable pageable) {
        Page<Event> page = repository.findAll(pageable);
        return page.map(x -> new EventDTO(x));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EventDTO update(Long id, EventDTO dto) {
        try {
            // Carregar a entidade do banco de dados
            Event entity = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));

            // Atualizar os campos da entidade com os valores do DTO
            entity.setName(dto.getName());
            entity.setDate(dto.getDate());
            entity.setUrl(dto.getUrl());
            entity.setCity(new City(dto.getCityId(), null));

            // Salvar a entidade atualizada
            entity = repository.save(entity);

            // Retornar o DTO da entidade atualizada
            return new EventDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }
}
