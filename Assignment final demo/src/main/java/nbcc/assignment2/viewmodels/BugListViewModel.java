package nbcc.assignment2.viewmodels;

import nbcc.assignment2.entities.BugReport;
import nbcc.assignment2.entities.UserInfo;


//Todo add more properties to the view model as needed

import java.math.BigDecimal;
import java.util.List;

public class BugListViewModel {

    private List<BugReport> bugReports;

    private UserInfo userInfo;

    public BugListViewModel() {
    }

    public BugListViewModel(List<BugReport> bugReports) {
        this.bugReports = bugReports;
    }

    public BugListViewModel(List<BugReport> bugReports, UserInfo userInfo) {
        this.bugReports = bugReports;
        this.userInfo = userInfo;
    }

    public List<BugReport> getBugReports() {
        return bugReports;
    }

    public void setBugReports(List<BugReport> bugReports) {
        this.bugReports = bugReports;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Boolean isLoggedIn() {
        // If userInfo is not null
        return userInfo != null;
    }

    public Boolean showCreateLink() {
        // if user is loggedIn Then show create link
        if (userInfo != null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean showEditLink() {
        // if user is loggedIn Then show edit link
        if (userInfo != null) {
            return true;
        }  else {
            return false;
        }
    }

    // Calculate the totalCost
    public BigDecimal getTotalCost(){
        BigDecimal totalCost = BigDecimal.ZERO;

        // Check bugReport List
        if (bugReports != null) {
            for (BugReport bugReport : bugReports) {
                if(bugReport.getCostToFix() != null){
                    totalCost = totalCost.add(bugReport.getCostToFix());
                }
            }
        }
       return totalCost;
    }

    // Bonus Question
    public boolean userEditBugReport(BugReport bugReport){
        if (userInfo == null) {
            return false;
        }
        if (bugReport == null) {
            return false;
        }
        if (bugReport.getUserInfo() == null) {
            return false;
        }
        if (userInfo.getId() == bugReport.getUserInfo().getId()) {
            return true;
        } else  {
            return false;
        }
    }
}
