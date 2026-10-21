import java.util.ArrayList;
import java.util.List;

/** Holds the shared objects every user role works with. Created once in Main and passed to each User. */
public class StoreContext {
    private Inventory inventory = new Inventory();
    private OrderQueue orderQueue;
    private UserManager userManager = new UserManager();
    private List<Supplier> suppliers = new ArrayList<>();
    private List<Order> restockOrders = new ArrayList<>();
    private List<CustomerOrder> processedOrders = new ArrayList<>();

    public StoreContext(int queueCapacity) {
        this.orderQueue = new OrderQueue(queueCapacity);
    }

    public Inventory getInventory()                  { return inventory; }
    public OrderQueue getOrderQueue()                { return orderQueue; }
    public UserManager getUserManager()              { return userManager; }
    public List<Supplier> getSuppliers()             { return suppliers; }
    public List<Order> getRestockOrders()            { return restockOrders; }
    public List<CustomerOrder> getProcessedOrders()  { return processedOrders; }

    public Supplier findSupplier(String supplierID) {
        for (Supplier s : suppliers) {
            if (s.getSupplierID().equalsIgnoreCase(supplierID.trim())) return s;
        }
        return null;
    }

    public Order findRestockOrder(String orderID) {
        for (Order o : restockOrders) {
            if (o.getOrderID().equalsIgnoreCase(orderID.trim())) return o;
        }
        return null;
    }

    public List<Order> restockOrdersWithStatus(String status) {
        List<Order> result = new ArrayList<>();
        for (Order o : restockOrders) {
            if (o.getStatus().equals(status)) result.add(o);
        }
        return result;
    }
}