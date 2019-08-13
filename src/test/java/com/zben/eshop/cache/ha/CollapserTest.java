package com.zben.eshop.cache.ha;

import com.zben.eshop.cache.ha.http.HttpClientUtils;

/**
 * @DESC:
 * @author: jhon.zhou
 * @date: 2019/8/13 0013 14:32
 */
public class CollapserTest {

    public static void main(String[] args) {
        //HttpClientUtils.sendGetRequest("http://localhost:8081/getProductInfos?ids=1,1,2,2,3,4");
        for (int i = 0; i < 100; i++) {
            HttpClientUtils.sendGetRequest("http://localhost:8081/getProductInfo/1");
        }
    }
}
