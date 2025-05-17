package com.ysu.wyh;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.functions.FunctionConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@SpringBootTest
class WyhApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testAdmin() throws PulsarClientException, PulsarAdminException {
        // 创建 Admin 客户端（替换为实际 Broker 地址）
        String adminUrl = "http://82.157.51.27:8080"; // 注意端口是 8080
        PulsarAdmin admin = PulsarAdmin.builder()
                .serviceHttpUrl(adminUrl)
                .build();

        // 定义函数配置（参数与命令行一致）
        FunctionConfig config = new FunctionConfig();
        config.setName("JuLei-function");
        config.setClassName("com.ysu.wyh.ClusteringFunction");
        config.setInputs(Collections.singletonList("persistent://public/default/input-topic"));
        config.setOutput("persistent://public/default/output-topic");
        config.setRuntime(FunctionConfig.Runtime.JAVA);
        config.setJar("/path/to/function.jar"); // 指定函数 JAR 路径

        // 部署函数
        admin.functions().createFunction(config, null);

        // 关闭客户端
        admin.close();

    }

}
