package nbcc.assignment2.entities;

import jakarta.persistence.*;

import java.util.List;

//Todo implement this as a proper entity
@Entity
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;

    @OneToMany(mappedBy = "userInfo", fetch = FetchType.LAZY)
    private List<BugReport> bugReports;

    public UserInfo() {
    }

    public UserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<BugReport> getBugReports() {
        return bugReports;
    }

    public void setBugReports(List<BugReport> bugReports) {
        this.bugReports = bugReports;
    }
}
