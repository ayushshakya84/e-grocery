package ru.voleshko.grocery.search.service.index;

import java.util.List;

public interface IndexService {

    void index(String indexName, String documentId, String stringToIndex);

    void createIndex(String indexName, String mapping);

    void delete(String indexName, String mapping);

    List<String> findAll(String indexName);

    List<String> find(String indexName, String[] fieldNames, String searchText);

}
