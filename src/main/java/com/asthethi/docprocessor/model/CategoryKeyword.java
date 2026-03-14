package com.asthethi.docprocessor.model;

import com.asthethi.docprocessor.model.entity.TransactionCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category_keyword")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private TransactionCategory category;
}
