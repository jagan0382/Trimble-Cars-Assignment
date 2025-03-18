package com.tribmle.controller;


import com.tribmle.constants.Constants;
import com.tribmle.entity.Car;
import com.tribmle.entity.LeaseHistory;
import com.tribmle.entity.User;
import com.tribmle.entity.UserRole;
import com.tribmle.exception.NotFoundException;
import com.tribmle.exception.UnauthorizedException;
import com.tribmle.repository.UserRepository;
import com.tribmle.service.CarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarServiceImpl carService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = carService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }


    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Car addCar(@RequestBody Car car) //(@RequestBody JsonObject request)
    {
        return carService.addCar(car);
    }

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping(value = "/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Car> getCarById(@PathVariable Long id) {
        return carService.getCarById(id);

    }



    @PostMapping(value = "/lease/{carId}/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Car> leaseCar(@PathVariable Long carId, @PathVariable Long customerId, @RequestHeader("userId") Long adminId) {
        User adminUser = userRepository.findById(adminId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));

        if (adminUser.getRole() == UserRole.ADMIN || adminUser.getRole() == UserRole.END_CUSTOMER) {
            Car leasedCar = carService.leaseCar(carId, customerId);
            return ResponseEntity.ok(leasedCar);
        } else {
            throw new UnauthorizedException(Constants.ONLY_ADMINS_AND_END_CUSTOMERS_CAN_LEASE_CARS);
        }
    }




    @PostMapping( value = "/end-lease/{carId}" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public Car endLease(@PathVariable Long carId) {
        return carService.endLease(carId);
    }


    @GetMapping(value = "/history/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LeaseHistory> getLeaseHistory(@PathVariable Long userId, @RequestParam Long requesterId) {
        return carService.getLeaseHistory(userId, requesterId);
    }

}
