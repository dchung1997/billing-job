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
            throw new JobParametersInvalidException("Input File is missing.");
        } 

        if (!StringUtils.hasText(outputFile)) {
            throw new JobParametersInvalidException("Output File is missing.");
        }

        if (yearParameter == null || !(yearParameter.getValue() instanceof Integer)) {
            throw new JobParametersInvalidException("Year is missing.");
        }

        if (monthParameter == null || !(monthParameter.getValue() instanceof Integer)) {
            throw new JobParametersInvalidException("Month is missing.");
        }        
    }
    
}
