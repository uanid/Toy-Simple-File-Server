package com.uanid.toy.simplefileserver.configuration;

import com.uanid.toy.simplefileserver.driver.AbstractFileDriver;
import com.uanid.toy.simplefileserver.driver.DiskFileSystemDriver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author uanid
 * @since 2020-02-03
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileDriverConfig {

    @Value("${rootPath}")
    private String rootPath;

    @Bean
    @Primary
    public AbstractFileDriver defaultFileDriver() {
        return diskFileSystemDriver();
    }

    @Bean
    public DiskFileSystemDriver diskFileSystemDriver() {
        log.warn("DiskFileSystemDriver initialized --rootPath={}", rootPath);
        return new DiskFileSystemDriver(rootPath);
    }
}
