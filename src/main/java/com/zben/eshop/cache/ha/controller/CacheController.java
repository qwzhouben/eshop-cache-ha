package com.zben.eshop.cache.ha.controller;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixObservableCommand;
import com.zben.eshop.cache.ha.http.HttpClientUtils;
import com.zben.eshop.cache.ha.hystrix.command.GetBrandNameCommand;
import com.zben.eshop.cache.ha.hystrix.command.GetProductInfoCommand;
import com.zben.eshop.cache.ha.hystrix.command.GetProductInfosCollapser;
import com.zben.eshop.cache.ha.hystrix.command.GetProductInfosCommand;
import com.zben.eshop.cache.ha.model.ProductInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @DESC:
 * @author: jhon.zhou
 * @date: 2019/8/9 0009 9:34
 */
@RestController
public class CacheController {

    /**
     * 变更商品信息，应该从kafka中消费
     * @param productId
     * @return
     */
    @GetMapping("/change/product/{productId}")
    public String changeProduct(@PathVariable Long productId) {
        //从商品服务拿出商品最新信息
        String response = HttpClientUtils.sendGetRequest("http://127.0.0.1:8082/getProductInfo/" + productId);
        System.out.println("【response】:" + response);
        return "success";
    }

    /**
     * nginx开始，各级缓存都失效了，nginx发送很多的请求直接到缓存服务要求拉取最原始的数据
     * @param productId
     * @return
     */
    @GetMapping("/getProductInfo/{productId}")
    public ProductInfo getProductInfo(@PathVariable Long productId) {
        //从商品服务拿出商品最新信息
        HystrixCommand<ProductInfo> productInfoHystrixCommand =
                new GetProductInfoCommand(productId);
        //同步调用
        ProductInfo productInfo = productInfoHystrixCommand.execute();
        System.out.println("【productInfo】" + productInfo);

        HystrixCommand<String> brandCommand =
                new GetBrandNameCommand(productInfo.getBrandId());
        String brandName = brandCommand.execute();
        productInfo.setBrandName(brandName);
        System.out.println("【productInfo】" + productInfo);
        //异步调用
//		Future<ProductInfo> future = getProductInfoCommand.queue();
//		try {
//			Thread.sleep(1000);
//			System.out.println(future.get());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

        return productInfo;
    }

    /**
     * nginx开始，各级缓存都失效了，nginx发送很多的请求直接到缓存服务要求拉取最原始的数据
     * @param ids
     * @return
     */
    @GetMapping("/getProductInfos")
    public String getProductInfo(String ids) {
        List<Long> productIds = new ArrayList<>();

        String[] split = ids.split(",");
        for (String s : split) {
            productIds.add(Long.valueOf(s));
        }
        /*HystrixObservableCommand<ProductInfo> observableCommand =
                new GetProductInfosCommand(productIds);
        Observable<ProductInfo> observe = observableCommand.observe();

        observe.subscribe(new Observer<ProductInfo>() {
            @Override
            public void onCompleted() {
                System.out.println("获取完了所有的商品数据");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(ProductInfo productInfo) {
                System.out.println(productInfo);

            }
        });*/
        //测试缓存中有没有数据
        /*for (Long productId : productIds) {
            HystrixCommand<ProductInfo> productInfoHystrixCommand =
                    new GetProductInfoCommand(productId);
            ProductInfo productInfo = productInfoHystrixCommand.execute();
            System.out.println(productInfo);
            //测试缓存中有没有数据
            System.out.println(productInfoHystrixCommand.isResponseFromCache());
        }*/
        //批量处理，request collapser
        //异步执行
        List<Future<ProductInfo>> futureList = new ArrayList<>();
        for (String productId : ids.split(",")) {
            GetProductInfosCollapser collapser =
                    new GetProductInfosCollapser(Long.valueOf(productId));
            futureList.add(collapser.queue());
        }


        try {
            for (Future<ProductInfo> future : futureList) {
                System.out.println("CacheController的结果: " + future.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "success";
    }
}
