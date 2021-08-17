package jdbc.task_three.model;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Like {
    private long postID;
    private long userID;
    private LocalDateTime createdDate;

    public Like(long postID, long userID) {
        this.postID = postID;
        this.userID = userID;
    }

    public Like(long postID, long userID, LocalDateTime createdDate) {
        this.postID = postID;
        this.userID = userID;
        this.createdDate = createdDate;
    }
}
