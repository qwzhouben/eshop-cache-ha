package com.zben.eshop.cache.ha;

import com.zben.eshop.cache.ha.hystrix.command.GetProductInfoCommand;

/**
 * @DESC:
 * @author: jhon.zhou
 * @date: 2019/8/13 0013 15:25
 */
public class MultiCommandTest {

    public static void main(String[] args) {
        GetProductInfoCommand productInfoCommand =
                new GetProductInfoCommand(-1L);
        System.out.println(productInfoCommand.execute());
        System.out.println(new GetProductInfoCommand(-2l).execute());
    }
}
