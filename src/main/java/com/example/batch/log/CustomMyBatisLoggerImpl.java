package com.example.batch.log;

import org.apache.ibatis.logging.stdout.StdOutImpl;

public class CustomMyBatisLoggerImpl extends StdOutImpl {
    public CustomMyBatisLoggerImpl(String clazz) {
        super(clazz);
    }

    @Override
    public void debug(String message) {
        if (message != null && (message.startsWith("==>") || message.startsWith("<=="))) {
            super.debug("🟡 [MyBatis SQL] " + message);
        }
    }
}
