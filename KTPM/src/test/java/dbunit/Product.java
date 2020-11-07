package dbunit;
public class Product {
    int id = 0;
    String name = "";
    float price = 0;
    int amount = 0;
    String description = "";

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Product(int id, String name, float price, int amount, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
    }
}
