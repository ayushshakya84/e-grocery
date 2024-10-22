package ru.voleshko.grocery.search.rest.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.voleshko.grocery.search.service.image.ImageService;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void save(
            @RequestPart("file") MultipartFile file,
            @RequestParam("productId") UUID productId
    ) throws IOException {
        imageService.saveImage(file.getBytes(), productId);
    }

    @GetMapping(value = "/image")
    public ResponseEntity<byte[]> getImage(@RequestParam("productId") UUID productId) throws IOException {
        try (InputStream img = imageService.getImage(productId)) {
            byte[] bytes = img.readAllBytes();
            return ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .contentLength(bytes.length)
                    .body(bytes);
        } catch (Exception e) {
            log.error("Failed to get image productId=[{}]: ", productId, e);
            return ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .contentLength(0)
                    .body(new byte[0]);
        }
    }
}
