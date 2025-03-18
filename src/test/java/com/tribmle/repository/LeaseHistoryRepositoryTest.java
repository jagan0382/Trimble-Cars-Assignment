package com.tribmle.repository;


import com.tribmle.entity.LeaseHistory;
import com.tribmle.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class LeaseHistoryRepositoryTest {

    @Mock
    private LeaseHistoryRepository leaseHistoryRepository;


    @Test
    void testFindByCustomerId() {
        User customer = new User();
        customer.setId(101L); // Set customer ID

        LeaseHistory lease1 = new LeaseHistory();
        lease1.setId(1L);
        lease1.setCustomer(customer); // Assign the User object

        LeaseHistory lease2 = new LeaseHistory();
        lease2.setId(2L);
        lease2.setCustomer(customer); // Assign the User object

        when(leaseHistoryRepository.findByCustomerId(101L)).thenReturn(List.of(lease1, lease2));

        List<LeaseHistory> leases = leaseHistoryRepository.findByCustomerId(101L);

        assertThat(leases).hasSize(2);
        assertThat(leases.get(0).getCustomer().getId()).isEqualTo(101L); // Get the ID from User object
        verify(leaseHistoryRepository, times(1)).findByCustomerId(101L);
    }
}
