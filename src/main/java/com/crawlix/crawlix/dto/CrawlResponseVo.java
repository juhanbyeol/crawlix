package com.crawlix.crawlix.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CrawlResponseVo {
    private List<Item> items;

    public CrawlResponseVo() {
        this.items = new ArrayList<>();
    }

    public CrawlResponseVo(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private String title;
        private String price;
        private String detailUrl;
        private String imgUrl;

        public Item() {
        }

        public Item(String title, String price, String detailUrl, String imgUrl) {
            this.title = title;
            this.price = price;
            this.detailUrl = detailUrl;
            this.imgUrl = imgUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDetailUrl() {
            return detailUrl;
        }

        public void setDetailUrl(String detailUrl) {
            this.detailUrl = detailUrl;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "title='" + title + '\'' +
                    ", price='" + price + '\'' +
                    ", detailUrl='" + detailUrl + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    '}';
        }
    }
}