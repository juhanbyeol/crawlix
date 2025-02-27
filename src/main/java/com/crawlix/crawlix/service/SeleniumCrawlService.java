package com.crawlix.crawlix.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.opentelemetry.sdk.resources.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SeleniumCrawlService {

    private static final String FILE_PATH = "/Users/juhanbyeol/spring/crawlix_files/";
    private static final String FILE_PREFIX = "OO_title";
    private static final String FILE_EXTENSION = ".txt";
    
    public WebDriver initDriver(String siteUrl) {
        WebDriver driver = new ChromeDriver();
        driver.get(siteUrl);
        return driver;
    }
    
    public List<String> crawlAfterTabUrl(WebDriver driver, String tabUrl) throws IOException {
        List<String> titles = new ArrayList<>();
        StringBuilder allTitles = new StringBuilder();
        List<String> list = new ArrayList<>();
        String savedFilePath = "";
        
        driver.navigate().to(tabUrl);

        List<WebElement> newsItems = driver.findElements(By.className("press_edit_news_item"));
        for (int i = 0; i < newsItems.size(); i++) {
            try {
                WebElement titleElement = newsItems.get(i).findElement(By.className("press_edit_news_title"));
                titles.add(titleElement.getText());
                
                String titleText = titleElement.getText();
                allTitles.append("Item ").append(i + 1).append(": ").append(titleText).append("\n");
                list.add(titleText);
            } catch (Exception e) {
            	 allTitles.append("Item ").append(i + 1).append(": 타이틀 없음\n");
                titles.add("데이터 없음");
            }
        }
        savedFilePath = saveTitleToFile(allTitles.toString());
        return titles;
    }

    public List<String> crawlAndSave(String siteUrl, String tabUrl) throws IOException {
        // 필요에 따라 아래와 같이 드라이버 경로 설정
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        WebDriver driver = new ChromeDriver();
        String savedFilePath = "";
        List<String> list = new ArrayList<>();
        try {
            // 입력받은 사이트로 이동 후 탭 이동
            driver.get(siteUrl);
            driver.navigate().to(tabUrl);

            // press_edit_news_item 요소 리스트 가져오기
            List<WebElement> newsItems = driver.findElements(By.className("press_edit_news_item"));
            StringBuilder allTitles = new StringBuilder();

            for (int i = 0; i < newsItems.size(); i++) {
                WebElement item = newsItems.get(i);
                try {
                    WebElement titleElement = item.findElement(By.className("press_edit_news_title"));
                    String titleText = titleElement.getText();
                    allTitles.append("Item ").append(i + 1).append(": ").append(titleText).append("\n");
                    list.add(titleText);
                } catch (Exception e) {
                    allTitles.append("Item ").append(i + 1).append(": 타이틀 없음\n");
                	list.add("데이터 없음");
                }
            }
            savedFilePath = saveTitleToFile(allTitles.toString());
        } finally {
            driver.quit();
        }
        return list;
    }

    // 텍스트를 파일에 저장 (자동 파일명 생성)
    private String saveTitleToFile(String text) throws IOException {
        int nextFileNumber = getNextFileNumber();
        String fileName = FILE_PREFIX + nextFileNumber + FILE_EXTENSION;
        File file = new File(FILE_PATH + fileName);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(text);
        }
        return file.getAbsolutePath();
    }

    // 디렉토리에서 파일명 확인 후, 다음 파일 번호 계산
    private int getNextFileNumber() {
        File directory = new File(FILE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        int maxNumber = 0;
        Pattern pattern = Pattern.compile(FILE_PREFIX + "(\\d+)" + FILE_EXTENSION);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    int number = Integer.parseInt(matcher.group(1));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                }
            }
        }
        return maxNumber + 1;
    }
 
    /**
     * 📂 저장된 파일 목록 조회
     */
    public List<String> getSavedFiles() {
        File directory = new File(FILE_PATH);
        if (!directory.exists()) {
            return new ArrayList<>();
        }

        List<String> fileNames = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    /**
     * 📥 파일 다운로드 리소스 반환
     */
    public FileSystemResource getFileAsResource(String fileName) {
        File file = new File(FILE_PATH + fileName);
        if (!file.exists() || !file.isFile()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");
        }
        return new FileSystemResource(file);
    }
}