package ru.voleshko.grocery.search.service.image;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

/**
 * Inserts images for test catalogue
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "insert-test-images", havingValue = "true")
public class TestDataCreator {

    // TODO: find out how to list resource directory contents with docker. For now just hardcoded file names
    String[] fileNames = new String[]{
            "01b8571f-39ac-4fda-9b86-083fb0050ca8.jpg",
            "5ec6d155-5e52-40d8-9e0d-332afc0b4249.jpg",
            "5f118a2a-4055-48cb-807e-3e703cd40b37.jpg",
            "7b875d4a-6991-4648-9d64-63a96581e484.jpg",
            "10d6c886-b90a-4f04-a912-498eeb652fa5.jpg",
            "55e7ef91-9570-41f0-b858-5927252cba1e.jpg",
            "95d7eab5-7ce1-4e33-ae23-874f346da258.jpg",
            "193bc156-0332-484c-ad42-3e7519ba0e2a.jpg",
            "379f4418-ffd4-4e88-a118-88ccfae5a8d3.jpg",
            "869a77ea-113c-43f3-b62e-41582a2058dc.jpg",
            "3459cd59-bd31-4ea9-889e-5e8d0998267b.jpg",
            "9395f3a4-e3b3-412f-8311-86f86cbf144e.jpg",
            "90159799-5232-4509-b357-376ff96470a5.jpg",
            "a28cfe23-9752-434f-a0d2-0864651374a3.jpg",
            "abffc680-825f-4af5-95b6-77234723a72e.jpg",
            "acbb37c0-7356-4281-a8ac-a81b6c2e01ce.jpg",
            "bc6bbc28-75bd-4701-96c5-b394a6c9f5ad.jpg",
            "bdfadd8e-a87a-4023-8821-eb0684d1d557.jpg",
            "cb70bfab-14bf-4c5a-84ff-00dd23d9c0af.jpg",
            "d4d36d16-1ab8-4479-8ad1-fbebfc2b19e8.jpg",
            "f6960d25-6dbb-491d-aca0-0cee9ee2e669.jpg",
            "fcb8fa9b-d767-4076-b0f1-2cdceb16eaaa.jpg"
    };
    private final ImageService imageService;

    @EventListener(ApplicationReadyEvent.class)
    public void doOnStartUp() {
        insertTestImages();
    }

    @SneakyThrows
    private void insertTestImages() {
        try {
            byte[] image;
            for (String fileName : fileNames) {
                try (InputStream in = getClass().getResourceAsStream("/img/" + fileName)) {
                    image = IOUtils.toByteArray(in);
                    imageService.saveImage(image, getFileId(fileName));
                }
            }
            log.info("Photos for test product catalogue uploaded successfully");

        } catch (Exception e) {
            log.error("Was not able to start application. Error on reading test image: ", e);
            System.exit(-1);
        }
    }

    private UUID getFileId(String fileName) {
        return UUID.fromString(FilenameUtils.removeExtension(fileName));
    }
}
