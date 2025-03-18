package com.tribmle.service;


import com.tribmle.constants.Constants;
import com.tribmle.entity.*;
import com.tribmle.exception.NotFoundException;
import com.tribmle.exception.UnauthorizedException;
import com.tribmle.repository.CarRepository;
import com.tribmle.repository.LeaseHistoryRepository;
import com.tribmle.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class CarServiceImpl implements CarService {

    //private static final Logger log = LoggerFactory.getLogger(CarServiceImpl.class);

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private LeaseHistoryRepository leaseHistoryRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public User registerUser(User user) {
        try {
            // Validate the role
            if (user.getRole() != UserRole.CAR_OWNER && user.getRole() != UserRole.END_CUSTOMER) {
                throw new BadRequestException("Invalid role! Must be either CAR_OWNER or END_CUSTOMER.");
            }

            // Save user
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Exception while registering user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register user", e);
        }
    }



    @Override
    public Car addCar(Car car) {
        try {
            return carRepository.save(car);
        } catch (Exception e) {
            log.error("Exception while saving CarDetails: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add car", e);
        }
    }

    @Override
    public List<Car> getAllCars() {
        try {
            return carRepository.findAll();
        } catch (Exception e) {
            log.error("Exception while fetching all cars: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch cars", e);
        }
    }

    @Override
    public Optional<Car> getCarById(Long id) {
        try {
            return carRepository.findById(id);
        } catch (Exception e) {
            log.error("Exception while fetching car by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch car", e);
        }
    }

    @Override
    public Car leaseCar(Long carId, Long customerId) {
        try {
            Car car = carRepository.findById(carId).orElseThrow(() -> new NotFoundException(Constants.CAR_NOT_FOUND));
            User customer = userRepository.findById(customerId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));

            if (car.getStatus() != CarStatus.IDLE) {
                throw new RuntimeException(Constants.CAR_IS_NOT_AVAILABLE_FOR_LEASE);
            }

            car.setStatus(CarStatus.ON_LEASE);
            car.setLeasedBy(customer);
            carRepository.save(car);

            LeaseHistory history = new LeaseHistory();
            history.setCar(car);
            history.setCustomer(customer);
            history.setLeaseStart(LocalDateTime.now());
            leaseHistoryRepository.save(history);

            return car;
        } catch (Exception e) {
            log.error("Exception while leasing car {} to customer {}: {}", carId, customerId, e.getMessage(), e);
            throw new RuntimeException("Failed to lease car", e);
        }
    }

    @Override
    public Car endLease(Long carId) {
        try {
            Car car = carRepository.findById(carId).orElseThrow(() -> new NotFoundException(Constants.CAR_NOT_FOUND));

            if (car.getStatus() != CarStatus.ON_LEASE) {
                throw new RuntimeException(Constants.CAR_IS_NOT_ON_LEASE);
            }

            LeaseHistory history = leaseHistoryRepository.findByCustomerId(car.getLeasedBy().getId()).stream()
                    .filter(h -> h.getCar().getId().equals(carId) && h.getLeaseEnd() == null)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(Constants.LEASE_RECORD_NOT_FOUND));

            history.setLeaseEnd(LocalDateTime.now());
            leaseHistoryRepository.save(history);

            car.setStatus(CarStatus.IDLE);
            car.setLeasedBy(null);
            return carRepository.save(car);
        } catch (Exception e) {
            log.error("Exception while ending lease for car {}: {}", carId, e.getMessage(), e);
            throw new RuntimeException("Failed to end lease", e);
        }
    }

    @Override
    public List<LeaseHistory> getLeaseHistory(Long userId, Long requesterId) {
        try {
            User requester = userRepository.findById(requesterId)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            // Admin can access all lease histories, while users can only view their own
            if (requester.getRole() == UserRole.ADMIN || requester.getId().equals(userId)) {
                return leaseHistoryRepository.findByCustomerId(userId);
            } else {
                log.warn("Unauthorized access attempt by user {} to fetch lease history for user {}", requesterId, userId);
                throw new UnauthorizedException("You do not have permission to view this history.");
            }
        } catch (NotFoundException e) {
            log.error("User not found while fetching lease history: {}", e.getMessage());
            throw e;
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized access attempt: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception while fetching lease history for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch lease history", e);
        }
    }

}
