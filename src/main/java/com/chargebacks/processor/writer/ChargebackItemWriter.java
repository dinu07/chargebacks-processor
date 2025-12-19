package com.chargebacks.processor.writer;

import com.chargebacks.processor.model.Chargeback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ChargebackItemWriter extends FlatFileItemWriter<Chargeback> {

    @Value("${chargeback.output.directory:./output}")
    private String outputDirectory;

    private String outputFileName;

    public ChargebackItemWriter() {
        // Initialize with a default line aggregator to pass Spring validation
        // This will be reconfigured before the job runs
        DelimitedLineAggregator<Chargeback> defaultAggregator = new DelimitedLineAggregator<>();
        defaultAggregator.setDelimiter(",");
        defaultAggregator.setFieldExtractor(item -> new Object[]{"", "", "", "", "", ""});
        setLineAggregator(defaultAggregator);
    }

    public void setOutputFileName(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String startStr = startTimestamp.format(formatter);
        String endStr = endTimestamp.format(formatter);
        this.outputFileName = String.format("%s/chargebacks_%s_to_%s.csv", 
            outputDirectory, startStr, endStr);
        
        // Ensure output directory exists
        java.io.File dir = new java.io.File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        setResource(new FileSystemResource(outputFileName));
    }

    public void configureWriter() {
        setHeaderCallback(writer -> writer.write("disputed_dt,disputed_amt,disputed_curr,merchandise_ref,reason_for_dispute,created_time"));
        
        DelimitedLineAggregator<Chargeback> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        
        FieldExtractor<Chargeback> fieldExtractor = chargeback -> {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            
            return new Object[]{
                chargeback.getDisputedDt() != null ? chargeback.getDisputedDt().format(dateFormatter) : "",
                chargeback.getDisputedAmt() != null ? chargeback.getDisputedAmt().toString() : "",
                chargeback.getDisputedCurr() != null ? chargeback.getDisputedCurr() : "",
                chargeback.getMerchandiseRef() != null ? chargeback.getMerchandiseRef() : "",
                chargeback.getReasonForDispute() != null ? chargeback.getReasonForDispute() : "",
                chargeback.getCreatedTime() != null ? chargeback.getCreatedTime().format(dateTimeFormatter) : ""
            };
        };
        
        lineAggregator.setFieldExtractor(fieldExtractor);
        setLineAggregator(lineAggregator);
    }

    public String getOutputFileName() {
        return outputFileName;
    }
}

