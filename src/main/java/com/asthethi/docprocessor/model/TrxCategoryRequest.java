package com.asthethi.docprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
