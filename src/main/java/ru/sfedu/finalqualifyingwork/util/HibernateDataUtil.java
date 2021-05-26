package ru.sfedu.finalqualifyingwork.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class HibernateDataUtil {

  private final HibernateUtil hibernateUtil;

  private synchronized <T> Query<T> createQueryWithArgs(Session session, Class<T> tClass, String queryString, Object[] args) {
    Query<T> query = session.createQuery(queryString, tClass);
    for (int i = 0; i < args.length; i++) {
      query.setParameter(i + 1, args[i]);
    }
    return query;
  }

  public synchronized <T> Optional<T> executeQuerySingle(Class<T> tClass, String queryString, Object... args) {
    try {
      Session session = hibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      var entity = createQueryWithArgs(session, tClass, queryString, args).getSingleResult();
      session.flush();
      session.close();
      return Optional.ofNullable(entity);
    } catch (ConstraintViolationException e) {
      return Optional.empty();
    }
  }


  public synchronized <T> List<T> executeQueryList(Class<T> tClass, String queryString, Object... args) {
    try {
      Session session = hibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      var resultList = createQueryWithArgs(session, tClass, queryString, args).getResultList();
      session.flush();
      session.close();
      return resultList;
    } catch (ConstraintViolationException e) {
      return new ArrayList<>();
    }
  }


  public synchronized <T extends BaseEntity> Optional<T> getEntityById(Class<T> tClass, long id) {
    try {
      Session session = hibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      T entity = session.get(tClass, id);
      session.getTransaction().commit();
      session.close();
      return entity != null
              ? Optional.of(entity)
              : Optional.empty();
    } catch (ConstraintViolationException e) {
      log.error(e.toString());
      return Optional.empty();
    }
  }

  public synchronized <T extends BaseEntity> Statuses createEntity(T entity) {
    try {
      Session session = hibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      session.save(entity);
      session.getTransaction().commit();
      session.close();
      return Statuses.SUCCESS;
    } catch (ConstraintViolationException e) {
      log.error(e.toString());
      return Statuses.FAILED;
    }
  }

  public synchronized <T extends BaseEntity> Statuses updateEntity(T entity) {
    try {
      Session session = hibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      session.update(entity);
      session.getTransaction().commit();
      session.close();
      return Statuses.SUCCESS;
    } catch (ConstraintViolationException e) {
      log.error(e.toString());
      return Statuses.FAILED;
    }
  }

  public synchronized <T extends BaseEntity> Statuses deleteEntity(Class<T> tClass, long id) {
    try {
      var optEntity = getEntityById(tClass, id);
      if (optEntity.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      Session session = hibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      session.delete(optEntity.get());
      session.getTransaction().commit();
      session.close();
      return Statuses.SUCCESS;
    } catch (ConstraintViolationException e) {
      log.error(e.toString());
      return Statuses.FAILED;
    }
  }

}
