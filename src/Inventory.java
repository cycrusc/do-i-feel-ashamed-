import java.util.ArrayList;
import java.util.List;
public class Inventory implements Searchable{
    private List<Product> productList = new ArrayList<>();

    public List<Object> search(String query) {
        List<Object> results = new ArrayList<>();
        return results;
    }
    public void addProduct(Product product) {
        if (product != null){
            productList.add(product);
        }
    }
    public void editProduct(String productID) {}
    public void deleteProduct(String productID) {}
    public Product searchProduct(String productID) {return null;}

    public List<Product> checkLowStock() {
        List<Product> low = new ArrayList<>();
        for (Product p : productList) {
            if (p.isLowStock()) low.add(p);
        }
        return low;
    }
}
