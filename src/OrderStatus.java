/** Status values for orders, kept as String constants. Always compare with .equals(), never ==. */
public class OrderStatus {
    public static final String CREATED   = "CREATED";    // restock order built but not yet placed
    public static final String PENDING   = "PENDING";    // waiting (restock: for approval; customer order: in the queue)
    public static final String APPROVED  = "APPROVED";   // restock order approved by the manager
    public static final String COMPLETED = "COMPLETED";  // customer order filled / restock stock received
    public static final String REJECTED  = "REJECTED";   // customer order could not be filled / restock rejected
}