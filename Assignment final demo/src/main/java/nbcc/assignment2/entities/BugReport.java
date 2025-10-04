package nbcc.assignment2.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

//Todo implement this as a proper entity
@Entity
public class BugReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String summary;

    private String description;

    private BigDecimal costToFix;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    public BugReport() {
    }

    public BugReport(String summary, String description, UserInfo userInfo) {
        this.summary = summary;
        this.description = description;
        this.userInfo = userInfo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public BigDecimal getCostToFix() {
        return costToFix;
    }

    public void setCostToFix(BigDecimal costToFix) {
        this.costToFix = costToFix;
    }
}
