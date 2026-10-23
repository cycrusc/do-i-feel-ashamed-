import java.util.ArrayList;
import java.util.List;

public class Inventory implements Searchable {
    private List<Product> productList = new ArrayList<>();

    /** Returns false if the product is null or its ID already exists. */
    public boolean addProduct(Product p) {
        if (p == null || searchProduct(p.getProductID()) != null) return false;
        productList.add(p);
        return true;
    }

    /** Updates name, price and reorder level. Stock changes go through addStock/reduceStock. */
    public boolean editProduct(String productID, String newName, double newPrice, int newReorderLevel) {
        Product p = searchProduct(productID);
        if (p == null) return false;
        p.setProductName(newName);
        p.setPrice(newPrice);
        p.setReorderLevel(newReorderLevel);
        return true;
    }

    public boolean deleteProduct(String productID) {
        Product p = searchProduct(productID);
        return p != null && productList.remove(p);
    }

    /** Exact ID lookup (case-insensitive). Returns null when not found. */
    public Product searchProduct(String productID) {
        if (productID == null) return null;
        String id = productID.trim();
        for (Product p : productList) {
            if (p.getProductID().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    /** Partial, case-insensitive match on product ID or name. */
    @Override
    public List<Product> search(String query) {
        List<Product> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;
        String q = query.trim().toLowerCase();
        for (Product p : productList) {
            if (p.getProductID().toLowerCase().contains(q)
                    || p.getProductName().toLowerCase().contains(q)) {
                results.add(p);
            }
        }
        return results;
    }

    public List<Product> checkLowStock() {
        List<Product> low = new ArrayList<>();
        for (Product p : productList) {
            if (p.isLowStock()) low.add(p);
        }
        return low;
    }

    /** A copy, so callers cannot modify the internal list directly. */
    public List<Product> getAllProducts() {
        return new ArrayList<>(productList);
    }
}