package ru.voleshko.grocery.search.service.index;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticIndexService implements IndexService {

    private final RestHighLevelClient client;

    @Override
    @SneakyThrows
    public void index(String indexName, String documentId, String stringToIndex) {
        log.info("Indexing document with id=[{}] ( index=[{}] )", documentId, indexName);

        IndexRequest request = new IndexRequest(indexName, "_doc");
        request
                .source(stringToIndex, XContentType.JSON)
                .id(documentId);

        client.index(request, RequestOptions.DEFAULT);
    }

    @Override
    @SneakyThrows
    public void createIndex(String indexName, String mapping) {
        if (client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT)) {
            log.info("Index [{}] already exists, skipping it", indexName);
            return;
        }

        log.info("Creating index [{}]", indexName);
        client.indices().create(new CreateIndexRequest(indexName), RequestOptions.DEFAULT);
        PutMappingRequest putMappingRequest = new PutMappingRequest(indexName)
                .source(mapping, XContentType.JSON);
        client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
    }

    @SneakyThrows
    @Override
    public void delete(String indexName, String id) {
        DeleteRequest request = new DeleteRequest(indexName, id);
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Override
    public List<String> findAll(String indexName) {
        QueryBuilder query = QueryBuilders.matchAllQuery();
        return search(indexName, query);
    }

    @Override
    public List<String> find(String indexName, String[] fieldNames, String searchText) {
        QueryBuilder query = QueryBuilders
                .multiMatchQuery(searchText, fieldNames)
                .fuzziness(Fuzziness.TWO);
        return search(indexName, query);
    }

    @SneakyThrows
    private List<String> search(String indexName, QueryBuilder query) {
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(query)
                .sort(new ScoreSortBuilder().order(SortOrder.DESC))
                .sort("price", SortOrder.ASC)
                .size(20); // TODO: correct pagination required

        SearchRequest searchRequest = new SearchRequest(indexName).source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsString)
                .collect(toList());
    }

}
