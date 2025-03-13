package com.crawlix.crawlix.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;

@Configuration
public class SeleniumConfig {
    @Bean
    @Scope("prototype")//@Scope prototype을 사용하면 spring이 자동으로 관리하지 않
    public WebDriver webDriver() {
    	WebDriverManager.chromedriver().clearResolutionCache().setup();

        ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless");  // UI 없이 실행 (옵션)
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        WebDriver driver = new ChromeDriver(options);
        System.out.println("✅ WebDriver가 정상적으로 생성되었습니다.");
        return driver;
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("🛑 WebDriver 종료 중...");
        // 모든 WebDriver 프로세스 종료 (남아 있는 경우)
        try {
            Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T"); // Windows
            Runtime.getRuntime().exec("pkill -f chromedriver"); // Mac/Linux
        } catch (Exception e) {
            System.out.println("❌ WebDriver 종료 오류: " + e.getMessage());
        }
    }
}

