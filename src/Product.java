public class Product {
    private int id;
    private String name;
    private String description;
    private String categoryName;
    private String manufacturerName;
    private String supplierName;
    private String unitName;
    private double price;
    private double discount;
    private int quantity;
    private String imagePath;

    public Product(int id, String name, String description, String categoryName,
                   String manufacturerName, String supplierName, String unitName,
                   double price, double discount, int quantity, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryName = categoryName;
        this.manufacturerName = manufacturerName;
        this.supplierName = supplierName;
        this.unitName = unitName;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategoryName() { return categoryName; }
    public String getManufacturerName() { return manufacturerName; }
    public String getSupplierName() { return supplierName; }
    public String getUnitName() { return unitName; }
    public double getPrice() { return price; }
    public double getDiscount() { return discount; }
    public int getQuantity() { return quantity; }
    public String getImagePath() { return imagePath; }

    public double getFinalPrice() {
        return price * (1 - discount / 100.0);
    }

    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    public boolean isBigDiscount() {
        return discount > 25;
    }
}