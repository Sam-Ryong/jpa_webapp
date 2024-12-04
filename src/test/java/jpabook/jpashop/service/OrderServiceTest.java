package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    OrderService orderService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        Member member = createMember();

        Item book = getBook("시골 JPA", 10000, 10);
        entityManager.persist(book);

        Long orderId = orderService.order(member.getId(), book.getId(), 2);

        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        Assertions.assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        Assertions.assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * 2);
        Assertions.assertThat(book.getStockQuantity()).isEqualTo(8);
    }

    @Test
    public void 상품주문재고초과() throws Exception{
        Member member = createMember();

        Item book = getBook("시골 JPA", 10000, 10);

        int orderCount = 11;
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), orderCount));

    }

    @Test
    public void 주문취소() {
        Member member = createMember();
        Item book = getBook("시골 JPA", 10000, 10);
        Long orderId = orderService.order(member.getId(), book.getId(), 2);
        Order getOrder = orderRepository.findOne(orderId);
        orderService.cancel(orderId);
        Assertions.assertThat(orderRepository.findOne(orderId).getStatus()).isEqualTo(OrderStatus.CANCEL);
        Assertions.assertThat(book.getStockQuantity()).isEqualTo(10);

    }


    private Item getBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        entityManager.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        entityManager.persist(member);
        return member;
    }

}