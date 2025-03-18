package com.tribmle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tribmle.entity.Car;
import com.tribmle.entity.LeaseHistory;
import com.tribmle.entity.User;
import com.tribmle.entity.UserRole;
import com.tribmle.repository.UserRepository;
import com.tribmle.service.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CarControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CarServiceImpl carService;

    @Mock
    private UserRepository userRepository; // Mock the repository

    @InjectMocks
    private CarController carController;



    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    @Test
    void testRegisterUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.CAR_OWNER);

        when(carService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/cars/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void testAddCar() throws Exception {
        Car car = new Car();
        car.setId(1L);

        when(carService.addCar(any(Car.class))).thenReturn(car);

        mockMvc.perform(post("/api/cars/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(car.getId()));
    }

    @Test
    void testGetAllCars() throws Exception {
        when(carService.getAllCars()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetCarById() throws Exception {
        Car car = new Car();
        car.setId(1L);

        when(carService.getCarById(1L)).thenReturn(Optional.of(car));

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(car.getId()));
    }

    @Test
    void testLeaseCar() throws Exception {
        Car car = new Car();
        car.setId(1L);

        User user = new User();
        user.setId(3L);
        user.setName("Test User");
        user.setRole(UserRole.valueOf("END_CUSTOMER"));

        // Mock user lookup
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        // Mock leasing the car
        when(carService.leaseCar(1L, 3L)).thenReturn(car);

        mockMvc.perform(post("/api/cars/lease/1/3")
                        .header("userId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(car.getId()));
    }


    @Test
    void testEndLease() throws Exception {
        Car car = new Car();
        car.setId(1L);

        when(carService.endLease(1L)).thenReturn(car);

        mockMvc.perform(post("/api/cars/end-lease/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(car.getId()));
    }

    @Test
    void testGetLeaseHistory() throws Exception {
        LeaseHistory history = new LeaseHistory();
        List<LeaseHistory> historyList = Collections.singletonList(history);

        when(carService.getLeaseHistory(1L, 2L)).thenReturn(historyList);

        mockMvc.perform(get("/api/cars/history/1?requesterId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
