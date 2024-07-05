package bpm.graalvm.js;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExtractFileToTemp {

    private File createTempDirectory() throws IOException {
        File tempDir = File.createTempFile("temp", Long.toString(1));
        if (!(tempDir.delete())) {
            throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
        }
        if (!(tempDir.mkdir())) {
            throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
        }
        tempDir.deleteOnExit();
        return tempDir;
    }

    private String extractFileToTemp(String jarFilePath, String fileName, File tempDir) throws IOException {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().equals(fileName)) {
                    File outputFile = new File(tempDir, fileName);
                    try (InputStream inputStream = jarFile.getInputStream(entry); FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                        outputFile.deleteOnExit();
                        return outputFile.getAbsolutePath();
                    }
                }
            }
        }
        return null;
    }

    public String getTempFilePath(String jarFilePath, String fileName) {
        String extractedFilePath = null;
        try {
            File tempDir = createTempDirectory();
            extractedFilePath = extractFileToTemp(jarFilePath, fileName, tempDir);
            if (extractedFilePath == null) {
                System.out.println("Failed to extract file or file not found in JAR.");
            }
            System.out.println("Extracted at: "+extractedFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractedFilePath;
    }
}
