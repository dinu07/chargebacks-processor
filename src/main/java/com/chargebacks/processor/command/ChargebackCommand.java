package com.chargebacks.processor.command;

import com.chargebacks.processor.job.ChargebackJobLauncher;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@Component
@CommandLine.Command(
    name = "chargeback-processor",
    description = "ETL processor to extract chargeback records from MySQL database and export to CSV",
    mixinStandardHelpOptions = true
)
public class ChargebackCommand implements Callable<Integer> {

    @Autowired
    private ChargebackJobLauncher jobLauncher;

    @CommandLine.Option(
        names = {"--startTimestamp"},
        description = "Beginning of the date range (format: yyyy-MM-ddTHH:mm:ss). Defaults to start of today (00:00:00).",
        defaultValue = ""
    )
    private String startTimestamp;

    @CommandLine.Option(
        names = {"--endTimestamp"},
        description = "End of the date range (format: yyyy-MM-ddTHH:mm:ss). Defaults to end of today (23:59:59).",
        defaultValue = ""
    )
    private String endTimestamp;

    @Override
    public Integer call() throws Exception {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime start;
            LocalDateTime end;

            // Use defaults if not provided
            if (startTimestamp == null || startTimestamp.isEmpty()) {
                start = LocalDate.now().atStartOfDay();
            } else {
                start = LocalDateTime.parse(startTimestamp, formatter);
            }

            if (endTimestamp == null || endTimestamp.isEmpty()) {
                end = LocalDate.now().atTime(23, 59, 59);
            } else {
                end = LocalDateTime.parse(endTimestamp, formatter);
            }

            System.out.println("Starting chargeback export job...");
            System.out.println("Start timestamp: " + start);
            System.out.println("End timestamp: " + end);

            String outputFile = jobLauncher.launchJob(start, end);
            
            System.out.println("Job completed successfully!");
            System.out.println("Output file: " + outputFile);

            try {
                // see file contents
                Files.readAllLines(Paths.get(outputFile)).forEach(System.out::println);
            } catch(Exception e) {
                // temp fix
                e.printStackTrace();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error executing job: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}

