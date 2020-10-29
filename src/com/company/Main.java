package com.company;

import com.utils.Flags;
import com.utils.ResponseFormat;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

//    public static String encryptPass(String input) throws NoSuchAlgorithmException {
//        return toHexString(getSHA(input));
//    }
//
//    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
//        // Static getInstance method is called with hashing SHA
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//
//        // digest() method called
//        // to calculate message digest of an input
//        // and return array of byte
//        return md.digest(input.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public static String toHexString(byte[] hash)
//    {
//        // Convert byte array into signum representation
//        BigInteger number = new BigInteger(1, hash);
//
//        // Convert message digest into hex value
//        StringBuilder hexString = new StringBuilder(number.toString(16));
//
//        // Pad with leading zeros
//        while (hexString.length() < 32)
//        {
//            hexString.insert(0, '0');
//        }
//
//        return hexString.toString();
//    }

    public static void main(String[] args) {

//        List<String> lst;
//        lst = new ArrayList<String>();
//        lst.add("hey");

        Client c = new Client("127.0.0.1");
        ResponseFormat r = c.ExecCommand(Flags.LOGIN, "dani asd");
        System.out.println(r.status.name() + ", " + r.data);

//        System.out.println(String.format("username=%s AND password=%s", "roee", "cohen"));
    }


}
