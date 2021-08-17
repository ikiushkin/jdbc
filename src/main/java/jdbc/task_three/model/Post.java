package jdbc.task_three.model;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Post {
    private Long id;
    private Long userId;
    private String text;
    private LocalDateTime createdDate;

    public Post(Long userId, String text) {
        this.userId = userId;
        this.text = text;
    }
}
