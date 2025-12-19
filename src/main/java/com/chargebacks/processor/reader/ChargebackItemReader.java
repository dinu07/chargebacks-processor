package com.chargebacks.processor.reader;

import com.chargebacks.processor.model.Chargeback;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ChargebackItemReader extends JdbcCursorItemReader<Chargeback> {

    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;

    public ChargebackItemReader(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("SELECT disputed_dt, disputed_amt, disputed_curr, merchandise_ref, " +
               "reason_for_dispute, created_time " +
               "FROM Chargebacks " +
               "WHERE created_time >= ? AND created_time <= ? " +
               "ORDER BY created_time");
        setRowMapper(new ChargebackRowMapper());
    }

    public void setTimestampRange(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Don't validate timestamps here - they'll be set before the job runs
        // Validation will happen in open() method
        if (startTimestamp != null && endTimestamp != null) {
            setPreparedStatementSetter(ps -> {
                ps.setObject(1, startTimestamp);
                ps.setObject(2, endTimestamp);
            });
        }
        super.afterPropertiesSet();
    }

    @Override
    public void open(org.springframework.batch.item.ExecutionContext executionContext) {
        // Validate timestamps when reader is actually opened
        if (startTimestamp == null || endTimestamp == null) {
            throw new IllegalStateException("Start and end timestamps must be set before opening the reader");
        }
        // Set prepared statement setter (will be called each time reader is opened)
        setPreparedStatementSetter(ps -> {
            ps.setObject(1, startTimestamp);
            ps.setObject(2, endTimestamp);
        });
        super.open(executionContext);
    }

    private static class ChargebackRowMapper implements RowMapper<Chargeback> {
        @Override
        public Chargeback mapRow(ResultSet rs, int rowNum) throws SQLException {
            Chargeback chargeback = new Chargeback();
            chargeback.setDisputedDt(rs.getObject("disputed_dt", LocalDate.class));
            chargeback.setDisputedAmt(rs.getBigDecimal("disputed_amt"));
            chargeback.setDisputedCurr(rs.getString("disputed_curr"));
            chargeback.setMerchandiseRef(rs.getString("merchandise_ref"));
            chargeback.setReasonForDispute(rs.getString("reason_for_dispute"));
            chargeback.setCreatedTime(rs.getObject("created_time", LocalDateTime.class));
            return chargeback;
        }
    }
}

