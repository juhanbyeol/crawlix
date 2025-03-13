package com.crawlix.crawlix.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crawlix.crawlix.dto.ProductDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NaverCrawlerService {
	
	private final WebDriver webDriver; // Bean으로 주입
	
    @Autowired
    public NaverCrawlerService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
    
    

    public List<String> searchProducts(String keyword) {
        String url = "https://search.shopping.naver.com/";
        webDriver.get(url);

        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));

            // 🔹 1. 불필요한 팝업 닫기 (_buttonArea_button_7wo-V 클릭)
            try {
                WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("_buttonArea_button_7wo-V")));
                if (closeButton.isDisplayed()) {
                    System.out.println("❌ 팝업 닫기 버튼 발견! 클릭 시도");
                    closeButton.click();
                    Thread.sleep(1000); // 팝업 닫히는 시간 대기
                }
            } catch (Exception e) {
                System.out.println("✅ 팝업 닫기 버튼 없음");
            }

            // 🔹 2. 가려진 레이어 제거 (_layerWrapper_inner_z-IxL)
            try {
                WebElement overlay = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("_layerWrapper_inner_z-IxL")));
                if (overlay.isDisplayed()) {
                    System.out.println("❌ 가려진 레이어 발견! 닫기 시도");
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

            // 🔹 4. 검색 버튼이 활성화될 때까지 기다리기 & 클릭
            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button._searchInput_button_search_wu9xq")));
            searchButton.click();

            // 🔹 5. 검색 결과 로딩 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("composite-card-list")));

            // 🔹 6. 결과 목록 가져오기
            WebElement results = webDriver.findElement(By.id("composite-card-list"));

            // 🔹 7. "compositeCardContainer_composite_card_container__jr8cb" 클래스 요소 찾기
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
        } finally {
            webDriver.quit(); // 🔴 요청이 끝난 후 WebDriver 종료
            System.out.println("✅ WebDriver가 정상적으로 종료되었습니다.");
        }
    }
    
    public Map<String, Object> crawlDetailPage(String url) {
        Map<String, Object> data = new HashMap<>();

        try {
            webDriver.get(url);
            Thread.sleep(2000); // 페이지 로딩 대기
            
            /*
             *1. 상세페이지에서 태그 fieldset 를 찾기 
             *2. 1번에서 찾은 태그 하위에 h3 태그 찾은뒤 text 가져오기 (상세페이지 상품 제목임)
             *3. 그리고 이어서 class _1LY7DqCnwR 를 찾은 뒤 text 가져오기상품 금액임)
             *4. 그 다음에는 class _1gG8JHE9Zc 을 찾은 뒤에 만약 존재한 다면 클릭이벤트 처리해서 상세정보 펼치기 
             *5. class se-main-container을 찾은 뒤에 하위 dom 을 문자열 그대로 담기
             *6. 지금까지 처리된 데이터를 title, price, contents 라는 key 로 map에 담아서 리턴하
             * */

            /*
             * 1. 상세페이지에서 fieldset 태그 찾기
             */
            WebElement fieldsetElement = webDriver.findElement(By.tagName("fieldset"));

            /*
             * 2. fieldset 하위에서 h3 태그 찾고 상품 제목 가져오기
             */
            WebElement titleElement = fieldsetElement.findElement(By.tagName("h3"));
            String title = titleElement.getText();
            data.put("title", title);

            /*
             * 3. class _1LY7DqCnwR 를 찾아 상품 금액 가져오기
             */
            WebElement priceElement = webDriver.findElement(By.className("_1LY7DqCnwR"));
            String price = priceElement.getText();
            data.put("price", price);

            /*
             * 4. class _1gG8JHE9Zc을 찾은 뒤 존재하면 클릭하여 상세정보 펼치기
             */
            try {
                WebElement detailToggleElement = webDriver.findElement(By.className("_1gG8JHE9Zc"));
                if (detailToggleElement.isDisplayed()) {
                    detailToggleElement.click();
                    Thread.sleep(1000); // 클릭 후 로딩 대기
                }
            } catch (NoSuchElementException e) {
                System.out.println("🔹 상세 정보 펼치기 버튼이 존재하지 않음.");
            }

            /*
             * 5. class se-main-container을 찾아 하위 DOM을 문자열 그대로 저장하기
             */
            WebElement contentElement = webDriver.findElement(By.className("se-main-container"));
            String contents = contentElement.getAttribute("innerHTML"); // HTML 그대로 가져오기
            data.put("contents", contents);

            data.put("url", url);

        } catch (NoSuchElementException e) {
            data.put("error", "❌ 요소를 찾을 수 없음: " + e.getMessage());
        } catch (Exception e) {
            data.put("error", "❌ 크롤링 실패: " + e.getMessage());
        } finally {
            webDriver.quit(); // 🔴 요청이 끝난 후 WebDriver 종료
            System.out.println("✅ WebDriver가 정상적으로 종료되었습니다.");
        }

        return data;
    }

}
