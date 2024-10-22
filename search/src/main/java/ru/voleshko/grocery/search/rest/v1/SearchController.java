package ru.voleshko.grocery.search.rest.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.voleshko.grocery.search.config.elastic.ElasticProperties;
import ru.voleshko.grocery.search.service.index.ElasticIndexService;

import java.util.List;

// TODO: pagination required
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SearchController {

    private final ElasticIndexService indexService;
    private final ElasticProperties elasticProperties;

    @GetMapping("/search/all")
    public List<String> findAll() {
        return indexService.findAll(elasticProperties.getIndex().getName());
    }

    @PostMapping("/search/by-text")
    public List<String> find(@RequestBody String searchText) {
        return indexService.find(
                elasticProperties.getIndex().getName(),
                new String[]{},
                searchText
        );
    }

    @GetMapping("/search/by-category")
    public List<String> findByCategory(@RequestParam String category) {
        return indexService.find(
                elasticProperties.getIndex().getName(),
                new String[]{"categories.name"},
                category
        );
    }

}
