package com.crawlix.crawlix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CrawlRequestVo {
    private String keyword;     // 검색어
    private boolean useScroll;  // 스크롤 사용 여부
    private int scrollCount;    // 스크롤 횟수
    private int maxItems;       // 최대 크롤링할 상품 개수
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public boolean isUseScroll() {
		return useScroll;
	}
	public void setUseScroll(boolean useScroll) {
		this.useScroll = useScroll;
	}
	public int getScrollCount() {
		return scrollCount;
	}
	public void setScrollCount(int scrollCount) {
		this.scrollCount = scrollCount;
	}
	public int getMaxItems() {
		return maxItems;
	}
	public void setMaxItems(int maxItems) {
		this.maxItems = maxItems;
	}
    
    
}
