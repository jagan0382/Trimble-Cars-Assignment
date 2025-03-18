package com.tribmle.repository;


import com.tribmle.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testFindById() {
        User user = new User();
        user.setId(1L);
        user.setName("Trimble");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findById(1L);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Trimble");
        verify(userRepository, times(1)).findById(1L);
    }
}
