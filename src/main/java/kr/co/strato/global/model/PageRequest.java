package kr.co.strato.global.model;

import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class PageRequest {
    private int page; //페이지 순번
    private int size; //페이지 크기, Optional(default = 10)
    private String sort; //정렬(DESC, ASC), Optional(default = DESC)
    private String property; //정렬 기준, Optional(default = id)



    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size > MAX_SIZE ? MAX_SIZE : (size <= 0 ? DEFAULT_SIZE : size);
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public org.springframework.data.domain.PageRequest of(){
        Sort.Direction direction = Sort.Direction.DESC;
        if(StringUtils.hasText(sort)){
            if("ASC".equals(sort.toUpperCase())){
                direction = Sort.Direction.ASC;
            }
        }
        if(!StringUtils.hasText(property)){
            property = "id";
        }
        size = (size == 0) ? DEFAULT_SIZE : size;
        page = page <= 0 ? 1 : page;


        return org.springframework.data.domain.PageRequest.of(page - 1, size, direction, property);
    }
}
