package com.tribmle.service;



import com.tribmle.entity.*;
import com.tribmle.repository.CarRepository;
import com.tribmle.repository.LeaseHistoryRepository;
import com.tribmle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private LeaseHistoryRepository leaseHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private User user;
    private Car car;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setRole(UserRole.CAR_OWNER);

        car = new Car();
        car.setId(1L);
        car.setStatus(CarStatus.IDLE);
    }

    @Test
    void testRegisterUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User savedUser = carService.registerUser(user);
        assertNotNull(savedUser);
        assertEquals(1L, savedUser.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddCar() {
        when(carRepository.save(any(Car.class))).thenReturn(car);
        Car savedCar = carService.addCar(car);
        assertNotNull(savedCar);
        assertEquals(1L, savedCar.getId());
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void testGetAllCars() {
        when(carRepository.findAll()).thenReturn(Collections.singletonList(car));
        List<Car> cars = carService.getAllCars();
        assertFalse(cars.isEmpty());
        assertEquals(1, cars.size());
    }

    @Test
    void testGetCarById() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        Optional<Car> retrievedCar = carService.getCarById(1L);
        assertTrue(retrievedCar.isPresent());
        assertEquals(1L, retrievedCar.get().getId());
    }

    @Test
    void testLeaseCar() {
        User customer = new User();
        customer.setId(2L);
        customer.setRole(UserRole.END_CUSTOMER);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(leaseHistoryRepository.save(any(LeaseHistory.class))).thenReturn(new LeaseHistory());

        Car leasedCar = carService.leaseCar(1L, 2L);
        assertEquals(CarStatus.ON_LEASE, leasedCar.getStatus());
        verify(carRepository, times(1)).save(car);
        verify(leaseHistoryRepository, times(1)).save(any(LeaseHistory.class));
    }

    @Test
    void testEndLease() {
        car.setStatus(CarStatus.ON_LEASE);
        User customer = new User();
        customer.setId(2L);
        car.setLeasedBy(customer);

        LeaseHistory leaseHistory = new LeaseHistory();
        leaseHistory.setCar(car);
        leaseHistory.setCustomer(customer);
        leaseHistory.setLeaseStart(LocalDateTime.now());

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(leaseHistoryRepository.findByCustomerId(2L)).thenReturn(Collections.singletonList(leaseHistory));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(leaseHistoryRepository.save(any(LeaseHistory.class))).thenReturn(leaseHistory);

        Car updatedCar = carService.endLease(1L);
        assertEquals(CarStatus.IDLE, updatedCar.getStatus());
        verify(carRepository, times(1)).save(car);
        verify(leaseHistoryRepository, times(1)).save(any(LeaseHistory.class));
    }

    @Test
    void testGetLeaseHistory() {
        User requester = new User();
        requester.setId(1L);
        requester.setRole(UserRole.ADMIN);

        LeaseHistory leaseHistory = new LeaseHistory();
        leaseHistory.setCustomer(user);
        leaseHistory.setCar(car);
        leaseHistory.setLeaseStart(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(leaseHistoryRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(leaseHistory));

        List<LeaseHistory> leaseHistories = carService.getLeaseHistory(1L, 1L);
        assertFalse(leaseHistories.isEmpty());
        assertEquals(1, leaseHistories.size());
    }
}

