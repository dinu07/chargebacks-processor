package com.chargebacks.processor.job;

import com.chargebacks.processor.reader.ChargebackItemReader;
import com.chargebacks.processor.writer.ChargebackItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChargebackJobLauncher {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("chargebackExportJob")
    private Job chargebackExportJob;

    @Autowired
    private ChargebackItemReader chargebackItemReader;

    @Autowired
    private ChargebackItemWriter chargebackItemWriter;

    public String launchJob(LocalDateTime startTimestamp, LocalDateTime endTimestamp) throws Exception {
        // Configure reader with timestamp range
        chargebackItemReader.setTimestampRange(startTimestamp, endTimestamp);
        chargebackItemReader.afterPropertiesSet();

        // Configure writer with output filename
        chargebackItemWriter.setOutputFileName(startTimestamp, endTimestamp);
        chargebackItemWriter.configureWriter();
        chargebackItemWriter.afterPropertiesSet();

        // Create job parameters
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("startTimestamp", startTimestamp.toString())
                .addString("endTimestamp", endTimestamp.toString())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // Launch the job
        jobLauncher.run(chargebackExportJob, jobParameters);

        return chargebackItemWriter.getOutputFileName();
    }
}

