package com.zben.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.zben.eshop.cache.ha.http.HttpClientUtils;
import com.zben.eshop.cache.ha.model.ProductInfo;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * @DESC: 批量查询多个商品数据的command
 * @author: jhon.zhou
 * @date: 2019/8/9 0009 10:36
 */
public class GetProductInfosCommand extends HystrixObservableCommand<ProductInfo> {

    private List<Long> productIds;

    public GetProductInfosCommand(List<Long> productIds) {
        super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        this.productIds = productIds;
    }


    @Override
    protected Observable<ProductInfo> construct() {
        return Observable.create(new Observable.OnSubscribe<ProductInfo>() {
            @Override
            public void call(Subscriber<? super ProductInfo> subscriber) {
                try {

                    for (Long productId : productIds) {
                        String response = HttpClientUtils.sendGetRequest("http://127.0.0.1:8082/getProductInfo/" + productId);
                        ProductInfo productInfo = JSONObject.parseObject(response, ProductInfo.class);
                        subscriber.onNext(productInfo);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
