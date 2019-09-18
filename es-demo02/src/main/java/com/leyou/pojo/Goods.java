package com.leyou.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.lang.reflect.Type;

@Data//get  set  toString
@AllArgsConstructor//有参构造
@NoArgsConstructor//无参构造
@Document(indexName = "leyou",type = "goods",shards = 3,replicas=1)
public class Goods {
    @Field(type = FieldType.Keyword)
    private String id;     //不分词
    @Field(type = FieldType.Text,analyzer = "ik_max_word",index = true,store=true)
    private String title; //标题   分词
    @Field(type=FieldType.Keyword,index = true,store = true)
    private String category;// 分类   不分词
    @Field(type = FieldType.Keyword,index = true,store = true)
    private String brand; // 品牌     不分词
    @Field(type = FieldType.Keyword,index = true,store = true)
    private Double price; // 价格    不分词
    @Field(type = FieldType.Keyword,index = true,store = true)
    private String images; // 图片地址   不分词
}
