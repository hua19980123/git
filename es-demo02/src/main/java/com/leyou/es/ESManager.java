package com.leyou.es;

import com.google.gson.Gson;
import com.leyou.pojo.Item;
import org.apache.http.HttpHost;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ESManager {
    RestHighLevelClient client = null;
   Gson gson= new Gson();
    @Before
    public void init() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1", 9201, "http"),
                        new HttpHost("127.0.0.1", 9202, "http"),
                        new HttpHost("127.0.0.1", 9203, "http")));
    }
    //新增和修改
    @Test
    public void testDoc() throws Exception {
        Item item =new Item("1","小米9手机","手机","小米",1199.0,"wwww.com.cn.xioami.kk");
        //IndexRequest专门用来插入索引数据的对象
        IndexRequest request=new IndexRequest("item","docs",item.getId());
        //把对象转成json字符串
        //String jsonString=JSON.toJSONString(item);//fastJson转json的方式
        Gson gson = new Gson();
        String jsonString = gson.toJson(item);
        request.source(jsonString, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);

    }

    //删除
    @Test
    public void testDeleteDoc() throws Exception {
        DeleteRequest request=new DeleteRequest("item","docs","1");
        client.delete(request,RequestOptions.DEFAULT);
    }

    //批量新增
    @Test
    public void testBulkAddDoc() throws Exception {
        List<Item> list = new ArrayList<>();
        list.add(new Item("1","小米手机7","手机","小米",3299.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("2","坚果手机R1","手机","锤子",3699.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("3","华为META10","手机","华为",4499.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("4","小米 Mix2s","手机","小米",3299.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("5","荣耀 V10","手机","华为",2799.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("6","三星手机","手机","三星",3999.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("7","小米手机7","手机","小米",2789.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("8","兰博基尼手机","手机","兰博基尼",3499.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("9","联想手机","手机","联想",5299.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("10","诺基亚","手机","诺基亚",6799.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("11","红米","手机","红米",4999.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("12","vivo","手机","vivo",6699.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("13","OPPO手机","手机","OPPO",3699.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("14","华为xxxx","手机","华为",9039.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("15","苹果","手机","苹果",3899.00,"http//image.leyou.com/131223.jpg"));
        list.add(new Item("16","三星W2019","手机","三星",14699.00,"http//image.leyou.com/131223.jpg"));
        BulkRequest request=new BulkRequest();
        /*for (Item item : list) {
            IndexRequest indexRequest = new IndexRequest("item", "docs", item.getId());
            Gson gson = new Gson();
            String jsonString = gson.toJson(item);//gson转json的方式
            indexRequest.source(jsonString,XContentType.JSON);
            request.add(indexRequest);
        }*/
        //流式编程
        list.forEach(item -> {
            IndexRequest indexRequest=new IndexRequest("item","docs",item.getId());
            Gson gson = new Gson();//gson转json的方式
            String jsonString = gson.toJson(item);
            indexRequest.source(jsonString, XContentType.JSON);
            request.add(indexRequest);
        });
        client.bulk(request,RequestOptions.DEFAULT);
    }


    //查询
    @Test
    public void testSearch() throws Exception {
        //构建一个用来查询的对象
        SearchRequest searchRequest = new SearchRequest("item").types("docs");
        //构建查询条件
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //查询所有
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //term查询
       searchSourceBuilder.query(QueryBuilders.termQuery("category","手机"));
       searchSourceBuilder.aggregation(AggregationBuilders.terms("brandCount").field("price"));
       /* HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);*/
        //searchSourceBuilder.from(0);
       // searchSourceBuilder.size(3);
        //searchSourceBuilder.sort("price", SortOrder.DESC);
        //searchSourceBuilder.postFilter(QueryBuilders.termQuery("category","手机"));
        // searchSourceBuilder.fetchSource(new String[]{"id","title"},null);//包含
        // searchSourceBuilder.fetchSource(null,new String[]{"id","title"});//排除
        //分词查询
       // searchSourceBuilder.query(QueryBuilders.matchQuery("title","小米三星"));
        //统配符查询
        //searchSourceBuilder.query(QueryBuilders.wildcardQuery("title","*三*"));
        //模糊查询(容错)   设置偏离度
   //    searchSourceBuilder.query(QueryBuilders.fuzzyQuery("title","三米").fuzziness(Fuzziness.ONE));
        //区间查询//省略
        //放入到searchRequest中
        searchRequest.source(searchSourceBuilder);
        //执行查询
        SearchResponse searchResponse=client.search(searchRequest,RequestOptions.DEFAULT);
        SearchHits responseHits=searchResponse.getHits();
        System.out.println("总记录数:"+responseHits.getTotalHits());
        SearchHit[] searchHits = responseHits.getHits();
        for (SearchHit searchHit: searchHits) {
            //把json字符串转成对象
            //fastjson
            //Item item= JSON.parseObject(jsonString,Item.class);
            //gson
            String jsonString = searchHit.getSourceAsString();
            Item item = gson.fromJson(jsonString, Item.class);
            Aggregations aggregation=searchResponse.getAggregations();
            Terms terms = aggregation.get("brandCount");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            buckets.forEach(bucket->{
                System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());
            });
           /* Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("title");
            Text[] fragments = highlightField.getFragments();
            if(fragments!=null&&fragments.length>0){
                String title = fragments[0].toString();
                item.setTitle(title);//把item的title替换成高亮的数据
            }*/


            System.out.println(item);
        }
    }






    @After
    public void end() throws Exception {
        client.close();
    }
	
	@Test
	public void zhangsan(){
		System.out.println(张三");
	}
}
