package com.crawlix.crawlix.controller;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;



@Controller
public class WebController {
	
//   @RequestMapping(value = "/{page}")
//   public ModelAndView mainAddService(HttpServletRequest HttpRequest, HttpSession session,
//         @PathVariable("page") String page, ModelMap model) throws Exception {
//      
////      model.addAttribute("config", config);
//      
//      return new ModelAndView("/" + page + ".html", model);
//   }
   
 /*  public static void main(String[] args) {
       WebDriver driver = new ChromeDriver();
       driver.get("https://media.naver.com/press/088");
//       System.out.println(driver.getTitle());
//       System.out.println(driver.getCurrentUrl());
//       driver.navigate().refresh();
       
//       driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
       //Longer way
       driver.navigate().to("https://media.naver.com/press/088?sid=100");
       
       
       //뉴스 리스트 그룹 탭 패널 _tab_panel
       List<WebElement> press_edit_news_item = driver.findElements(By.className("press_edit_news_item"));
       
       for (int i = 0; i < press_edit_news_item.size(); i++) {
           WebElement item = press_edit_news_item.get(i);

           // 하위 요소 중 'title' 클래스를 가진 요소 조회
           WebElement titleElement = item.findElement(By.className("title")); //이게 아님..

           // 타이틀 텍스트 출력
           System.out.println("Item " + (i + 1) + " 타이틀: " + titleElement.getText());
       }
   }*/
   
  
}
