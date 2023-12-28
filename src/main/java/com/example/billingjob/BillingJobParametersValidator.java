package com.example.billingjob;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class BillingJobParametersValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String inputFile = parameters.getString("input.file");
        String outputFile = parameters.getString("output.file");        

        JobParameter<?> yearParameter = parameters.getParameter("data.year");
        JobParameter<?> monthParameter = parameters.getParameter("data.month");  

        if (!StringUtils.hasText(inputFile)) {
            throw new JobParametersInvalidException("Input File is missing or is incorrectly formatted. Requires string.");
        } 

        if (!StringUtils.hasText(outputFile)) {
            throw new JobParametersInvalidException("Output File is missing or is incorrectly formatted. Requires string.");
        }

        if (yearParameter == null) {
            throw new JobParametersInvalidException("data.year is missing.");
        }

        if (monthParameter == null) {
            throw new JobParametersInvalidException("data.month is missing.");
        }        
    }
    
}
