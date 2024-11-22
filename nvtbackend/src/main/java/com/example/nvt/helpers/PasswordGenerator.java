package com.example.nvt.helpers;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

public class PasswordGenerator {

    public static String generatePassword(String email, String filePath, int passwordLength) {
        String generatedPassword = generatePassword(passwordLength);

        savePasswordToFile(email, generatedPassword, filePath);
        return generatedPassword;
    }

    private static String generatePassword(int length) {

        final String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        final String numbers = "0123456789";
        final String specialChars = "!@#$%^&*()-_=+<>?";

        String allChars = upperCase + lowerCase + numbers + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return shuffleString(password.toString(), random);
    }

    private static String shuffleString(String input, SecureRandom random) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    private static void savePasswordToFile(String email, String password, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("email:" + email + System.lineSeparator());
            writer.write("password:" + password + System.lineSeparator());
            System.out.println("Password saved to file: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving password to file: " + e.getMessage());
        }
    }
}
