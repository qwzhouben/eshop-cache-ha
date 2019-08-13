package com.zben.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.zben.eshop.cache.ha.cache.local.BrandCache;
import com.zben.eshop.cache.ha.http.HttpClientUtils;
import com.zben.eshop.cache.ha.model.ProductInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @DESC: 获取商品信息
 * @author: jhon.zhou
 * @date: 2019/8/9 0009 10:29
 */
public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public GetProductInfoCommand(Long productId) {
        super(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"));
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        //模拟出错
        if (productId == -1L) {
            throw new Exception();
        }
        if (productId == -2L) {
            throw new Exception();
        }
        String response = HttpClientUtils.sendGetRequest("http://127.0.0.1:8082/getProductInfo/" + productId);
        return JSONObject.parseObject(response, ProductInfo.class);
    }

    /**
     * 采用嵌套多级降级
     */
    private static class FirstLevelFallbackCommand extends HystrixCommand<ProductInfo> {

        private Long productId;

        public FirstLevelFallbackCommand(Long productId) {
            /**
             * 第一级的降级策略，因为这个command是运行在fallback中的
             * 所以至关重要的一点是，在做多级降级的时候，要将降级command的线程池单独做一个出来
             * 如果主流程的command都失败了，可能线程池都已经沾满了
             * 降级command必须用自己的独立线程池
             */
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("FirstLevelFallbackCommand"))
                    .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("FirstLevelFallbackPool"))
            );
            this.productId = productId;
        }

        @Override
        protected ProductInfo run() throws Exception {
            // 这里，因为是第一级降级的策略，所以说呢，其实是要从备用机房的机器去调用接口
            // 但是，我们这里没有所谓的备用机房，所以说还是调用同一个服务来模拟
            if (productId == -2L) {
                throw new Exception();
            }
            String response = HttpClientUtils.sendGetRequest("http://127.0.0.1:8082/getProductInfo/" + productId);
            return JSONObject.parseObject(response, ProductInfo.class);
        }

        @Override
        protected ProductInfo getFallback() {
            // 第二级降级策略，第一级降级策略，都失败了
            ProductInfo productInfo = new ProductInfo();
            // 从请求参数中获取到的唯一条数据
            productInfo.setId(productId);
            // 从本地缓存中获取一些数据
            productInfo.setBrandId(BrandCache.getBrandId(productId));
            productInfo.setBrandName(BrandCache.getBrandName(productInfo.getBrandId()));
            // 手动填充一些默认的数据
            productInfo.setColor("默认颜色");
            productInfo.setModifyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            productInfo.setName("默认商品");
            productInfo.setPictureList("default.jpg");
            productInfo.setPrice(0.0);
            productInfo.setService("默认售后服务");
            productInfo.setShopId(-1L);
            productInfo.setSize("默认大小");
            productInfo.setSpecification("默认规格");
            return productInfo;
        }
    }

    @Override
    protected ProductInfo getFallback() {
        return new FirstLevelFallbackCommand(productId).execute();
    }

/*    @Override
    protected String getCacheKey() {
        return "product_info_" + productId;
    }*/
}
