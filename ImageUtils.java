import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class ImageUtils {

    public static byte[] readImage(String inputImagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(inputImagePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    public static void saveToFile(byte[] data, String outputImagePath) throws Exception {
        Path path = Paths.get(outputImagePath);
        Files.write(path, data);
    }
}
