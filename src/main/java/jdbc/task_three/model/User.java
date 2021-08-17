package jdbc.task_three.model;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    private String name;
    private String surname;
    private LocalDateTime createdDate;

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }
}
