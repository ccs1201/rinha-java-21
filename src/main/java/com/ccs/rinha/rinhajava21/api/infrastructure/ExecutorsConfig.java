package com.ccs.rinha.rinhajava21.api.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.*;
@Configuration
public class ExecutorsConfig {

    @Bean("virtual")
    @Lazy
    public Executor virtual() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean("forkjoin")
    @Lazy
    public Executor forkjoin() {
        return ForkJoinPool.commonPool();
    }

    @Bean("custom")
    @Lazy
    public Executor custom() {
        return new ThreadPoolExecutor(200, 300, 10,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(20000, true));
    }
}
