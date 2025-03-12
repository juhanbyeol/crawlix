package com.crawlix.crawlix.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
        }
    }

}
