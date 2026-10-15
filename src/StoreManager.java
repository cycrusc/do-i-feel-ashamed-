import java.util.List;

public class StoreManager extends User implements Report {

    public StoreManager(String userID, String username, String password, StoreContext ctx) {
        super(userID, username, password, AccessLevel.MANAGER, ctx);
    }

    // =============== core logic ===============

    public boolean deleteProduct(String productID) {
        return ctx.getInventory().deleteProduct(productID);
    }

    public void approveOrder(Order o) {
        o.approve();
    }

    public void rejectOrder(Order o) {
        o.reject();
    }

    /** The supplier delivers an approved order; stock is added to the product. */
    public boolean receiveDelivery(Order o) {
        return o.getSupplier().fulfillOrder(o);
    }

    @Override
    public String generateReport(String reportType) {
        StringBuilder sb = new StringBuilder();
        switch (reportType.trim().toUpperCase()) {
            case "INVENTORY": {
                List<Product> all = ctx.getInventory().getAllProducts();
                int units = 0;
                double value = 0;
                for (Product p : all) {
                    units += p.getQuantity();
                    value += p.getQuantity() * p.getPrice();
                }
                sb.append("--- Inventory Report ---\n");
                sb.append("Products: ").append(all.size()).append('\n');
                sb.append("Total units in stock: ").append(units).append('\n');
                sb.append(String.format("Total stock value: %.2f%n", value));
                sb.append("Low-stock products: ").append(ctx.getInventory().checkLowStock().size());
                break;
            }
            case "ORDERS": {
                int completed = 0, rejected = 0;
                for (CustomerOrder o : ctx.getProcessedOrders()) {
                    if (OrderStatus.COMPLETED.equals(o.getStatus())) completed++;
                    else if (OrderStatus.REJECTED.equals(o.getStatus())) rejected++;
                }
                sb.append("--- Orders Report ---\n");
                sb.append("Customer orders waiting: ").append(ctx.getOrderQueue().size()).append('\n');
                sb.append("Customer orders completed: ").append(completed).append('\n');
                sb.append("Customer orders rejected: ").append(rejected).append('\n');
                sb.append("Restock orders pending approval: ")
                        .append(ctx.restockOrdersWithStatus(OrderStatus.PENDING).size()).append('\n');
                sb.append("Restock orders approved (awaiting delivery): ")
                        .append(ctx.restockOrdersWithStatus(OrderStatus.APPROVED).size()).append('\n');
                sb.append("Restock orders completed: ")
                        .append(ctx.restockOrdersWithStatus(OrderStatus.COMPLETED).size());
                break;
            }
            default:
                sb.append("Unknown report type. Use INVENTORY or ORDERS.");
        }
        return sb.toString();
    }

    // =============== menu ===============

    @Override
    public void displayMenu() {
        while (isLoggedIn()) {
            System.out.println("\n=== Store Manager Menu (" + username + ") ===");
            System.out.println("1. Delete product");
            System.out.println("2. View all products");
            System.out.println("3. View low-stock alerts");
            System.out.println("4. View pending customer orders");
            System.out.println("5. Review restock orders (approve / reject)");
            System.out.println("6. Receive restock delivery");
            System.out.println("7. View reports");
            System.out.println("0. Logout");
            int choice = ConsoleInput.readInt("Choose: ", 0);

            try {
                switch (choice) {
                    case 1: deleteProductFlow(); break;
                    case 2: printProducts(ctx.getInventory().getAllProducts()); break;
                    case 3: printProducts(ctx.getInventory().checkLowStock()); break;
                    case 4: printPendingOrders(); break;
                    case 5: reviewRestockFlow(); break;
                    case 6: receiveDeliveryFlow(); break;
                    case 7: reportFlow(); break;
                    case 0: logout(); System.out.println("Logged out."); break;
                    default: System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void deleteProductFlow() {
        String id = ConsoleInput.readString("Product ID to delete: ");
        String confirm = ConsoleInput.readString("Delete " + id + "? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Cancelled.");
            return;
        }
        System.out.println(deleteProduct(id) ? "Product deleted." : "Product not found.");
    }

    private void reviewRestockFlow() {
        List<Order> pending = ctx.restockOrdersWithStatus(OrderStatus.PENDING);
        if (pending.isEmpty()) {
            System.out.println("No restock orders waiting for approval.");
            return;
        }
        for (Order o : pending) System.out.println(o.getSummary());
        Order order = ctx.findRestockOrder(ConsoleInput.readString("Order ID: "));
        if (order == null || !OrderStatus.PENDING.equals(order.getStatus())) {
            System.out.println("That is not a pending restock order.");
            return;
        }
        String decision = ConsoleInput.readString("Approve (A) or Reject (R)? ");
        if (decision.equalsIgnoreCase("A")) {
            approveOrder(order);
            System.out.println(order.getOrderID() + " approved.");
        } else if (decision.equalsIgnoreCase("R")) {
            rejectOrder(order);
            System.out.println(order.getOrderID() + " rejected.");
        } else {
            System.out.println("No change made.");
        }
    }

    private void receiveDeliveryFlow() {
        List<Order> approved = ctx.restockOrdersWithStatus(OrderStatus.APPROVED);
        if (approved.isEmpty()) {
            System.out.println("No approved restock orders awaiting delivery.");
            return;
        }
        for (Order o : approved) System.out.println(o.getSummary());
        Order order = ctx.findRestockOrder(ConsoleInput.readString("Order ID delivered: "));
        if (order == null || !receiveDelivery(order)) {
            System.out.println("That order cannot be received.");
            return;
        }
        System.out.println("Delivery received. " + order.getProduct().getProductName()
                + " stock is now " + order.getProduct().getQuantity() + ".");
    }

    private void reportFlow() {
        String type = ConsoleInput.readString("Report type (INVENTORY / ORDERS): ");
        System.out.println(generateReport(type));
    }
}