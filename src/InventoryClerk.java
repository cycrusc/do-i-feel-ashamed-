import java.util.List;

public class InventoryClerk extends User {

    public InventoryClerk(String userID, String username, String password, StoreContext ctx) {
        super(userID, username, password, AccessLevel.CLERK, ctx);
    }

    // =============== core logic (no Scanner, no printing) ===============

    public boolean addProduct(Product p) {
        return ctx.getInventory().addProduct(p);
    }

    public boolean editProduct(String productID, String name, double price, int reorderLevel) {
        return ctx.getInventory().editProduct(productID, name, price, reorderLevel);
    }

    public List<Product> viewLowStockAlerts() {
        return ctx.getInventory().checkLowStock();
    }

    /** Validates and enqueues a customer order. Throws with a readable message if it can't be accepted. */
    public CustomerOrder submitOrder(String customerName, String productID, int quantity) {
        Product product = ctx.getInventory().searchProduct(productID);
        if (product == null)
            throw new IllegalArgumentException("Product not found: " + productID);
        if (ctx.getOrderQueue().isFull())
            throw new IllegalStateException("Order queue is full. Process pending orders first.");
        CustomerOrder order = new CustomerOrder(customerName, product.getProductID(), quantity);
        ctx.getOrderQueue().enqueue(order);
        return order;
    }

    /** Takes the order at the front of the queue and tries to fill it. Returns null if the queue is empty. */
    public CustomerOrder processNextOrder() {
        if (ctx.getOrderQueue().isEmpty()) return null;

        CustomerOrder order = ctx.getOrderQueue().dequeue();
        Product product = ctx.getInventory().searchProduct(order.getProductID());

        if (product != null && product.reduceStock(order.getQuantity())) {
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            order.setStatus(OrderStatus.REJECTED);   // product deleted or not enough stock
        }
        ctx.getProcessedOrders().add(order);
        return order;
    }

    /** Creates and places a restock order that the manager must approve. */
    public Order requestRestock(String productID, int quantity, String supplierID) {
        Product product = ctx.getInventory().searchProduct(productID);
        if (product == null) throw new IllegalArgumentException("Product not found: " + productID);
        Supplier supplier = ctx.findSupplier(supplierID);
        if (supplier == null) throw new IllegalArgumentException("Supplier not found: " + supplierID);

        Order order = new Order(product, quantity, supplier);
        order.placeOrder();
        ctx.getRestockOrders().add(order);
        return order;
    }

    // =============== menu (all input/output lives here) ===============

    @Override
    public void displayMenu() {
        String[] options = {
                "Add product", "Edit product", "Search products", "View all products",
                "View low-stock alerts", "Take customer order", "Process next customer order",
                "View pending customer orders", "Request restock from supplier"
        };
        while (isLoggedIn()) {
            ConsoleUI.printMenu("INVENTORY CLERK  -  " + username, options);
            int choice = ConsoleInput.readInt("  Select an option: ", 0);

            try {
                switch (choice) {
                    case 1: addProductFlow(); break;
                    case 2: editProductFlow(); break;
                    case 3: searchFlow(); break;
                    case 4: printProducts("ALL PRODUCTS", ctx.getInventory().getAllProducts()); break;
                    case 5: lowStockFlow(); break;
                    case 6: takeOrderFlow(); break;
                    case 7: processOrderFlow(); break;
                    case 8: printPendingOrders(); break;
                    case 9: restockFlow(); break;
                    case 0: logout(); ConsoleUI.info("Logged out."); break;
                    default: ConsoleUI.error("Invalid option.");
                }
            } catch (Exception e) {
                ConsoleUI.error(e.getMessage());
            }
        }
    }

    private void addProductFlow() {
        String id = ConsoleInput.readString("  Product ID: ");
        String name = ConsoleInput.readString("  Name: ");
        int qty = ConsoleInput.readInt("  Quantity: ", 0);
        double price = ConsoleInput.readDouble("  Price: ", 0);
        int reorder = ConsoleInput.readInt("  Reorder level: ", 0);
        if (addProduct(new Product(id, name, qty, price, reorder))) {
            ConsoleUI.success("Product added.");
        } else {
            ConsoleUI.error("A product with ID " + id + " already exists.");
        }
    }

    private void editProductFlow() {
        String id = ConsoleInput.readString("  Product ID to edit: ");
        Product p = ctx.getInventory().searchProduct(id);
        if (p == null) {
            ConsoleUI.error("Product not found.");
            return;
        }
        ConsoleUI.printBox("CURRENT DETAILS", p.getDetails());
        String name = ConsoleInput.readString("  New name: ");
        double price = ConsoleInput.readDouble("  New price: ", 0);
        int reorder = ConsoleInput.readInt("  New reorder level: ", 0);
        editProduct(id, name, price, reorder);
        ConsoleUI.success("Product updated.");
    }

    private void searchFlow() {
        String query = ConsoleInput.readString("  Search (ID or name): ");
        printProducts("SEARCH RESULTS", ctx.getInventory().search(query));
    }

    private void lowStockFlow() {
        List<Product> low = viewLowStockAlerts();
        if (low.isEmpty()) ConsoleUI.success("No low-stock products.");
        else printProducts("LOW-STOCK ALERTS", low);
    }

    private void takeOrderFlow() {
        String customer = ConsoleInput.readString("  Customer name: ");
        String productID = ConsoleInput.readString("  Product ID: ");
        int qty = ConsoleInput.readInt("  Quantity: ", 1);
        CustomerOrder order = submitOrder(customer, productID, qty);
        ConsoleUI.success("Order " + order.getOrderID() + " queued. Position in line: "
                + ctx.getOrderQueue().size());
    }

    private void processOrderFlow() {
        CustomerOrder order = processNextOrder();
        if (order == null) {
            ConsoleUI.info("No orders waiting.");
            return;
        }
        if (OrderStatus.REJECTED.equals(order.getStatus())) {
            ConsoleUI.error(order.getOrderID() + " rejected: product not found or not enough stock.");
            return;
        }
        ConsoleUI.success(order.getOrderID() + " completed for " + order.getCustomerName() + ".");
        Product p = ctx.getInventory().searchProduct(order.getProductID());
        if (p != null && p.isLowStock()) {
            ConsoleUI.warn("ALERT: " + p.getProductName() + " is low on stock (" + p.getQuantity() + " left).");
        }
    }

    private void restockFlow() {
        String productID = ConsoleInput.readString("  Product ID to restock: ");
        int qty = ConsoleInput.readInt("  Quantity to order: ", 1);

        ConsoleUI.printTop("SUPPLIERS");
        System.out.printf(ConsoleUI.V + " %-6s %-22s %-28s " + ConsoleUI.V + "%n", "ID", "Name", "Contact");
        ConsoleUI.printDivider();
        for (Supplier s : ctx.getSuppliers()) {
            System.out.printf(ConsoleUI.V + " %-6.6s %-22.22s %-28.28s " + ConsoleUI.V + "%n",
                    s.getSupplierID(), s.getSupplierName(), s.getContactInfo());
        }
        ConsoleUI.printBottom();

        String supplierID = ConsoleInput.readString("  Supplier ID: ");
        Order order = requestRestock(productID, qty, supplierID);
        ConsoleUI.success("Restock order " + order.getOrderID() + " sent for manager approval.");
    }
}