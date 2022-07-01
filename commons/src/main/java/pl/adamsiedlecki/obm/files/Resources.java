package pl.adamsiedlecki.obm.files;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

public class Resources {

    public static String getResourceFileAsString(String fileName) throws IOException {
        try(InputStream in = new ClassPathResource(fileName).getInputStream()) {
            return new String(in.readAllBytes());
        }
    }
}
