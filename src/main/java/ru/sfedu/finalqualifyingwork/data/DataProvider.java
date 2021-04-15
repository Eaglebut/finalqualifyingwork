package ru.sfedu.finalqualifyingwork.data;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import ru.sfedu.finalqualifyingwork.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DataProvider {

    public <T> List<T> getEntities(Class<T> tClass ) {
      try {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();
        List<T> entity = session.createQuery("from " + tClass.getName(), tClass).getResultList();
        session.getTransaction().commit();
        session.close();
        return entity;
      } catch (ConstraintViolationException e) {
        return new ArrayList<>();
      }
    }
/*
    protected <T> Statuses saveOrUpdateEntity(T entity) {
      try {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.saveOrUpdate(entity);
        session.getTransaction().commit();
        session.close();
        return Statuses.SUCCESSFUL;
      } catch (ConstraintViolationException e) {
        log.error(e);
        return Statuses.FAILED;
      }
    }

    protected <T> Statuses deleteEntity(Class<T> tClass, long id) {
      try {
        var optEntity = getEntityById(tClass, id);
        if (optEntity.isEmpty()) {
          return Statuses.FAILED;
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.delete(optEntity.get());
        session.getTransaction().commit();
        session.close();
        return Statuses.SUCCESSFUL;
      } catch (ConstraintViolationException e) {
        log.error(e);
        return Statuses.FAILED;
      }
    }
*/
}
