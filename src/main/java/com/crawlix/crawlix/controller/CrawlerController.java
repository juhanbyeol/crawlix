package com.crawlix.crawlix.controller;

import com.crawlix.crawlix.service.SeleniumCrawlService;
import com.crawlix.crawlix.utils.ApiResponse;
import com.google.common.net.HttpHeaders;

import io.opentelemetry.sdk.resources.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController  // JSON 응답을 위해 사용
public class CrawlerController {

    @Autowired
    private SeleniumCrawlService crawlService;
    
    private WebDriver driver;

    @PostMapping("/crawl")
    public ApiResponse crawl(@RequestParam("siteUrl") String siteUrl,
                                     @RequestParam("tabUrl") String tabUrl) {
        Map<String, List<String>> response = new HashMap<>();
        try {
          	if(siteUrl.equals("")) {
          		return ApiResponse.failure("파라미터가 부족합니다.");
        	}
        	List<String> list = crawlService.crawlAndSave(siteUrl, tabUrl);
        	response.put("result", list);
        } catch (Exception e) {
            return ApiResponse.failure(e.getMessage());
        }
        return ApiResponse.success("OK", response);
    }
    
    @PostMapping("/crawl/start")
    public ApiResponse startCrawl(@RequestParam("siteUrl") String siteUrl) {
        try {
            driver = crawlService.initDriver(siteUrl);  // 초기 접속 후 대기
            return ApiResponse.success("OK", "초기 사이트 접속 완료. 추가 URL 입력 대기 중...");
        } catch (Exception e) {
            return ApiResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/crawl/continue")
    public ApiResponse continueCrawl(@RequestParam("tabUrl") String tabUrl) {
        try {
            List<String> result = crawlService.crawlAfterTabUrl(driver, tabUrl);  // 이후 URL 처리
            return ApiResponse.success("OK", Map.of("result", result));
        } catch (Exception e) {
            return ApiResponse.failure(e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();  // 크롤링 종료 후 드라이버 닫기
            }
        }
    }
    
    /**
     * 📜 저장된 파일 목록 반환 (JSON)
     */
    @GetMapping("/files/list")
    public List<String> getFileList() {
        return crawlService.getSavedFiles();
    }

    /**
     * 📥 특정 파일 다운로드
     */
    @GetMapping("/files/download")
    public ResponseEntity<FileSystemResource> downloadFile(@RequestParam("fileName") String fileName) {
        FileSystemResource fileResource = crawlService.getFileAsResource(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileResource);
    }
}