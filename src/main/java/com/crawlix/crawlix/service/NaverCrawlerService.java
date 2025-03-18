package com.crawlix.crawlix.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ScriptTimeoutException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crawlix.crawlix.dto.ProductDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NaverCrawlerService {
    private WebDriver webDriver; // WebDriver를 한 번만 생성하고 재사용
    
    public WebDriver getWebDriver() {
        if (this.webDriver == null) { // 필요할 때만 새 WebDriver 생성
            this.webDriver = new ChromeDriver();
        }
        return this.webDriver;
    }

    public List<String> searchProducts(String keyword) {
        WebDriver webDriver = getWebDriver(); // 기존 WebDriver 사용
        String url = "https://search.shopping.naver.com/";
        webDriver.get(url);

        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));

            // 🔹 1. 불필요한 팝업 닫기
            try {
                WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("_buttonArea_button_7wo-V")));
                if (closeButton.isDisplayed()) {
                    closeButton.click();
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.out.println("✅ 팝업 닫기 버튼 없음");
            }

            // 🔹 2. 가려진 레이어 제거
            try {
                WebElement overlay = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("_layerWrapper_inner_z-IxL")));
                if (overlay.isDisplayed()) {
                    JavascriptExecutor js = (JavascriptExecutor) webDriver;
                    js.executeScript("arguments[0].style.display='none';", overlay);
                }
            } catch (Exception e) {
                System.out.println("✅ 가려진 레이어 없음");
            }

            // 🔹 3. 검색 입력 필드에 키워드 입력
            WebElement searchBox = webDriver.findElement(By.cssSelector("input._searchInput_search_text_83jy9"));
            searchBox.clear();
            searchBox.sendKeys(keyword);

            // 🔹 4. 검색 버튼 클릭
            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button._searchInput_button_search_wu9xq")));
            searchButton.click();

            // 🔹 5. 검색 결과 로딩 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("composite-card-list")));

            // 🔹 6. 검색 결과에서 상품 링크 추출
            WebElement results = webDriver.findElement(By.id("composite-card-list"));
            List<WebElement> productContainers = results.findElements(By.className("compositeCardContainer_composite_card_container__jr8cb"));
            List<String> productLinks = new ArrayList<>();

            for (WebElement container : productContainers) {
                try {
                    WebElement imgElement = container.findElement(By.tagName("img"));
                    WebElement linkElement = container.findElement(By.tagName("a"));
                    String productSrc = imgElement.getAttribute("src");
                    String productLink = linkElement.getAttribute("href");

                    if (productSrc != null && !productSrc.isEmpty()) {
                        productLinks.add(productSrc);
                    }
                    if (productLink != null && !productLink.isEmpty()) {
                        productLinks.add(productLink);
                    }
                } catch (Exception e) {
                    System.out.println("❌ 링크 추출 실패: " + e.getMessage());
                }
            }

            return productLinks;

        } catch (Exception e) {
            System.out.println("❌ 크롤링 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Object> crawlDetailPage(String url) {
        WebDriver webDriver = getWebDriver(); // 🟢 기존 WebDriver 사용
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        Map<String, Object> data = new HashMap<>();

        try {
            webDriver.get(url);

            /*
             * 1. 상세페이지에서 fieldset 태그 찾기
             */
            WebElement fieldsetElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("fieldset")));

            /*
             * 2. fieldset 하위에서 h3 태그 찾고 상품 제목 가져오기
             */
            WebElement titleElement = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(fieldsetElement, By.tagName("h3")));
            String title = titleElement.getText();
            data.put("title", title);

            /*
             * 3. class _1LY7DqCnwR 를 찾아 상품 금액 가져오기
             */
            WebElement priceElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("_1LY7DqCnwR")));
            String price = priceElement.getText();
            data.put("price", price);

            /*
             * 4. class _1gG8JHE9Zc을 찾은 뒤 존재하면 클릭하여 상세정보 펼치기
             */
            try {
                WebElement detailToggleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("_1gG8JHE9Zc")));
                if (detailToggleElement.isDisplayed()) {
                    detailToggleElement.click();
                    //wait.until(ExpectedConditions.attributeContains(detailToggleElement, "class", "expanded"));
                }
            } catch (TimeoutException e) {
                System.out.println("🔹 상세 정보 펼치기 버튼이 존재하지 않음.");
            }

            /*
             * 5. class se-main-container을 찾아 하위 DOM을 문자열 그대로 저장하기
             */
            WebElement contentElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("_9F9CWn02VE")));
            String contents = contentElement.getAttribute("innerHTML");
            data.put("contents", contents);
            data.put("url", url);

        } catch (TimeoutException e) {
            data.put("error", "⏳ 요소를 찾을 수 없음 (시간 초과): " + e.getMessage());
        } catch (NoSuchElementException e) {
            data.put("error", "❌ 요소를 찾을 수 없음: " + e.getMessage());
        } catch (Exception e) {
            data.put("error", "❌ 크롤링 실패: " + e.getMessage());
        } finally {
        	quitWebDriver();	
		}

        return data;
    }

    public void quitWebDriver() {
        if (this.webDriver != null) {
            this.webDriver.quit(); // 🔴 명시적으로 종료할 때만 호출
            this.webDriver = null;
            System.out.println("✅ WebDriver가 정상적으로 종료되었습니다.");
        }
    }

}
