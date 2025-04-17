package com.MiniLms.LMSBackend.utils;

import java.security.SecureRandom;

public interface IPasswordGenerator {
    String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String LOWER = "abcdefghijklmnopqrstuvwxyz";
    String DIGITS = "0123456789";
    String SPECIAL = "@#$%&*!";

    String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIAL;
    int PASSWORD_LENGTH = 8;
    SecureRandom random = new SecureRandom();
}
