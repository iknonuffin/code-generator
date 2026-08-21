package ${}.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "${}")
@NoArgsConstructor
@Getter @Setter
public class ${} {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ${} id;

<#list  as  >
    @Column(nullable = ${}, unique = ${})
    private ${} ${};
</#list>
}