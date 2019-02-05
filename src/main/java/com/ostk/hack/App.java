package com.ostk.hack;

import java.util.Scanner;
import java.util.function.BiConsumer;

/**
 * Hello world!
 *
 */
public class App 
{
    // TODO: You'll need to instantiate an AmazonS3 instance using AmazonS3ClientBuilder and probably with region
    // us-east-1. You could add it as a static field here.

    public static void main( String[] args )
    {
        println("Enter 1 if you want to save a login, 2 if you want to look up a login, 3 if you want to delete a " +
                "login, or 4 if you want to delete a whole login group");

        Scanner in = new Scanner(System.in);

        int selection = in.nextInt();

        switch (selection) {
            case 1:
                saveLogin(in);
                break;
            case 2:
                lookupLogin(in);
                break;
            case 3:
                deleteLogin(in);
                break;
            case 4:
                deleteGroup(in);
            default:
                println("SOPHISTICATED HACK ATTEMPT DETECTED AND BLOCKED, GOODBYE.");
                break;
        }
    }

    private static void saveLogin(Scanner in) {
        getBucketNameAndObjectKey(in, (bucketName, objectKey) -> {
            String username = promptForStringInput(in, "Enter the username or email:");

            String password = promptForStringInput(in, "Enter the password:");

            String loginText = createLoginText(username, password);

            createS3BucketIfItDoesNotExist(bucketName);

            // TODO: Create the object in the bucket with the 'loginText' as its content
        });
    }

    private static void lookupLogin(Scanner in) {
        getBucketNameAndObjectKey(in, (bucketName, objectKey) -> {
            // TODO: get the object from the bucket and print it at the console
        });
    }

    private static void deleteLogin(Scanner in) {
        getBucketNameAndObjectKey(in, (bucketName, objectKey) -> {
            // TODO: delete the object from the bucket
            // TODO: (extra credit) If you enabled versioning, delete all versions
            // TODO: (extra credit) If you enabled cross-region replication, delete the bucket it is replicating to
        });
    }

    private static void deleteGroup(Scanner in) {
        getBucketNameAndObjectKey(in, (bucketName, objectKey) -> {
            // TODO: delete the bucket
        });
    }

    private static void createS3BucketIfItDoesNotExist(String bucketName) {
        // TODO: check if the bucket exists and create it if it does not
        // TODO: (EXTRA CREDIT) enable versioning on the bucket
        // TODO: (EXTRA CREDIT) tag the bucket with 'Creator'=<your name>, Project='Hack Sessions Week 2'
        // TODO: (EXTRA CREDIT) enable cross-region replication on the bucket
        throw new UnsupportedOperationException();
    }

    private static void getBucketNameAndObjectKey(Scanner in, BiConsumer<String, String> bucketAndKeyFn) {
        String groupName = promptForGroupName(in);

        String loginName = promptForLoginName(in);

        String bucketName = bucketNameForGroup(groupName);

        String objectKey = objectKeyForLogin(loginName);

        bucketAndKeyFn.accept(bucketName, objectKey);
    }

    private static String promptForLoginName(Scanner in) {
        return promptForStringInput(in,"Enter the login name:");
    }

    private static String promptForGroupName(Scanner in) {
        return promptForStringInput(in,"Enter the name of the password group:");
    }

    private static String promptForStringInput(Scanner in, String s) {
        println(s);
        return in.nextLine();
    }

    private static String bucketNameForGroup(String groupName) {
        // Amazon S3 defines a bucket name as a series of one or more labels, separated by periods, that adhere to the following rules:
        // * The bucket name can be between 3 and 63 characters long, and can contain only lower-case characters, numbers, periods, and dashes.
        // * Each label in the bucket name must start with a lowercase letter or number.
        // * The bucket name cannot contain underscores, end with a dash, have consecutive periods, or use dashes adjacent to periods.
        // * The bucket name cannot be formatted as an IP address (198.51.100.24).
        // TODO: (EXTRA CREDIT) many groupNames will map to the same bucket name, which could cause some unexpected
        // behavior. Implement a more sophisticated mapping here.
        return groupName
                .toLowerCase()
                .replaceAll("[^a-z0-9\\-]", "-");
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
    }
}
