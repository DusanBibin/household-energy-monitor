package com.example.nvt.repository.elastic;


import com.example.nvt.model.elastic.CityDoc;
import com.example.nvt.model.elastic.RegionDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionDocRepository extends ElasticsearchRepository<RegionDoc, String> {
}
