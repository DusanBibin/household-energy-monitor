package com.example.nvt.model.elastic;


import com.example.nvt.enumeration.RealEstateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

@Data
@Document(indexName = "realestate")
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class RealestateDoc {

    @Id
    private String id;

    private Long dbId;

    private RealEstateType type;

    @GeoPointField
    private String location;

    @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
    private String address;

    @Field(type = FieldType.Keyword)
    private String cityDocId;

    @Field(type = FieldType.Keyword)
    private String municipalityDocId;

    @Field(type = FieldType.Keyword)
    private String regionDocId;
}
