package com.ostk.hack;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;

import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Hello world!
 *
 */
public class App 
{
    private static Scanner scanner = new Scanner(System.in);

    private static AmazonS3 s3;

    // TODO: REPLACE my-name WITH YOUR NAME IN BUCKET_PREFIX, e.g. ostk-hack-sessions-larry-christensen-week-1
    private static final String BUCKET_PREFIX = "ostk-hack-sessions-my-name-week-1-";

    public static void main( String[] args )
    {
        // TODO: REPLACE hack-sessions-my-name WITH THE ACTUAL AWS PROFILE YOU ARE USING (PROBABLY default)
        AWSCredentials credentials = new ProfileCredentialsProvider("hack-sessions-my-name").getCredentials();

        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("us-east-1")
                .build();

        while (true) {
            println("What do you want to do? Enter a number for one of the options below:\n" +
                    "1. Save a login.\n" +
                    "2. List login groups.\n" +
                    "3. List logins in a group.\n" +
                    "4. Lookup login.\n" +
                    "5. Delete a login from a group.\n" +
                    "6. Delete a login group.\n");

            String selection = readLine();

            switch (selection) {
                case "1":
                    saveLogin();
                    break;
                case "2":
                    listLoginGroups();
                    break;
                case "3":
                    listLogins();
                    break;
                case "4":
                    lookupLogin();
                    break;
                case "5":
                    deleteLogin();
                    break;
                case "6":
                    deleteGroup();
                    break;
                default:
                    println("SOPHISTICATED HACK ATTEMPT DETECTED AND BLOCKED, GOODBYE.");
                    break;
            }
        }
    }

    private static void listLoginGroups() {
        println("LISTING LOGIN GROUPS BY LISTING BUCKETS WITH BUCKET_PREFIX, REMOVING THE PREFIX FROM EACH, AND " +
                "PRINTING THE RESULTING VALUES");
        // TODO: Implement Me!
    }

    private static void listLogins() {
        getBucketName(bucketName -> {
            println("LISTING OBJECTS WITHIN THE BUCKET " + bucketName);
            // TODO: Implement Me!
        });
    }

    private static void saveLogin() {
        getBucketNameAndObjectKey((bucketName, objectKey) -> {
            String username = promptForStringInput("Enter the username or email:");

            String password = promptForStringInput("Enter the password:");

            String loginText = createLoginText(username, password);

            createS3BucketIfItDoesNotExist(bucketName);

            println(String.format(
                    "PUTTING OBJECT WITH KEY %s TO BUCKET %s WITH CONTENT: \n%s",
                    objectKey,
                    bucketName,
                    loginText));

            // TODO: Implement Me!
        });
    }

    private static void lookupLogin() {
        getBucketNameAndObjectKey((bucketName, objectKey) -> {
            println(String.format(
                    "GETTING OBJECT CONTENTS FOR OBJECT WITH KEY %s FROM BUCKET %s",
                    bucketName,
                    objectKey));

            // TODO: Implement Me!
        });
    }

    private static void deleteLogin() {
        getBucketNameAndObjectKey((bucketName, objectKey) -> {
            println(String.format(
                    "DELETING OBJECT WITH KEY %s FROM BUCKET %s",
                    bucketName,
                    objectKey));

            // TODO: (extra credit) If you enabled versioning, delete all versions
            // TODO: (extra credit) If you enabled cross-region replication, delete the bucket it is replicating to

            // TODO: Implement Me!
        });
    }

    private static void deleteGroup() {
        getBucketName(bucketName -> {
            println(String.format(
                    "GETTING THE LIST OF ALL OBJECTS FROM BUCKET %s TO DELETE THEM",
                    bucketName));

            ObjectListing objectListing = s3.listObjects(bucketName);
            objectListing.getObjectSummaries().stream().forEach(
                    objectSummary -> s3.deleteObject(bucketName, objectSummary.getKey()));

            println(String.format(
                    "DELETING BUCKET %s",
                    bucketName));
            // TODO: Implement Me!
        });
    }

    private static void createS3BucketIfItDoesNotExist(String bucketName) {
        // TODO: (EXTRA CREDIT) enable versioning on the bucket
        // TODO: (EXTRA CREDIT) tag the bucket with 'Creator'=<your name>, Project='Hack Sessions Week 2'
        // TODO: (EXTRA CREDIT) enable cross-region replication on the bucket
        if (!doesBucketExist(bucketName)) {
            println(String.format("BUCKET %s DID NOT EXIST, CREATING IT", bucketName));
            // TODO: Implement Me!
        }
    }

    private static boolean doesBucketExist(String bucketName) {
        println(String.format("CHECKING IF BUCKET %s EXISTS", bucketName));
        // TODO: Implement Me!
        return false;
    }

    private static void getBucketNameAndObjectKey(BiConsumer<String, String> bucketAndKeyFn) {
        getBucketName(bucketName -> {
            String loginName = promptForLoginName();

            String objectKey = objectKeyForLogin(loginName);

            bucketAndKeyFn.accept(bucketName, objectKey);
        });
    }

    private static void getBucketName(Consumer<String> bucketFn) {
        String groupName = promptForGroupName();

        String bucketName = bucketNameForGroup(groupName);
        println(String.format("CONVERTING INPUT GROUP NAME %s TO VALID BUCKET NAME %s", groupName, bucketName));

        bucketFn.accept(bucketName);
    }

    private static String promptForLoginName() {
        return promptForStringInput("Enter the login name:");
    }

    private static String promptForGroupName() {
        return promptForStringInput("Enter the name of the password group:");
    }

    private static String promptForStringInput(String s) {
        println(s);
        return readLine();
    }

    private static String bucketNameForGroup(String groupName) {
        // Amazon S3 defines a bucket name as a series of one or more labels, separated by periods, that adhere to the following rules:
        // * The bucket name can be between 3 and 63 characters long, and can contain only lower-case characters, numbers, periods, and dashes.
        // * Each label in the bucket name must start with a lowercase letter or number.
        // * The bucket name cannot contain underscores, end with a dash, have consecutive periods, or use dashes adjacent to periods.
        // * The bucket name cannot be formatted as an IP address (198.51.100.24).
        // TODO: (EXTRA CREDIT) perform a more sophisticated conversion
        String validGroupName = groupName
                .toLowerCase()
                .replaceAll("[^a-z0-9\\-]", "-");
        println(String.format("CONVERTING INPUT GROUP NAME %s TO VALID GROUP NAME %s", groupName, validGroupName));
        return BUCKET_PREFIX + validGroupName;
    }

    private static String createLoginText(String username, String password) {
        return String.format("Username: %s\nPassword: %s", username, password);
    }

    private static String objectKeyForLogin(String loginName) {
        // TODO: (EXTRA CREDIT) S3 will accept any UTF-8 characters for an object key, but certain characters can cause
        // issues with some applications, so we should add more sophisticated validation and/or transformation.
        // See https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html for more info.
        return loginName;
    }

    private static void println(Object arg) {
        System.out.println(arg);
        System.out.println();
    }

    private static String readLine() {
        return scanner.nextLine();
    }
}
