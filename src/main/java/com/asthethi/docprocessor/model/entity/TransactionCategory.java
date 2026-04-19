package com.asthethi.docprocessor.model.entity;

import com.asthethi.docprocessor.model.CategoryKeyword;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;


@Entity
@Table(name = "transaction_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CategoryKeyword> keywords;

    public void addKeyword(CategoryKeyword keyword) {
        keywords.add(keyword);
        keyword.setCategory(this);
    }
}
