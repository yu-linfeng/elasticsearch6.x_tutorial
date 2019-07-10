package com.coderbuff.logstash;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author cdyulinfeng
 * @date 2019/7/10
 **/
@Slf4j
@Component
public class LogstashDemo implements InitializingBean {

    //private Logger log = LoggerFactory.getLogger(LogstashDemo.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }


    private void init() throws InterruptedException {
        int i = 0;
        while (true) {
            log.debug(i + " : DEBUG级别日志");
            log.info(i + " : INFO级别日志");
            log.warn(i + " : WARN级别日志");
            log.error(i + " : ERROR级别日志");
            i++;
            Thread.sleep(2000);
        }
    }
}
