package com.asthethi.docprocessor.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequest {
    private MultipartFile document;
    private FileType fileType;
}
