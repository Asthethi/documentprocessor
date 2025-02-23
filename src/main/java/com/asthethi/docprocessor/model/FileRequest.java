package com.asthethi.docprocessor.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequest {
    @NotNull(message = "The file cannot be null")
    private MultipartFile document;
    @NotNull(message = "The fileType cannot be null")
    private FileType fileType;
}
