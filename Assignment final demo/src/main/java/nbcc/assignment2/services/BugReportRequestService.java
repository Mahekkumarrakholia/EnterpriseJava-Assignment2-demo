package nbcc.assignment2.services;

import jakarta.servlet.http.HttpServletRequest;
import nbcc.assignment2.entities.BugReport;

public class BugReportRequestService extends RequestAttributeService{

    public BugReportRequestService(HttpServletRequest request) {
        super(request);
    }

    public BugReport getBugReport(){

        BugReport bugReport = new BugReport();

        bugReport.setId(getId());

        bugReport.setSummary(getString("summary"));
        bugReport.setDescription(getString("description"));
        bugReport.setCostToFix(getBigDecimal("costToFix", null));

        return bugReport;
    }

    public Long getId() {
        return getLong("id");
    }
}
