package com.crawlix.crawlix.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crawlix.crawlix.dto.CrawlRequestVo;
import com.crawlix.crawlix.dto.CrawlResponseVo;
import com.crawlix.crawlix.dto.ProductDto;
import com.crawlix.crawlix.service.NaverCrawlerService;
import com.crawlix.crawlix.service.NaverStoreCrawlerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/naver")
public class NaverCrawlerController {
	
//    @Autowired
	private final NaverCrawlerService naverCrawlerService;
    
//    @Autowired
    private final NaverStoreCrawlerService naverStoreCrawlerService;
	
	 // 명시적으로 생성자 추가
    @Autowired
    public NaverCrawlerController(NaverCrawlerService naverCrawlerService, 
            NaverStoreCrawlerService naverStoreCrawlerService) {
		this.naverCrawlerService = naverCrawlerService;
		this.naverStoreCrawlerService = naverStoreCrawlerService;
		}
    
    // 키워드 기반 네이버 쇼핑 크롤링 API
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchProducts(@RequestParam("keyword") String keyword) {
        List<String> products = naverCrawlerService.searchProducts(keyword);
        return ResponseEntity.ok(products); // JSON 응답 반환
    }
    
    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getDetail(@RequestParam("url") String url) {
    	Map<String, Object> map = naverCrawlerService.crawlDetailPage(url);
        return ResponseEntity.ok(map); // JSON 응답 반환
    }
    
    
    @PostMapping("/execute")
    public ResponseEntity<CrawlResponseVo> executeCrawling(@RequestBody CrawlRequestVo requestVo) {
        CrawlResponseVo response = naverStoreCrawlerService.startCrawling(requestVo);
        return ResponseEntity.ok(response);
    }
    
}
