package com.demo.contentcenter;


import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class UserCenterApplicationTests {

    @Test
    public void test() {

    }

    @Resource
    private DiscoveryClient discoveryClient;

    /**
     * 测试发现服务
     */
    @Test
    public void test1() {
        //查询所有user-center的服务信息
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        System.out.println(JSON.toJSON(instances));
        List<String> services = discoveryClient.getServices();
        System.out.println(services);
    }


}
