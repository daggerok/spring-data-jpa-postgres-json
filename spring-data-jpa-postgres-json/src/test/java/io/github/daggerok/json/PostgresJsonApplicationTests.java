package io.github.daggerok.json;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
class PostgresJsonApplicationTests {

    @Autowired
    UserEventRepository userEventRepository;

    @Test
    void should_create_user_and_update_username_and_age() {
        log.info("size before: {}", userEventRepository.findAll().size());

        userEventRepository.save(new UserEvent().setData(new UserCreatedEvent()));
        userEventRepository.save(new UserEvent().setData(new UsernameUpdatedEvent().setName("Max")));
        userEventRepository.save(new UserEvent().setData(new UserAgeUpdatedEvent().setAge(39)));

        log.info("size after: {}", userEventRepository.findAll());
    }
}
