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

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Client c = new Client("127.0.0.1");
        String data = s.nextLine();
        ResponseFormat r = c.ExecCommand(Flags.LOGIN, data);
        System.out.println(r.status.name() + ", " + r.data);
    }
}
