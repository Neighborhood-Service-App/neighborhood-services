package com.neighborhoodservice.user.enumeration;

import com.neighborhoodservice.user.exception.UnsupportedFileTypeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public enum FileType {

    // Enum constants representing file types with their extensions and corresponding media types
    JPG("jpg", MediaType.IMAGE_JPEG),
    JPEG("jpeg", MediaType.IMAGE_JPEG),
    PNG("png", MediaType.IMAGE_PNG),
    PDF("pdf", MediaType.APPLICATION_PDF);

    // File extension
    private final String extension;

    // Media type associated with the file extension
    private final MediaType mediaType;

    // Method to get MediaType based on the filename's extension
    public static MediaType fromFilename(String fileName) {
        // Find the last index of '.' to extract the extension
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new UnsupportedFileTypeException(
                    "File '" + fileName + "' does not have a valid extension."
            );
        }

        String fileExtension = fileName.substring(dotIndex + 1).toLowerCase(); // Ensure case-insensitivity
        return Arrays.stream(values())
                .filter(e -> e.getExtension().equals(fileExtension))
                .findFirst()
                .map(FileType::getMediaType)
                .orElseThrow(() -> new UnsupportedFileTypeException(
                        "File extension '" + fileExtension + "' is not supported."
                ));
    }

}
