package com.portfolio.portafolio.Controller;

import com.portfolio.portafolio.Dto.dtoPersona;
import com.portfolio.portafolio.Entity.Persona;
import com.portfolio.portafolio.Security.Controller.Mensaje;
import com.portfolio.portafolio.Service.ImpPersonaService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = {"http://localhost:4200"})
public class PersonaController {

    @Autowired
    ImpPersonaService personaService;

    @GetMapping("/lista")
    public ResponseEntity<List<Persona>> list() {
        List<Persona> list = personaService.list();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Persona> getById(@PathVariable("id") int id) {
        if (!personaService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe el ID"), HttpStatus.BAD_REQUEST);
        }

        Persona persona = personaService.getOne(id).get();
        return new ResponseEntity(persona, HttpStatus.OK);
    }

    @GetMapping("/traer/perfil/{id}")
    public ResponseEntity<Persona> getProfileById(@PathVariable("id") int id) {
        if (!personaService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe el ID"), HttpStatus.BAD_REQUEST);
        }

        Persona persona = personaService.getOne(id).get();
        return new ResponseEntity(persona, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/crear")
    public ResponseEntity<?> create(@RequestBody dtoPersona dto) {
        try {
            Persona persona = new Persona(dto.getNombre(), dto.getApellido(), dto.getDescripcion(), dto.getImg());
            personaService.save(persona);
            return new ResponseEntity(new Mensaje("Persona creada"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new Mensaje("Error al crear persona"), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        if (!personaService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe persona con ese id"), HttpStatus.NOT_FOUND);
        }
        personaService.delete(id);
        return new ResponseEntity(new Mensaje("Persona eliminada"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody dtoPersona dtopersona) {
        if (!personaService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe el ID"), HttpStatus.NOT_FOUND);
        }
        if (personaService.existsByNombre(dtopersona.getNombre()) && personaService.getByNombre(dtopersona.getNombre()).get().getId() != id) {
            return new ResponseEntity(new Mensaje("Ese nombre ya existe"), HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dtopersona.getNombre())) {
            return new ResponseEntity(new Mensaje("El campo no puede estar vacio"), HttpStatus.BAD_REQUEST);
        }

        Persona persona = personaService.getOne(id).get();

        persona.setNombre(dtopersona.getNombre());
        persona.setApellido(dtopersona.getApellido());
        persona.setDescripcion(dtopersona.getDescripcion());
        persona.setImg(dtopersona.getImg());

        personaService.save(persona);

        return new ResponseEntity(new Mensaje("Persona actualizada"), HttpStatus.OK);
    }

}
