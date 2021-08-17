package jdbc.task_one;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Employee {
    private Long id;
    private String name;
    private BigDecimal salary;
    private LocalDateTime createdDate;

    public Employee(String name, BigDecimal salary) {
        this.name = name;
        this.salary = salary;
    }
}
