public class User {
    private int id;
    private int roleId;
    private String fullName;
    private String login;
    private String passwordHash;

    public User(int id, int roleId, String fullName, String login, String passwordHash) {
        this.id = id;
        this.roleId = roleId;
        this.fullName = fullName;
        this.login = login;
        this.passwordHash = passwordHash;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRoleName() {
        switch (roleId) {
            case 1: return "Гость";
            case 2: return "Клиент";
            case 3: return "Менеджер";
            case 4: return "Администратор";
            default: return "Неизвестно";
        }
    }
}