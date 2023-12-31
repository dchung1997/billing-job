package com.example.billingjob;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBatchTest
@SpringBootTest
class BillingJobApplicationTests {
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void setUp() {
		this.jobRepositoryTestUtils.removeJobExecutions();
		JdbcTestUtils.deleteFromTables(this.jdbcTemplate, "BILLING_DATA");
	}	


	@Test 
	void testJobExecution() throws Exception {
		// Given
		JobParameters jobParameters = new JobParametersBuilder()
										.addString("input.file", "input/billing-2023-01.csv")
										.addString("output.file", "staging/billing-report-2023-01.csv")
										.addJobParameter("data.year", 2023, Integer.class)
										.addJobParameter("data.month", 1, Integer.class)										
										.toJobParameters();

		// When
		JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(jobParameters);
		

		// Then
		Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		Assertions.assertTrue(Files.exists(Paths.get("staging", "billing-2023-01.csv")));
		Assertions.assertEquals(1000, JdbcTestUtils.countRowsInTable(jdbcTemplate, "BILLING_DATA"));
		
		Path billingReport = Paths.get("staging", "billing-report-2023-01.csv");
		Assertions.assertTrue(Files.exists(billingReport));
		Assertions.assertEquals(781, Files.lines(billingReport).count());
	}

	@Test 
	void testJobExecutionSkippedTests() throws Exception {
		// Given
		JobParameters jobParameters = new JobParametersBuilder()
										.addString("input.file", "input/billing-2023-03.csv")
										.addString("output.file", "staging/billing-report-2023-03.csv")
										.addString("skip.file", "staging/billing-data-skip-2023-03.psv")
										.addJobParameter("data.year", 2023, Integer.class)
										.addJobParameter("data.month", 3, Integer.class)										
										.toJobParameters();

		// When
		JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(jobParameters);

		// Then
		Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		Assertions.assertTrue(Files.exists(Paths.get("staging", "billing-2023-03.csv")));
		Assertions.assertEquals(498, JdbcTestUtils.countRowsInTable(jdbcTemplate, "BILLING_DATA"));

		Path billingSkippedData = Paths.get("staging", "billing-data-skip-2023-03.psv");
		Assertions.assertTrue(Files.exists(billingSkippedData));
		Assertions.assertEquals(2, Files.lines(billingSkippedData).count());		
		
		Path billingReport = Paths.get("staging", "billing-report-2023-03.csv");
		Assertions.assertTrue(Files.exists(billingReport));
		Assertions.assertEquals(387, Files.lines(billingReport).count());
	}
	
	@Test 
	void testEmptyInputFile() throws Exception {
		// Given
		JobParameters jobParameters = new JobParametersBuilder()
										.addString("output.file", "staging/billing-report-2023-01.csv")
										.addJobParameter("data.year", 2023, Integer.class)
										.addJobParameter("data.month", 1, Integer.class)											
										.toJobParameters();

		// When		
		Throwable exception = Assertions.assertThrows(JobParametersInvalidException.class, () -> this.jobLauncherTestUtils.launchJob(jobParameters));
		
		// Then
		Assertions.assertEquals("Input File is missing or is incorrectly formatted. Requires string.", exception.getMessage());

	}

	@Test
	void testMalformatedInputFile() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
										.addJobParameter("input.file", 1, Integer.class)
										.addString("output.file", "staging/billing-report-2023-01.csv")
										.addJobParameter("data.year", 2023, Integer.class)
										.addJobParameter("data.month", 1, Integer.class)										
										.toJobParameters();			

		// When		
		Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> this.jobLauncherTestUtils.launchJob(jobParameters));
		
		// Then
		Assertions.assertEquals("Key input.file is not of type String", exception.getMessage());										
	}


	@Test 
	void testEmptyOutputFile() throws Exception {
		// Given
		JobParameters jobParameters = new JobParametersBuilder()
										.addString("output.file", "")
										.addString("input.file", "input/billing-2023-01.csv")
										.addJobParameter("data.year", 2023, Integer.class)
										.addJobParameter("data.month", 1, Integer.class)											
										.toJobParameters();

		// When		
		Throwable exception = Assertions.assertThrows(JobParametersInvalidException.class, () -> this.jobLauncherTestUtils.launchJob(jobParameters));
		
		// Then
		Assertions.assertEquals("Output File is missing or is incorrectly formatted. Requires string.", exception.getMessage());

	}
	

	@Test 
	void testEmptyDataYear() throws Exception {
		// Given
		JobParameters jobParameters = new JobParametersBuilder()
										.addString("input.file", "input/billing-2023-01.csv")
										.addString("output.file", "staging/billing-report-2023-01.csv")
										.addJobParameter("data.month", 1, Integer.class)											
										.toJobParameters();

		// When		
		Throwable exception = Assertions.assertThrows(JobParametersInvalidException.class, () -> this.jobLauncherTestUtils.launchJob(jobParameters));
		
		// Then
		Assertions.assertEquals("data.year is missing.", exception.getMessage());

	}		

	@Test 
	void testEmptyDataMonth() throws Exception {
		// Given
		JobParameters jobParameters = new JobParametersBuilder()
										.addString("input.file", "input/billing-2023-01.csv")
										.addString("output.file", "staging/billing-report-2023-01.csv")
										.addJobParameter("data.year", 12, Integer.class)	
										.toJobParameters();

		// When		
		Throwable exception = Assertions.assertThrows(JobParametersInvalidException.class, () -> this.jobLauncherTestUtils.launchJob(jobParameters));
		
		// Then
		Assertions.assertEquals("data.month is missing.", exception.getMessage());

	}		

	@Test
	void contextLoads() {
	}

}
