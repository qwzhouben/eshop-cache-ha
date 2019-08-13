package com.zben.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.*;
import com.zben.eshop.cache.ha.cache.local.BrandCache;

/**
 * @DESC:
 * @author: jhon.zhou
 * @date: 2019/8/12 0012 17:41
 */
public class GetBrandNameCommand extends HystrixCommand<String> {

    private Long brandId;

    public GetBrandNameCommand(Long brandId){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("BrandInfoService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetBrandNameCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetBrandInfoPool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(15)
                        .withQueueSizeRejectionThreshold(10))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(15))
        );
        this.brandId = brandId;
    }

    @Override
    protected String run() throws Exception {
        //正常应该是调用服务
        throw new Exception();
    }

    @Override
    protected String getFallback() {
        System.out.println("从本地缓存获取过期的品牌数据，brandId=" + brandId);
        return BrandCache.getBrandName(brandId);
    }
}
