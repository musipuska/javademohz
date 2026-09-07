public class Product {
    private int id;
    private int categoryId;
    private String categoryName;
    private int manufacturerId;
    private String manufacturerName;
    private int supplierId;
    private String supplierName;
    private int unitId;
    private String unitName;
    private String name;
    private String description;
    private double price;
    private double discount;
    private int quantity;
    private String imagePath;

    // Конструктор для отображения на экране
    public Product(int id, String name, String description, String categoryName,
                   String manufacturerName, String supplierName, String unitName,
                   double price, double discount, int quantity) {
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
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getFinalPrice() {
        return price * (1 - discount / 100);
    }
}