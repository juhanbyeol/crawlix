package com.crawlix.crawlix.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.crawlix.crawlix.dto.CrawlRequestVo;
import com.crawlix.crawlix.dto.CrawlResponseVo;

import lombok.RequiredArgsConstructor;

@Service
public class NaverStoreCrawlerService {

    private static final String NAVER_STORE_URL = "https://search.shopping.naver.com/search/all";
    private static final ThreadLocal<WebDriver> threadLocalWebDriver = new ThreadLocal<>();

    private WebDriver webDriver; // WebDriver를 한 번만 생성하고 재사용
    
    public WebDriver getWebDriver() {
        if (this.webDriver == null) { // 필요할 때만 새 WebDriver 생성
            this.webDriver = new ChromeDriver();
        }
        return this.webDriver;
    }
    
    public void quitWebDriver() {
        if (this.webDriver != null) {
            this.webDriver.quit(); // 🔴 명시적으로 종료할 때만 호출
            this.webDriver = null;
            System.out.println("✅ WebDriver가 정상적으로 종료되었습니다.");
        }
    }

    /**
     * 네이버 스토어 크롤링 실행
     */
    public CrawlResponseVo startCrawling(CrawlRequestVo requestVo) {
        WebDriver driver = getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<CrawlResponseVo.Item> results = new ArrayList<>();

        try {
            // 🔹 1. URL 접근
            driver.get(NAVER_STORE_URL);

            // 🔹 2. 키워드 입력 및 검색 실행
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input._searchInput_search_text_83jy9")));
            searchBox.clear();
            searchBox.sendKeys(requestVo.getKeyword());
            searchBox.sendKeys(Keys.ENTER);

            // 🔹 3. 검색 결과 로딩 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("composite-card-list")));

            // 🔹 4. 스크롤 처리 (필요 시)
            if (requestVo.isUseScroll()) {
                for (int i = 0; i < requestVo.getScrollCount(); i++) {
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1000);");
                    Thread.sleep(1200);
                }
            }

            // 🔹 5. 상품 목록 크롤링
            List<WebElement> productElements = driver.findElements(By.cssSelector(".basicList_item__2XT81"));

            for (WebElement product : productElements) {
                try {
                    String title = product.findElement(By.cssSelector(".basicList_title__3P9Q7 a")).getText();
                    String price = product.findElement(By.cssSelector(".price_num__2WUXn")).getText();
                    String detailUrl = product.findElement(By.cssSelector(".basicList_title__3P9Q7 a")).getAttribute("href");
                    String imgUrl = product.findElement(By.cssSelector(".thumbnail_thumb__3Agq6 img")).getAttribute("src");

                    // 🔹 객체 생성 후 리스트 추가
                    CrawlResponseVo.Item item = new CrawlResponseVo.Item(title, price, detailUrl, imgUrl);
                    results.add(item);

                    // 요청된 개수만큼 수집 후 종료
                    if (results.size() >= requestVo.getMaxItems()) break;
                } catch (NoSuchElementException ignored) {
                    System.out.println("❌ 일부 상품 정보 누락 - 계속 진행");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ 크롤링 중 오류 발생: " + e.getMessage());
        } finally {
            quitWebDriver();
        }

        return new CrawlResponseVo(results);
    }
}