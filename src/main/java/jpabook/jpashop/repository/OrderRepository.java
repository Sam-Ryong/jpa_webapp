package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager entityManager;

    public void save(Order order) {
        entityManager.persist(order);
    }

    public Order findOne(Long id) {
        return entityManager.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        List<Order> resultList = entityManager.createQuery("select o from Order o join o.member m"
                + "where o.status = :status"
                + "and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)
                .getResultList();
        return resultList;
    }

    public List<Order> findAll() {
        List<Order> resultList = entityManager.createQuery("select o from Order o join o.member m", Order.class)
                .setMaxResults(1000)
                .getResultList();
        return resultList;
    }
}
