public class Employee extends User{
    public Employee(String userId, String name, Inventory inventory){
        super(userId, name, inventory);
    }
    public UseRequest requestUse(LibraryItem item, UsePurpose purpose, User customer){
        if (purpose == UsePurpose.RENT_TO_CUSTOMER && customer == null){
            throw new IllegalArgumentException("Rental needs a customer");
        }
        UseRequest request = new UseRequest(item, purpose, this, customer);
        submitRequest(request);
        return request;
    }
    public void submitRequest(UseRequest r){
        inventory.requests.add(r);
    }
}
