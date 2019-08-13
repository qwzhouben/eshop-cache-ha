package com.zben.eshop.cache.ha.cache.local;

import java.util.HashMap;
import java.util.Map;

/**
 * @DESC: 本地品牌缓存
 * @author: jhon.zhou
 * @date: 2019/8/12 0012 17:47
 */
public class BrandCache {

    private static Map<Long, String> brandMap = new HashMap<>();
    private static Map<Long, Long> productBrandMap = new HashMap<>();

    static {
        brandMap.put(1L, "iphone");
        productBrandMap.put(-1L, 1L);
    }

    public static String getBrandName(Long brandId) {
        return brandMap.get(brandId);
    }

    public static Long getBrandId(Long productId) {
        return productBrandMap.get(productId);
    }
}
