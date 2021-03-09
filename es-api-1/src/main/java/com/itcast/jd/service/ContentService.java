package com.itcast.jd.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.itcast.jd.entity.Content;
import com.itcast.jd.utils.HtmlParseUtil;

@Service
public class ContentService {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	public Boolean parseContent(String KeyWords) throws IOException {
		List<Content> contents = HtmlParseUtil.parseJD(KeyWords);
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("2m");

		for (Content content : contents) {
			bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(content), XContentType.JSON));
		}
		BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		return !bulk.hasFailures();
	}

	public List<Map<String, Object>> searchPage(String keyWords, int pageNO, int pageSize) throws IOException {
		if (pageNO <= 1) {
			pageNO = 1;
		}
		SearchRequest searchRequest = new SearchRequest("jd_goods");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(pageNO);
		sourceBuilder.size(pageSize);

		TermQueryBuilder termQuery = QueryBuilders.termQuery("title", keyWords);
		sourceBuilder.query(termQuery);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest.source(sourceBuilder);

		SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		List<Map<String, Object>> list = new ArrayList<>();
		for (SearchHit documentFields : search.getHits().getHits()) {
			Map<String, Object> map = documentFields.getSourceAsMap();
			list.add(map);
		}
		return list;
	}

	public List<Map<String, Object>> highlighter(String keyWords, int pageNO, int pageSize) throws IOException {
		if (pageNO <= 1) {
			pageNO = 1;
		}
		SearchRequest searchRequest = new SearchRequest("jd_goods");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(pageNO);
		sourceBuilder.size(pageSize);

		TermQueryBuilder termQuery = QueryBuilders.termQuery("title", keyWords);
		sourceBuilder.query(termQuery);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		// 高亮
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("title");
		highlightBuilder.requireFieldMatch(false);
		highlightBuilder.preTags("<span style='color:red'>");
		highlightBuilder.postTags("</span>");
		sourceBuilder.highlighter(highlightBuilder);

		searchRequest.source(sourceBuilder);

		SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		List<Map<String, Object>> list = new ArrayList<>();
		for (SearchHit documentFields : search.getHits().getHits()) {
			Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
			HighlightField title = highlightFields.get("title");
			Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
			if (title != null) {
				Text[] fragments = title.fragments();
				String newTitle = "";
				for (Text fragment : fragments) {
					newTitle += fragment;
				}
				sourceAsMap.put("title", newTitle);
			}
			list.add(sourceAsMap);
		}
		return list;
	}

}
