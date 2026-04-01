package com.algaworks.algashop.ordering.domain.entity;


import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.utility.IdGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CustomerTest {

    @Test
    void given_invalidEMail_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new Customer(
                IdGenerator.generatedTimeBasedUUID(),
                "Jhon De",
                LocalDate.of(1991, 7, 5),
                "invalid",
                "345234234",
                "5345345",
                OffsetDateTime.now(),
                false
        );
                });
    }

    @Test
    void given_invalidEMail_whenTryUpdatedCustomer_shouldGenerateException() {
        Customer customer = new Customer(
                IdGenerator.generatedTimeBasedUUID(),
                "Jhon De",
                LocalDate.of(1991, 7, 5),
                "juan@gmail.com",
                "345234234",
                "5345345",
                OffsetDateTime.now(),
                false
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail("invalid");
                });
    }

    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = new Customer(
                IdGenerator.generatedTimeBasedUUID(),
                "John Doe",
                LocalDate.of(1991, 7, 5),
                "john.doe@gmail.com",
                "478-256-2504",
                "255-08-0578",
                OffsetDateTime.now(),
                false
        );

        customer.archive();

        Assertions.assertWith(customer,
                c -> assertThat(c.fullName()).isEqualTo("Anonymous"),
                c -> assertThat(c.email()).isNotEqualTo("john.doe@gmail.com"),
                c -> assertThat(c.phone()).isEqualTo("000-000-0000"),
                c -> assertThat(c.document()).isEqualTo("000-00-0000"),
                c -> assertThat(c.birthDate()).isNull()
        );

    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
        Customer customer = new Customer(
                IdGenerator.generatedTimeBasedUUID(),
                "Anonymous",
                null,
                "anonymous@anonymous.com",
                "000-000-0000",
                "000-00-0000",
                false,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                10
        );

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(()-> customer.changeEmail("email@gmail.com"));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(()-> customer.changePhone("123-123-1111"));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(()-> customer.enablePromotionNotifications());

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(()-> customer.disablePromotionNotifications());
    }

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        Customer customer = new Customer(
                IdGenerator.generatedTimeBasedUUID(),
                "John Doe",
                LocalDate.of(1991, 7, 5),
                "john.doe@gmail.com",
                "478-256-2504",
                "255-08-0578",
                OffsetDateTime.now(),
                false
        );

        customer.addLoyaltyPoints(10);
        customer.addLoyaltyPoints(20);

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(30);
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = new Customer(
                IdGenerator.generatedTimeBasedUUID(),
                "John Doe",
                LocalDate.of(1991, 7, 5),
                "john.doe@gmail.com",
                "478-256-2504",
                "255-08-0578",
                OffsetDateTime.now(),
                false
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> customer.addLoyaltyPoints(0));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> customer.addLoyaltyPoints(-10));
    }
}
