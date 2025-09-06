package com.tiktok.demo.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.jcip.annotations.NotThreadSafe;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadProgress {
    long uploadedBytes;
    long totalBytes;
    double percentage;
    long timestamp;
    boolean complete = false;
    String error = null;

    public String getFomattedSize() {
        return "%.2f MB / %.2f MB".formatted(
                uploadedBytes / 1024.0 / 1024.0,
                totalBytes / 1024.0 / 1024.0);
    }
}
