package com.asthethi.docprocessor.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCategoryRequest {
    @NotBlank(message = "category is missing")
    private String category;
    @NotNull(message = "The file cannot be null")
    private MultipartFile document;
}
