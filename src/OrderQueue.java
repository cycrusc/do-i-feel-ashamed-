/**
 * Array-based (linear) queue of customer orders, following the lesson:
 *  - front and rear start at -1
 *  - enqueue adds at rear, dequeue removes from front
 *  - overflow when rear == length - 1, underflow when front == -1 || front > rear
 *
 * Differences from the slides: methods return values / throw instead of printing,
 * and front/rear reset to -1 once the queue becomes empty.
 *
 * Known limitation of a linear queue: slots freed at the front are not reused until the
 * queue empties, so isFull() can be true while fewer than "capacity" orders are waiting.
 * A circular queue fixes this (see the notes in the build order).
 */
public class OrderQueue {
    private CustomerOrder[] queue;
    private int front = -1;
    private int rear = -1;

    public OrderQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive.");
        queue = new CustomerOrder[capacity];
    }

    public boolean isEmpty() {
        return front == -1 || front > rear;
    }

    public boolean isFull() {
        return rear == queue.length - 1;
    }

    public int size() {
        if (isEmpty()) return 0;
        return rear - front + 1;
    }

    public void enqueue(CustomerOrder order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null.");
        if (isFull()) throw new IllegalStateException("Order queue is full.");
        if (front == -1) front = 0;     // first element ever added (or first after a reset)
        rear++;
        queue[rear] = order;
    }

    public CustomerOrder dequeue() {
        if (isEmpty()) throw new IllegalStateException("Order queue is empty.");
        CustomerOrder removed = queue[front];
        queue[front] = null;            // release the slot
        front++;
        if (front > rear) {             // queue just became empty: reset to the initial state
            front = -1;
            rear = -1;
        }
        return removed;
    }

    public CustomerOrder peek() {
        if (isEmpty()) throw new IllegalStateException("Order queue is empty.");
        return queue[front];
    }

    /** Copy of the waiting orders from front to rear. Nothing is removed. */
    public CustomerOrder[] getPendingOrders() {
        CustomerOrder[] copy = new CustomerOrder[size()];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = queue[front + i];
        }
        return copy;
    }
}