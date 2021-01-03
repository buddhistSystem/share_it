package ribbonconfiguration;

import com.demo.contnetcenter.config.NacosWeightedRuleConfig;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ribbon 负载均衡配置必须放在启动类上级
 * 如果和启动类放在同一个包中
 * 就会产生spring和ribbon上下文重叠
 * 所有Ribbon客户端就会共用该配置，而无法实现细粒度控制
 */
@Configuration
public class RibbonConfiguration {

    /**
     * 配置Ribbon负载均衡策略
     *
     * @return
     */
    @Bean
    public IRule ribbonRule() {
        //return new RandomRule();
        //采用自定义支持Nacos权重配置的负载均衡算法
        return new NacosWeightedRuleConfig();
    }


}
