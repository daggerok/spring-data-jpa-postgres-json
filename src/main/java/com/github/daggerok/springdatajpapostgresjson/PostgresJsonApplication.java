package com.github.daggerok.springdatajpapostgresjson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonStringType;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;

@SpringBootApplication
public class PostgresJsonApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostgresJsonApplication.class, args);
    }
}

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class), // Postgres, MySQL
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) // Postgres
})
@MappedSuperclass
abstract class BaseDomainEventEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "domain_event_id_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "domain_event_id_sequence_generator", sequenceName = "domain_event_id_sequence", allocationSize = 1)
    Long id;

    @CreationTimestamp
    Instant createdAt;
}

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "user_event")
class UserEvent extends BaseDomainEventEntity {

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private EventData data;
}

interface UserEventRepository extends JpaRepository<UserEvent, Long> {
}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "UserCreatedEvent", value = UserCreatedEvent.class),
        @JsonSubTypes.Type(name = "UsernameUpdatedEvent", value = UsernameUpdatedEvent.class),
        @JsonSubTypes.Type(name = "UserAgeUpdatedEvent", value = UserAgeUpdatedEvent.class),
})
interface EventData extends Serializable {
    String getType();
}

@Data
@NoArgsConstructor
class UserCreatedEvent implements EventData {

    private String type = getClass().getSimpleName();
}

@Data
@NoArgsConstructor
class UsernameUpdatedEvent implements EventData {

    private String type = getClass().getSimpleName();

    @Accessors(chain = true)
    private String name;
}

@Data
@NoArgsConstructor
@Setter(AccessLevel.PACKAGE)
class UserAgeUpdatedEvent implements EventData {

    private String type = getClass().getSimpleName();

    @Accessors(chain = true)
    private Integer age;
}
