package com.MiniLms.LMSBackend.utils;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator implements IPasswordGenerator{
    public static String generateTemporaryPassword(){
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for(int i = 4; i < PASSWORD_LENGTH; i++){
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        return shuffleString(password.toString());
    }

    private static String shuffleString(String string) {
        char[] chars = string.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }
}
