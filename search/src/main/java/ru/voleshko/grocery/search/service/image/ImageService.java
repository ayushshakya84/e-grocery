package ru.voleshko.grocery.search.service.image;

import lombok.NonNull;

import java.io.InputStream;
import java.util.UUID;

public interface ImageService {

    void saveImage(byte[] image, @NonNull UUID id);

    InputStream getImage(@NonNull UUID key);
}
