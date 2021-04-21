package ru.sfedu.finalqualifyingwork.util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HibernateDataUtil {

    public static <T> Optional<T> executeQuerySingle(Class<T> tClass, String queryString, Object... args) {
      try {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();
        var query = session.createQuery(queryString, tClass);
        for (int i = 0; i < args.length; i++) {
          query.setParameter(i + 1, args[i]);
        }
        var entity = query.getSingleResult();
        session.flush();
        return Optional.ofNullable(entity);
      } catch (ConstraintViolationException e) {
        return Optional.empty();
      }
    }

  public static <T> List<T> executeQueryList(Class<T> tClass, String queryString, Object... args) {
    try {
      Session session = HibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      var query = session.createQuery(queryString, tClass);
      for (int i = 0; i < args.length; i++) {
        query.setParameter(i + 1, args[i]);
      }
      var resultList = query.getResultList();
      session.flush();
      return resultList;
    } catch (ConstraintViolationException e) {
      return new ArrayList<>();
    }
  }


  public static <T> Optional<T> getEntityById(Class<T> tClass, long id) {
    try {
      Session session = HibernateUtil.getSessionFactory().openSession();
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

  public static <T> Statuses createEntity(T entity) {
    try {
      Session session = HibernateUtil.getSessionFactory().openSession();
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

  public static <T> Statuses updateEntity(T entity) {
    try {
      Session session = HibernateUtil.getSessionFactory().openSession();
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

  public static <T> Statuses deleteEntity(Class<T> tClass, long id) {
    try {
      var optEntity = getEntityById(tClass, id);
      if (optEntity.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      Session session = HibernateUtil.getSessionFactory().openSession();
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
