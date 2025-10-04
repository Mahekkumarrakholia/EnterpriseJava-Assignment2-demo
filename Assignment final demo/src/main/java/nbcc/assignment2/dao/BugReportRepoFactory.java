package nbcc.assignment2.dao;

public class BugReportRepoFactory {
    public static BugReportRepo getRepo(){
        return new BugReportDAO();
    }
}
