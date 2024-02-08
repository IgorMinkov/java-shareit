package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    public Comment(String text, Long itemId, Long authorId) {
        this.text = text;
        this.itemId = itemId;
        this.authorId = authorId;
    }

}
