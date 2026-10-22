
package com.fittrack_app.fittrack.modules.producte.entity;

import com.fittrack_app.fittrack.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "product_search_history",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_search_user_query",
                        columnNames = {"user_id", "normalized_query"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "query_text",
            nullable = false,
            length = 200
    )
    private String queryText;

    @Column(
            name = "normalized_query",
            nullable = false,
            length = 200
    )
    private String normalizedQuery;

    @Column(
            name = "last_searched_at",
            nullable = false
    )
    private Instant lastSearchedAt;
}
