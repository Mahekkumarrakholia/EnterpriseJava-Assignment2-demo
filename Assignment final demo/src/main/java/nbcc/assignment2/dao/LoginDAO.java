package nbcc.assignment2.dao;

import jakarta.persistence.*;
import nbcc.assignment2.entities.UserInfo;

public class LoginDAO implements LoginRepo {

    private static final String PERSISTENCE_UNIT_NAME = "default";

    protected LoginDAO() {
    }

    @Override
    public void create(String username, String password) {
        create(new UserInfo(username, password));
    }

    @Override
    public void create(UserInfo userInfo) {
        try (EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
            try (EntityManager em = emFactory.createEntityManager()) {
                em.getTransaction().begin();
                em.persist(userInfo);
                em.getTransaction().commit();
            }
        }
    }

    @Override
    public UserInfo get(long id) {
        try (EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
            try (EntityManager em = emFactory.createEntityManager()) {
                return em.find(UserInfo.class, id);
            }
        }
    }

    @Override
    public UserInfo get(String username, String password) {
        try (EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
            try (EntityManager em = emFactory.createEntityManager()) {
                TypedQuery<UserInfo> query = em.createQuery(
                        "Select u from UserInfo u where u.username = :username and u.password = :password", UserInfo.class);
                query.setParameter("username", username);
                query.setParameter("password", password);

                return query.getSingleResult();
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean exists(String username) {
        try (EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
            try (EntityManager em = emFactory.createEntityManager()) {
                TypedQuery<Long> query = em.createQuery(
                        "Select Count(u) from UserInfo u where u.username = :username ", Long.class);

                query.setParameter("username", username);
                Long result = query.getSingleResult();

                return result != null && result > 0;
            }
        }
    }
}
