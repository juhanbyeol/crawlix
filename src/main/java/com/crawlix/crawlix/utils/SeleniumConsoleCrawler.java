package com.crawlix.crawlix.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumConsoleCrawler {

    private static final String FILE_PATH = "/Users/juhanbyeol/spring/crawlix_files/";
    private static final String FILE_PREFIX = "OO_title";
    private static final String FILE_EXTENSION = ".txt";
    private static String savedFilePath = "";

    public static void main(String[] args) {
    	
    	/*
    	 * https://media.naver.com/press/088
    	 * 
    	 * 
		  정치 항목 : https://media.naver.com/press/088?sid=100
		  
		 경제 항목 : https://media.naver.com/press/088?sid=101
		 
		 https://media.naver.com/press/088?sid=102
		 
    	 * */
        // 🔴 크롬 드라이버 경로 설정 (필요 시 수정)
       // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        // 🔴 콘솔 입력 받기
        Scanner scanner = new Scanner(System.in);
        System.out.print("크롤링할 사이트 주소를 작성해주세요: ");
        String siteUrl = scanner.nextLine();

        System.out.print("이동할 탭 주소를 작성해주세요: ");
        String tabUrl = scanner.nextLine();

        WebDriver driver = new ChromeDriver();

        try {
            // 🔴 입력받은 URL로 사이트 접속 및 탭 이동
            driver.get(siteUrl);
            driver.navigate().to(tabUrl);

            // 🔴 press_edit_news_item 요소 리스트 가져오기
            List<WebElement> press_edit_news_item = driver.findElements(By.className("press_edit_news_item"));

            StringBuilder allTitles = new StringBuilder();

            for (int i = 0; i < press_edit_news_item.size(); i++) {
                WebElement item = press_edit_news_item.get(i);
                try {
                    WebElement titleElement = item.findElement(By.className("press_edit_news_title"));
                    String titleText = titleElement.getText();
                    allTitles.append("Item ").append(i + 1).append(": ").append(titleText).append("\n");
                    System.out.println("Item " + (i + 1) + " 타이틀: " + titleText);
                } catch (Exception e) {
                    System.out.println("Item " + (i + 1) + "에 타이틀이 존재하지 않습니다.");
                }
            }

            // 🔴 크롤링 데이터 파일 저장
            savedFilePath = saveTitleToFile(allTitles.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 🔴 드라이버 종료 및 저장 경로 안내
            driver.quit();
            System.out.println("🔴 크롤링한 데이터는 txt파일로 [" + savedFilePath + "]에 저장되었습니다.");
        }
    }

    /**
     * 🔴 텍스트를 파일에 저장 (자동 파일명 생성)
     */
    private static String saveTitleToFile(String text) throws IOException {
        int nextFileNumber = getNextFileNumber();
        String fileName = FILE_PREFIX + nextFileNumber + FILE_EXTENSION;
        File file = new File(FILE_PATH + fileName);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs(); // 디렉토리 없으면 생성
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(text);
            System.out.println("🔴 저장 완료: " + file.getAbsolutePath());
        }
        return file.getAbsolutePath();
    }

    /**
     * 🔴 디렉토리에서 파일명 확인 후, 다음 파일 숫자 계산
     */
    private static int getNextFileNumber() {
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
}
