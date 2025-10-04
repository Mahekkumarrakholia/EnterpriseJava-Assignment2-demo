package nbcc.assignment2.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import nbcc.assignment2.entities.BugReport;

import java.util.List;

public class BugReportDAO implements BugReportRepo {

    private static final String PERSISTENCE_UNIT_NAME = "default";

    protected BugReportDAO() {
    }

    @Override
    public List<BugReport> getBugReportList() {
        try (EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
            try (EntityManager em = emFactory.createEntityManager()) {

                TypedQuery<BugReport> query = em.createQuery(
                        "SELECT b FROM BugReport b ORDER BY b.id", BugReport.class);
                return query.getResultList();
            }
        }
    }

    @Override
    public void addBugReport(BugReport bugReport) {
        try (EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
            try (EntityManager em = emFactory.createEntityManager()) {

                em.getTransaction().begin();
                em.persist(bugReport);
                em.getTransaction().commit();
            }
        }
    }

    @Override
    public void editBugReport(BugReport bugReport) {
        try (EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
            try (EntityManager em = emFactory.createEntityManager()) {

                em.getTransaction().begin();
                em.merge(bugReport);
                em.getTransaction().commit();
            }
        }
    }
}
