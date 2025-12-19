package com.chargebacks.processor.config;

import com.chargebacks.processor.model.Chargeback;
import com.chargebacks.processor.processor.ChargebackItemProcessor;
import com.chargebacks.processor.reader.ChargebackItemReader;
import com.chargebacks.processor.writer.ChargebackItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ChargebackItemReader chargebackItemReader;

    @Autowired
    private ChargebackItemProcessor chargebackItemProcessor;

    @Autowired
    private ChargebackItemWriter chargebackItemWriter;

    @Bean
    public Job chargebackExportJob() {
        return new JobBuilder("chargebackExportJob", jobRepository)
                .start(chargebackExportStep())
                .build();
    }

    @Bean
    public Step chargebackExportStep() {
        return new StepBuilder("chargebackExportStep", jobRepository)
                .<Chargeback, Chargeback>chunk(100, transactionManager)
                .reader(chargebackItemReader)
                .processor(chargebackItemProcessor)
                .writer(chargebackItemWriter)
                .build();
    }
}

