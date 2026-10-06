public class User {
    private int id;
    private int roleId;
    private String fullName;
    private String login;

    public User(int id, int roleId, String fullName, String login) {
        this.id = id;
        this.roleId = roleId;
        this.fullName = fullName;
        this.login = login;
    }

    public int getId() { return id; }
    public int getRoleId() { return roleId; }
    public String getFullName() { return fullName; }
    public String getLogin() { return login; }

    public String getRoleName() {
        switch (roleId) {
            case 1: return "Гость";
            case 2: return "Клиент";
            case 3: return "Менеджер";
            case 4: return "Администратор";
            default: return "Неизвестно";
        }
    }

    public boolean canFilter() { return roleId >= 3; }
    public boolean canEdit() { return roleId == 4; }
    public boolean canViewOrders() { return roleId >= 3; }
}