package nbcc.assignment2.dao;

import nbcc.assignment2.entities.BugReport;

import java.util.List;

public interface BugReportRepo {

    List<BugReport> getBugReportList();

    void addBugReport(BugReport bugReport);

    void editBugReport(BugReport bugReport);
}
