package com.example.billingjob;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class BillingJobParametersValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String fileName = parameters.getString("input.file");

        if (!StringUtils.hasText(fileName)) {
            throw new JobParametersInvalidException("Input File is missing.");
        } 
    }
    
}
