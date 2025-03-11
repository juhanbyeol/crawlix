package com.crawlix.crawlix.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import io.github.bonigarcia.wdm.WebDriverManager;

@Configuration
public class SeleniumConfig {
    @Bean
    @Scope("prototype")
    public WebDriver webDriver() {
        //System.setProperty("webdriver.chrome.driver", "/Users/juhanbyeol/spring/crawlix_files/chromedriver/chromedriver"); // Mac 예제
//        try {
            //WebDriverManager.chromedriver().setup(); // ChromeDriver 자동 다운로드
        	WebDriverManager.chromedriver().clearResolutionCache().setup();

            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless");  // UI 없이 실행 (옵션)
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");

            WebDriver driver = new ChromeDriver(options);
            System.out.println("✅ WebDriver가 정상적으로 생성되었습니다.");
            return driver;
//            return new ChromeDriver(options);
//        } catch (Exception e) {
//            System.err.println("❌ WebDriver 생성 중 오류 발생: " + e.getMessage());
//            throw new RuntimeException("WebDriver 생성 실패", e);
//        }
    }
}

