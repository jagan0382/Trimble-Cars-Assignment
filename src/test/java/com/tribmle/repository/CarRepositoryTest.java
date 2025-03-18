package com.tribmle.repository;


import com.tribmle.entity.Car;
import com.tribmle.entity.CarStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //
@ActiveProfiles("test")
class CarRepositoryTest {

    @Mock
    private CarRepository carRepository;

    @Test
    void testFindByStatus() {
        Car car1 = new Car();
        car1.setId(1L);
        car1.setStatus(CarStatus.IDLE);

        Car car2 = new Car();
        car2.setId(2L);
        car2.setStatus(CarStatus.IDLE);

        when(carRepository.findByStatus("IDLE")).thenReturn(List.of(car1, car2));

        List<Car> idleCars = carRepository.findByStatus("IDLE");

        assertThat(idleCars).hasSize(2);
    }
}
