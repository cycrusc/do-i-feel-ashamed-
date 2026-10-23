import java.util.Date;
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
                sb.append("Generated: ").append(new Date().toString()).append('\n');
                List<Product> all = ctx.getInventory().getAllProducts();
                int units = 0;
                double value = 0;
                for (Product p : all) {
                    units += p.getQuantity();
                    value += p.getQuantity() * p.getPrice();
                }
                sb.append("Products: ").append(all.size()).append('\n');
                sb.append("Total units in stock: ").append(units).append('\n');
                sb.append(String.format("Total stock value: %.2f", value)).append('\n');
                sb.append("Low-stock products: ").append(ctx.getInventory().checkLowStock().size());
                break;
            }
            case "ORDERS": {
                sb.append("Generated: ").append(new Date().toString()).append('\n');
                int completed = 0, rejected = 0;
                for (CustomerOrder o : ctx.getProcessedOrders()) {
                    if (OrderStatus.COMPLETED.equals(o.getStatus())) completed++;
                    else if (OrderStatus.REJECTED.equals(o.getStatus())) rejected++;
                }
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
        String[] options = {
                "Delete product", "View all products", "View low-stock alerts",
                "View pending customer orders", "Review restock orders (approve / reject)",
                "Receive restock delivery", "View reports"
        };
        while (isLoggedIn()) {
            ConsoleUI.printMenu("STORE MANAGER  -  " + username, options);
            int choice = ConsoleInput.readInt("  Select an option: ", 0);

            try {
                switch (choice) {
                    case 1: deleteProductFlow(); break;
                    case 2: printProducts("ALL PRODUCTS", ctx.getInventory().getAllProducts()); break;
                    case 3: printProducts("LOW-STOCK ALERTS", ctx.getInventory().checkLowStock()); break;
                    case 4: printPendingOrders(); break;
                    case 5: reviewRestockFlow(); break;
                    case 6: receiveDeliveryFlow(); break;
                    case 7: reportFlow(); break;
                    case 0: logout(); ConsoleUI.info("Logged out."); break;
                    default: ConsoleUI.error("Invalid option.");
                }
            } catch (Exception e) {
                ConsoleUI.error(e.getMessage());
            }
        }
    }

    private void deleteProductFlow() {
        String id = ConsoleInput.readString("  Product ID to delete: ");
        String confirm = ConsoleInput.readString("  Delete " + id + "? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            ConsoleUI.info("Cancelled.");
            return;
        }
        if (deleteProduct(id)) ConsoleUI.success("Product deleted.");
        else ConsoleUI.error("Product not found.");
    }

    private void reviewRestockFlow() {
        List<Order> pending = ctx.restockOrdersWithStatus(OrderStatus.PENDING);
        if (pending.isEmpty()) {
            ConsoleUI.info("No restock orders waiting for approval.");
            return;
        }
        printRestockOrders("RESTOCK ORDERS AWAITING APPROVAL", pending);
        Order order = ctx.findRestockOrder(ConsoleInput.readString("  Order ID: "));
        if (order == null || !OrderStatus.PENDING.equals(order.getStatus())) {
            ConsoleUI.error("That is not a pending restock order.");
            return;
        }
        String decision = ConsoleInput.readString("  Approve (A) or Reject (R)? ");
        if (decision.equalsIgnoreCase("A")) {
            approveOrder(order);
            ConsoleUI.success(order.getOrderID() + " approved.");
        } else if (decision.equalsIgnoreCase("R")) {
            rejectOrder(order);
            ConsoleUI.success(order.getOrderID() + " rejected.");
        } else {
            ConsoleUI.info("No change made.");
        }
    }

    private void receiveDeliveryFlow() {
        List<Order> approved = ctx.restockOrdersWithStatus(OrderStatus.APPROVED);
        if (approved.isEmpty()) {
            ConsoleUI.info("No approved restock orders awaiting delivery.");
            return;
        }
        printRestockOrders("APPROVED ORDERS AWAITING DELIVERY", approved);
        Order order = ctx.findRestockOrder(ConsoleInput.readString("  Order ID delivered: "));
        if (order == null || !receiveDelivery(order)) {
            ConsoleUI.error("That order cannot be received.");
            return;
        }
        ConsoleUI.success("Delivery received. " + order.getProduct().getProductName()
                + " stock is now " + order.getProduct().getQuantity() + ".");
    }

    private void reportFlow() {
        String type = ConsoleInput.readString("  Report type (INVENTORY / ORDERS): ").trim().toUpperCase();
        String body = generateReport(type);
        if (type.equals("INVENTORY") || type.equals("ORDERS")) {
            ConsoleUI.printBox(type + " REPORT", body);
        } else {
            ConsoleUI.error(body);
        }
    }
}