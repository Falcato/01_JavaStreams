package space.gavinklfong.demo.streamapi.exercises;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;
import space.gavinklfong.demo.streamapi.repos.CustomerRepo;
import space.gavinklfong.demo.streamapi.repos.OrderRepo;
import space.gavinklfong.demo.streamapi.repos.ProductRepo;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Exercises {

    private final CustomerRepo customerRepos;

    private final OrderRepo orderRepos;

    private final ProductRepo productRepos;

    @Autowired
    public Exercises(CustomerRepo customerRepos, OrderRepo orderRepos, ProductRepo productRepos) {
        this.customerRepos = customerRepos;
        this.orderRepos = orderRepos;
        this.productRepos = productRepos;
    }

    /*"Obtain a list of product with category = "Books" and price > 100"*/
    public void exercise1a() {
        productRepos.findAll()
                .stream()
                .filter(x -> x.getCategory().equalsIgnoreCase("Books"))
                .filter(x -> x.getPrice() > 100)
                .collect(Collectors.toList())
                .forEach(x -> log.info(x.toString()));
    }

    /*Obtain a list of product with category = "Books" and price > 100 (using BiPredicate for filter)*/
    public void exercise1b() {
        productRepos.findAll()
                .stream()
                .filter(x -> x.getCategory().equalsIgnoreCase("Books") && x.getPrice() > 100)
                .collect(Collectors.toList())
                .forEach(x -> log.info(x.toString()));
    }

    /*Obtain a list of order with product category = "Baby"*/
    public void exercise2() {
        orderRepos.findAll()
                .stream()
                .filter(o -> o.getProducts()
                        .stream()
                        .anyMatch(p -> p.getCategory().equalsIgnoreCase("Baby")))
                .collect(Collectors.toList())
                .forEach(x -> log.info(x.toString()));
    }

    /*Obtain a list of product with category = “Toys” and then apply 10% discount*/
    public void exercise3() {
        productRepos.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Toys"))
                .map(p -> p.withPrice(p.getPrice() * 0.9))
                .collect(Collectors.toList())
                .forEach(x -> log.info(x.toString()));
    }

    /*Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021*/
    public void exercise4() {
        orderRepos.findAll()
                .stream()
                .filter(o -> o.getCustomer().getTier() == 2)
                .filter(o -> o.getOrderDate().isBefore(LocalDate.of(2021, 7, 1)))
                .filter(o -> o.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)))
                .flatMap(o -> o.getProducts().stream())
                .distinct()
                .collect(Collectors.toList())
                .forEach(x -> log.info(x.toString()));
    }

    /*Get the 3 cheapest products of "Books" category*/
    public void exercise5() {
        Optional<Product> res = productRepos.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                .min(Comparator.comparing(Product::getPrice));

        log.info(res.get().toString());
    }

    /*Get the 3 most recent placed order*/
    public void exercise6() {
        List<Order> res = orderRepos.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .collect(Collectors.toList());

        res.forEach(x -> log.info(x.toString()));
    }

    /*Get a list of products which was ordered on 15-Mar-2021*/
    public void exercise7() {
        List<Product> res = orderRepos.findAll()
                .stream()
                .filter(o -> o.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .flatMap(o -> o.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());

        res.forEach(x -> log.info(x.toString()));
    }

    /*Calculate the total lump of all orders placed in Feb 2021*/
    public void exercise8() {
        Double res = orderRepos.findAll()
                .stream()
                .filter(o -> o.getOrderDate().getMonthValue() == 2)
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();

        log.info(res.toString());
    }

    /*Calculate the total lump of all orders placed in Feb 2021*/
    public void exercise8a() {
        BiFunction<Double, Product, Double> acc = (d, p)  -> d += p.getPrice();

        Double res = orderRepos.findAll()
                .stream()
                .filter(o -> o.getOrderDate().getMonthValue() == 2)
                .flatMap(o -> o.getProducts().stream())
                .reduce(0d, acc, Double::sum);

        log.info(res.toString());
    }

    /*Calculate the average price of all orders placed on 15-Mar-2021*/
    public void exercise9() {
        Double res = orderRepos.findAll()
                .stream()
                .filter(o -> o.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .average()
                .getAsDouble();

        log.info(res.toString());
    }

    /*Obtain statistics summary of all products belong to "Books" category*/
    public void exercise10() {
        DoubleSummaryStatistics statistics = productRepos.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();

        log.info(statistics.toString());
    }

    /*Obtain a mapping of order id and the order's product count*/
    public void exercise11() {
        orderRepos.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Order::getId,
                        o -> o.getProducts().size()
                ))
                .forEach((k, v) -> log.info("key: " + k + " value: " + v));
    }

    /*Obtain a data map of customer and list of orders*/
    public void exercise12() {
        orderRepos.findAll()
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer))
                .forEach((k, v) -> log.info("key: " + k + " value: " + v));
    }

    /*Obtain a data map of customer_id and list of order_id(s)*/
    public void exercise12a() {
        orderRepos.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                            o -> o.getCustomer().getId(),
                            Collectors.mapping(Order::getId, Collectors.toList())
                ))
                .forEach((k, v) -> log.info("key: " + k + " value: " + v));
    }

    /*Obtain a data map with order and its total price*/
    public void exercise13() {
        orderRepos.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Order::getId,
                        o -> o.getProducts().stream().mapToDouble(Product::getPrice).sum()
                ))
                .forEach((k, v) -> log.info("key: " + k + " value: " + v));
    }

    /*Obtain a data map with order and its total price (using reduce)*/
    public void exercise13a() {
        BiFunction<Double, Product, Double> acc = (d, p) -> d += p.getPrice();

        orderRepos.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Order::getId,
                        o -> o.getProducts().stream().reduce(0D, acc, Double::sum)
                ))
                .forEach((k, v) -> log.info("key: " + k + " value: " + v));
    }

    /*Obtain a data map of product name by category*/
    public void exercise14() {
        productRepos.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.mapping(Product::getName, Collectors.toList())
                ))
                .forEach((k, v) -> log.info("key: " + k + " value: " + v));
    }

    /*Get the most expensive product per category*/
    public void exercise15() {
        productRepos.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.maxBy(Comparator.comparing(Product::getPrice))
                ))
                .forEach((k, v) -> log.info("key: " + k + " value: " + v));
    }

    /*Get the most expensive product (by name) per category*/
    public void exercise15a() {
        productRepos.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.collectingAndThen(
                            Collectors.maxBy(Comparator.comparing(Product::getPrice)),
                            o -> o.map(Product::getName).orElse(null)
                        )
                ))
                .forEach((k, v) -> log.info("key: " + k + " value: " + v));
    }

}
