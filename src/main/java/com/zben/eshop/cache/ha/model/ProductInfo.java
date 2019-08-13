package com.zben.eshop.cache.ha.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @DESC: 商品信息
 * @author: jhon.zhou
 * @date: 2019/7/23 0023 17:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {

    private Long id;
    private String name;
    private Double price;
    private String pictureList;
    private String specification;
    private String service;
    private String color;
    private String size;
    private Long shopId;
    private String modifyTime;
    private Long brandId;
    private String brandName;
}
