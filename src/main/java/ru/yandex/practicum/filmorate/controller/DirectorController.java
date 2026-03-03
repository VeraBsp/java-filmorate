package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;
    private static final Logger log = LoggerFactory.getLogger(DirectorController.class);

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<Director> getAll() {
        log.info("Сформирован запрос на получение всех режиссеров");
        return directorService.getAll();
    }

    @GetMapping("{id}")
    public Director findById(@PathVariable("id") int id) {
        log.info("Сформирован запрос на получение режиссера с id={}", id);
        return directorService.findById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Получен запрос на добавление режиссера");
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Получен запрос на изменение режиссера");
        return directorService.update(director);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") int id) {
        log.info("Получен запрос на удадение режиссера с id={}", id);
        directorService.delete(id);
    }
}
