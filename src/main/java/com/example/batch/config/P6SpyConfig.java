package com.example.batch.config;

import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.util.Locale;

@Configuration
public class P6SpyConfig implements MessageFormattingStrategy {

    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
    }

    private String formatSql(String sql) {
        String trimmedSql = sql.trim().toLowerCase(Locale.ROOT);
        if (trimmedSql.startsWith("create") || trimmedSql.startsWith("alter") || trimmedSql.startsWith("comment")) {
            return FormatStyle.DDL.getFormatter().format(sql);
        } else {
            return FormatStyle.BASIC.getFormatter().format(sql);
        }
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                String prepared, String sql, String url) {
        String formattedSql = sql;
        if ("STATEMENT".equals(category) && sql != null && !sql.trim().isEmpty()) {
            formattedSql = formatSql(sql);
        }
        return String.format("[%s] | %d ms | \n %s", category, elapsed, formattedSql);
    }
}
