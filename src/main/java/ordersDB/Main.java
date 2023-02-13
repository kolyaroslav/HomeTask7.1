package ordersDB;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            try {

                while (true) {
                    System.out.println("1: Add client");
                    System.out.println("2: View clients");
                    System.out.println("3: Add product");
                    System.out.println("4: View products");
                    System.out.println("5: Add order");
                    System.out.println("6: Make order");
                    System.out.println("7: View orders");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1" -> addClient(sc);
                        case "2" -> viewClients();
                        case "3" -> addProduct(sc);
                        case "4" -> viewProducts();
                        case "5" -> addOrder(sc);
                        case "6" -> makeOrder(sc);
                        case "7" -> viewOrders();
                        default -> {
                            return;
                        }
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static void addClient(Scanner sc) {
        System.out.print("Enter client name: ");
        String name = sc.nextLine();
        System.out.print("Enter client phone: ");
        String sPhone = sc.nextLine();
        Long phone = Long.parseLong(sPhone);

        em.getTransaction().begin();
        try {
            Clients client = new Clients(name, phone);
            em.persist(client);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void addOrder(Scanner sc) {
        System.out.print("Enter order status: ");
        System.out.print("1. Done ");
        System.out.print("2. Active ");
        System.out.print("3. Cancelled ");
        String sStatus = sc.nextLine();
        OrderStatus orderStatus;
        if (sStatus.equals("1")) {
            orderStatus = OrderStatus.DONE;
        } else if (sStatus.equals("2")) {
            orderStatus = OrderStatus.ACTIVE;
        } else orderStatus = OrderStatus.CANCELLED;

        em.getTransaction().begin();
        try {
            Orders orders = new Orders(orderStatus);
            em.persist(orders);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void addProduct(Scanner sc) {
        System.out.print("Enter product name: ");
        String name = sc.nextLine();
        System.out.print("Enter product quantity: ");
        String sQuantity = sc.nextLine();
        Integer quantity = Integer.parseInt(sQuantity);

        em.getTransaction().begin();
        try {
            Products products = new Products(name, quantity);
            em.persist(products);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewProducts() {
        TypedQuery<Products> query = em.createQuery("SELECT p FROM Products p", Products.class);
        List<Products> list = query.getResultList();

        for (Products p : list)
            System.out.println(p);
    }

    private static void viewClients() {
        TypedQuery<Clients> query = em.createQuery("SELECT c FROM Clients c", Clients.class);
        List<Clients> list = query.getResultList();

        for (Clients c : list)
            System.out.println(c);
    }

    private static void viewOrders() {
        TypedQuery<Orders> query = em.createQuery("SELECT o FROM Orders o", Orders.class);
        List<Orders> list = query.getResultList();

        for (Orders o : list)
            System.out.println(o);
    }

    private static void makeOrder(Scanner sc) {
        System.out.println("Enter your name:");
        String name = sc.nextLine();
        System.out.println("Enter your phone:");
        String sPhone = sc.nextLine();
        Long phone = Long.parseLong(sPhone);
        var client = clientCheck(name, phone);
        var order = chooseProduct(sc);

        TypedQuery<Object> query = em.createQuery("SELECT o.productsSet FROM Orders o " +
                "WHERE o.id =:c ", Object.class);
        query.setParameter("c", order.getOrderNumber());
        var res = query.getResultList();

        System.out.println("Your order is " + res);
        System.out.println("Do you confirm your order ? type (yes) - to confirm");
        String answer = sc.nextLine();

        em.getTransaction().begin();
        if (answer.equals("yes")) {
            order.setStatus(OrderStatus.DONE.getV());
            client.addOrder(order);
        } else {
            order.setStatus(OrderStatus.CANCELLED.getV());
            System.out.println("Your order is Cancelled");
        }
        em.getTransaction().commit();
    }

    private static Clients clientCheck(String name, Long phone) {
        TypedQuery<Clients> query = em.createQuery("SELECT c FROM Clients c", Clients.class);
        for (Clients c : query.getResultList()) {
            if (c.getName().equals(name) && c.getPhone().equals(phone)) {
                return c;
            }
        }
        Clients client = new Clients(name, phone);
        em.getTransaction().begin();
        em.persist(client);
        em.getTransaction().commit();
        return client;
    }

    private static Orders chooseProduct(Scanner sc) {
        System.out.println("Choose product by id");
        viewProducts();
        Orders orders = new Orders(OrderStatus.ACTIVE);
        TypedQuery<Products> query = em.createQuery("SELECT p FROM Products p", Products.class);
        List<Products> list = query.getResultList();
        while (true) {
            String prNum = sc.nextLine();
            if (prNum.equals("")) {
                break;
            } else {
                Long l = Long.parseLong(prNum);
                for (Products p : list) {
                    if (p.getProductCode().equals(l)) {
                        orders.addProduct(p);
                    }
                }
            }
        }
        em.getTransaction().begin();
        em.persist(orders);
        em.getTransaction().commit();
        return orders;
    }
}
