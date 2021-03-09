package com.itcast.jd.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.itcast.jd.service.ContentService;

@RestController
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	
	@GetMapping("/parse/{keyWords}")
	public Boolean parse(@PathVariable("keyWords") String KeyWords) throws Exception{
		Boolean parseContent = contentService.parseContent(KeyWords);
		
		return parseContent;
	}
	@GetMapping("/search/{keyWords}/{pageNO}/{pageSize}")
	public List<Map<String, Object>> search(@PathVariable("keyWords")String keyWords,
										@PathVariable("pageNO")int pageNO,
										@PathVariable("pageSize")int pageSize) throws IOException{
		System.out.println(keyWords);
		return contentService.highlighter(keyWords, pageNO, pageSize);
	}
}
