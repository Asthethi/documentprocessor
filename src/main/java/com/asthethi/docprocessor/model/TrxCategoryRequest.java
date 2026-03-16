package com.asthethi.docprocessor.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class TrxCategoryRequest {

    private String name;
    private List<CategoryKeyword> keywords;
}
