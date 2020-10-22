package com.saniagonsalves.storage.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class AWSService {

    public static String cloudFrontUrl;
    private static String primaryBucket;
    private static String secondaryBucket;
    private static Regions currentRegion;
    private static AmazonS3 s3;
    private static String awsAccessKeyId;
    private static String awsSecret;

    @Value("${cloudfront.url}")
    public void setCloudFrontUrl(String cloudFrontUrl) {
        AWSService.cloudFrontUrl = cloudFrontUrl;
        log.info("Set : " + AWSService.cloudFrontUrl);
    }

    @Value("${primary.bucket}")
    public void setPrimaryBucket(String primaryBucket) {
        AWSService.primaryBucket = primaryBucket;
        log.info("Set : " + AWSService.primaryBucket);
    }

    @Value("${secondary.bucket}")
    public void setSecondaryBucket(String secondaryBucket) {
        AWSService.secondaryBucket = secondaryBucket;
        log.info("Set : " + AWSService.secondaryBucket);
    }

    @Value("${aws.accessKeyId}")
    public void setAwsAccessKeyId(String awsAccessKeyId) {
        AWSService.awsAccessKeyId = awsAccessKeyId;
        log.info("Set AWS Key");
    }

    @Value("${aws.secretKey}")
    public void setAwsSecret(String awsSecret) {
        AWSService.awsSecret = awsSecret;
        log.info("Set AWS Secret");
    }

    private static Regions getRegionToUse() {

        if (currentRegion == null) {
            Region runningRegion = null;

            try {
                runningRegion = Regions.getCurrentRegion();
                log.info("Running region : " + runningRegion);
            } catch (SdkClientException e) {
                log.error("Cannot determine ec2 region", e);
            }

            if (runningRegion == null) {
                currentRegion = Regions.US_WEST_1;
            } else {
                currentRegion = Regions.fromName(runningRegion.getName());
            }
        }

        return currentRegion;
    }

    private static String getBucketToUse() {
        Regions region = getRegionToUse();

        if (region == null || region == Regions.US_WEST_1) {
            return primaryBucket;
        } else if (region == Regions.US_EAST_1) {
            return secondaryBucket;
        } else {
            return primaryBucket;
        }
    }

    private static AmazonS3 init() {

        if (s3 != null) {
            return s3;
        }

        Regions region = getRegionToUse();

        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecret);

        s3 = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .enableAccelerateMode()
            .build();

        log.info("S3 client" + s3.toString());

        return s3;
    }

    protected static boolean uploadFile(String storageId, String keyName, MultipartFile file) {

        Path tempfile;

        try {
            tempfile = Files.createTempFile(storageId, storageId);
        } catch (IOException e) {
            log.error("Cannot create temp file", e);
            return false;
        }

        try (BufferedOutputStream bs = new BufferedOutputStream(Files.newOutputStream(tempfile))) {
            log.info("Saving file " + file.getOriginalFilename() + " to " + tempfile.toAbsolutePath().toString());
            bs.write(file.getBytes());
        } catch (IOException e) {
            log.error("Cannot write to temp file", e);
            return false;
        }

        try {

            AmazonS3 s3 = init();

            PutObjectResult re = s3.putObject(getBucketToUse(), keyName, tempfile.toFile());

            log.info("Put Object Successful " + re.toString());
        } catch (AmazonServiceException e) {
            log.error("Cannot upload due to AmazonServiceException", e);
            return false;
        } catch (SdkClientException e) {
            log.error("Cannot upload due to SdkClientException", e);
            return false;
        }

        return true;
    }
}
