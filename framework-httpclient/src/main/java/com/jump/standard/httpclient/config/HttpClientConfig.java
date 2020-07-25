package com.jump.standard.httpclient.config;

import com.jump.standard.httpclient.bo.HttpClientProperties;

import com.jump.standard.httpclient.service.HttpClientService;
import com.jump.standard.httpclient.service.impl.HttpClientServiceImpl;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈HttpClient配置〉
 *
 * @author LiLin
 * @date 2020/2/28 0028
 */
@Configuration
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(prefix = "httpclient", name = "enabled", havingValue = "true", matchIfMissing = false)
public class HttpClientConfig {
    private HttpClientProperties httpClientProperties;

    @Autowired
    public HttpClientConfig(HttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
    }

    /**
     * 首先实例化一个连接池管理器，设置最大连接数、并发连接数
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 最大连接数
        httpClientConnectionManager.setMaxTotal(httpClientProperties.getMaxTotal());
        // 并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(httpClientProperties.getDefaultMaxPerRoute());
        // 可用空闲连接过期时间,重用空闲连接时会先检查是否空闲时间超过这个时间，如果超过，释放socket重新建立
        httpClientConnectionManager.setValidateAfterInactivity(httpClientProperties.getValidateAfterInactivity());
        return httpClientConnectionManager;
    }

    /**
     * 实例化连接池，设置连接池管理器。 这里需要以参数形式注入上面实例化的连接池管理器
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(
            @Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {

        // HttpClientBuilder中的构造方法被protected修饰，所以这里不能直接使用new来实例化一个HttpClientBuilder，可以使用HttpClientBuilder提供的静态方法create()来获取HttpClientBuilder对象
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        httpClientBuilder.setConnectionManager(httpClientConnectionManager);

        return httpClientBuilder;
    }

    /**
     * Builder是RequestConfig的一个内部类 通过RequestConfig的custom方法来获取到一个Builder对象 设置builder的连接信息
     * 这里还可以设置proxy，cookieSpec等属性。有需要的话可以在此设置
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(httpClientProperties.getConnectTimeout()).setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout())
                .setSocketTimeout(httpClientProperties.getSocketTimeout());
    }

    /**
     * 使用builder构建一个RequestConfig对象
     */
    @Bean(name = "requestConfig")
    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
        return builder.build();
    }


    /**
     * 注入连接池，用于获取httpClient
     */
    @Bean
    public CloseableHttpClient
    getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder, @Qualifier("requestConfig") RequestConfig requestConfig) {
        return httpClientBuilder.setDefaultRequestConfig(requestConfig).build();
    }

    @Bean
    @ConditionalOnMissingBean({HttpClientService.class})
    public HttpClientService httpClientService (){
        HttpClientServiceImpl httpClientService = new HttpClientServiceImpl();
        return httpClientService;
    }
}
