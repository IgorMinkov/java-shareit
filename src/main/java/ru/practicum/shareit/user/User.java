package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 127)
    @EqualsAndHashCode.Exclude
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 127)
    private String email;

}
