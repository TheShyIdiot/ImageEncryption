import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageEncryption {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 128;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            SecretKey secretKey;
            IvParameterSpec iv;

            // Check if key and IV files exist
            if (Files.exists(Paths.get("key.txt")) && Files.exists(Paths.get("iv.txt"))) {
                // Read the key and IV from files
                String base64Key = new String(Files.readAllBytes(Paths.get("key.txt")));
                String base64IV = new String(Files.readAllBytes(Paths.get("iv.txt")));
                secretKey = base64ToKey(base64Key);
                iv = base64ToIV(base64IV);
            } else {
                // Generate a new key and IV
                secretKey = generateSecretKey();
                iv = generateIV();

                // Store the key and IV
                String base64Key = keyToBase64(secretKey);
                String base64IV = ivToBase64(iv);
                Files.write(Paths.get("key.txt"), base64Key.getBytes());
                Files.write(Paths.get("iv.txt"), base64IV.getBytes());
            }

            // User prompt to decide encryption or decryption
            System.out.print("Do you want to (E)ncrypt or (D)ecrypt the image? (E/D): ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) {
                // Encrypt the image
                encryptImage("C:\\Users\\Tanmay Durwasha\\Desktop\\learnsh\\ImageEncryption\\sample.jpg",
                        "C:\\Users\\Tanmay Durwasha\\Desktop\\learnsh\\ImageEncryption\\encrypted.jpg",
                        secretKey, iv);
            } else if (action.equals("D")) {
                // Decrypt the image
                decryptImage("C:\\Users\\Tanmay Durwasha\\Desktop\\learnsh\\ImageEncryption\\encrypted.jpg",
                        "C:\\Users\\Tanmay Durwasha\\Desktop\\learnsh\\ImageEncryption\\decrypted.jpg",
                        secretKey, iv);
                // Ask user if they want to print the decrypted image
                System.out.print("Do you want to print the decrypted image? (yes/no): ");
                String userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase("yes")) {
                    printDecryptedImage("C:\\Users\\Tanmay Durwasha\\Desktop\\learnsh\\ImageEncryption\\decrypted.jpg");
                } else {
                    System.out.println("Decrypted image will not be printed.");
                }
            } else {
                System.out.println("Invalid option. Please enter 'E' for encrypt or 'D' for decrypt.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

    private static IvParameterSpec generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static void encryptImage(String inputImagePath, String outputImagePath, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        // Read the image into bytes
        byte[] inputBytes = ImageUtils.readImage(inputImagePath);
        System.out.println("Input bytes length: " + inputBytes.length);

        // Encrypt the image
        byte[] encryptedBytes = cipher.doFinal(inputBytes);
        System.out.println("Encrypted bytes length: " + encryptedBytes.length);

        // Save the encrypted bytes to a file
        ImageUtils.saveToFile(encryptedBytes, outputImagePath);
        System.out.println("Encrypted image saved to: " + outputImagePath);
    }

    private static void decryptImage(String encryptedImagePath, String outputImagePath, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        // Read the encrypted image bytes
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(encryptedImagePath));

        // Decrypt the image
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Save the decrypted image
        ImageUtils.saveToFile(decryptedBytes, outputImagePath);
        System.out.println("Decrypted image saved to: " + outputImagePath);
    }

    private static void printDecryptedImage(String imagePath) throws IOException {
        // Display the image (this requires a GUI environment)
        BufferedImage img = ImageIO.read(new File(imagePath));
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        JLabel label = new JLabel(new ImageIcon(img));
        frame.getContentPane().add(label);
        frame.setVisible(true);
    }

    // Convert key to Base64
    private static String keyToBase64(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Convert Base64 to key
    private static SecretKey base64ToKey(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // Convert IV to Base64
    private static String ivToBase64(IvParameterSpec iv) {
        return Base64.getEncoder().encodeToString(iv.getIV());
    }

    // Convert Base64 to IV
    private static IvParameterSpec base64ToIV(String base64IV) {
        byte[] decodedIV = Base64.getDecoder().decode(base64IV);
        return new IvParameterSpec(decodedIV);
    }
}
