package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;


public class OrderTest {
    @Test
    public void shouldGenerate() {
        Order order = Order.draft(new CustomerId());
    }

    @Test
    public void shouldAddItem() {
        Order order = Order.draft(new CustomerId());
        ProductId productId = new ProductId();

        order.addItem(
                Product.builder()
                        .id(productId)
                        .name(new ProductName("Mouse pad"))
                        .price(new Money("100"))
                        .inStock(true).build(),
                new Quantity(1)
        );

        Assertions.assertThat(order.items().size()).isEqualTo(1);

        OrderItem orderItem = order.items().iterator().next();

        Assertions.assertWith(orderItem,
                (i) -> Assertions.assertThat(i.id()).isNotNull(),
                (i) -> Assertions.assertThat(i.productName()).isEqualTo(new ProductName("Mouse pad")),
                (i) -> Assertions.assertThat(i.productId()).isEqualTo(productId),
                (i) -> Assertions.assertThat(i.price()).isEqualTo(new Money("100")),
                (i) -> Assertions.assertThat(i.quantity()).isEqualTo(new Quantity(1))
        );
    }

    @Test
    public void shouldGenerateExceptionWhenTryToChangeItemSet() {
        Order order = Order.draft(new CustomerId());
        order.addItem(
                Product.builder()
                        .id(new ProductId())
                        .name(new ProductName("Mouse pad"))
                        .price(new Money("100"))
                        .inStock(true)
                        .build(),
                new Quantity(1)
        );

        Set<OrderItem> items = order.items();

        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(items::clear);
    }

    @Test
    public void shouldCalculateTotals() {
        Order order = Order.draft(new CustomerId());
        ProductId productId = new ProductId();

        order.addItem(
                Product.builder()
                        .name(new ProductName("Mouse pad"))
                        .price(new Money("100"))
                        .build(),
                new Quantity(2)
        );

        order.addItem(
                Product.builder()
                        .name(new ProductName("Keyboard"))
                        .price(new Money("50"))
                        .build(),
                new Quantity(1)
        );

        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("250"));
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(3));
    }

    @Test
    public void givenDraftOrder_whenPlace_shouldChangeToPlaced() {
        Order order = OrderTestDataBuilder.anOrder().build();
        order.place();
        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    public void givenPlacedOrder_whenMarkAsPaid_shouldChangeToPaid() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        order.markAsPaid();
        Assertions.assertThat(order.isPaid()).isTrue();
        Assertions.assertThat(order.paidAt()).isNotNull();
    }

    @Test
    public void givenDraftOrder_whenChangePaymentMethod_shouldAllowChange() {
        Order order = Order.draft(new CustomerId());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        Assertions.assertWith(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    public void givenDraftOrder_whenChangeBillingInfo_shouldAllowChange() {
        Address address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt. 11")
                .city("Montfort")
                .state("South Carolina")
                .zipCode(new ZipCode("79911")).build();

        Billing billingInfo = Billing.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("John", "Doe"))
                .email(new Email("jucami90@gmail.com"))
                .build();

        Order order = Order.draft(new CustomerId());
        order.changeBilling(billingInfo);

        Billing expectedBillingInfo = Billing.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("John", "Doe"))
                .build();

        Assertions.assertThat(order.billing()).isEqualTo(expectedBillingInfo);
    }

    @Test
    public void givenDraftOrder_whenChangeShippingInfo_shouldAllowChange() {
        Address address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt. 11")
                .city("Montfort")
                .state("South Carolina")
                .zipCode(new ZipCode("79911")).build();

        Shipping shippingInfo = Shipping.builder()
                .address(address)
                .recipient(Recipient.builder()
                        .fullName(new FullName("Jose","Mae"))
                        .document(new Document("112-33-2321"))
                        .phone(new Phone("111-441-1244")).build())
                .cost(Money.ZERO)
                .expectedDate(LocalDate.now().plusDays(1)).build();


        Order order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(1);

        order.changeShipping(shippingInfo);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shippingInfo),
                o -> Assertions.assertThat(o.shippingCost()).isEqualTo(shippingCost),
                o -> Assertions.assertThat(o.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate)
        );

    }

    @Test
    public void givenDraftOrderAndDeliveryDateInThePast_whenChangeShippingInfo_shouldNotAllowChange() {
        Address address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt. 11")
                .city("Montfort")
                .state("South Carolina")
                .zipCode(new ZipCode("79911")).build();

        Shipping shippingInfo = Shipping.builder()
                .address(address)
                .recipient(Recipient.builder()
                        .fullName(new FullName("Jose","Mae"))
                        .document(new Document("112-33-2321"))
                        .phone(new Phone("111-441-1244")).build())
                .cost(new Money("50"))
                .expectedDate(LocalDate.now().plusDays(1)).build();

        Order order = Order.draft(new CustomerId());

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(()-> order.changeShipping(shippingInfo));
    }

    @Test
    public void givenDraftOrder_whenChangeItem_shouldRecalculate() {
        Order order = Order.draft(new CustomerId());

        order.addItem(
                Product.builder()
                        .name(new ProductName("Mouse pad"))
                        .price(new Money("100"))
                        .build(),
                new Quantity(3)
        );

        OrderItem orderItem = order.items().iterator().next();

        order.changeItemQuantity(orderItem.id(), new Quantity(5));

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.totalAmount()).isEqualTo(new Money("500")),
                (o)-> Assertions.assertThat(o.totalItems()).isEqualTo(new Quantity(5))
        );


    }


}
