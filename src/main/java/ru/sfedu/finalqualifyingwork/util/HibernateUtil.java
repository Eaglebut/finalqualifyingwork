package ru.sfedu.finalqualifyingwork.util;

import lombok.Data;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Data
public class HibernateUtil {
  private static final String hibernateConfigPath = "src/main/resources/hibernate.cfg.xml";
  private final List<Class> classList = new ArrayList<>(Arrays.asList(new Class[]{
          User.class,
          Group.class,
          TaskGroup.class,
          Task.class,
          Comment.class
  }));
  private SessionFactory sessionFactory;

  public HibernateUtil() {
    File configFile = new File(hibernateConfigPath);
    Configuration configuration = new Configuration().configure(configFile);
    ServiceRegistry serviceRegistry
            = new StandardServiceRegistryBuilder()
            .applySettings(configuration.getProperties()).build();
    MetadataSources metadataSources =
            new MetadataSources(serviceRegistry);
    registerClasses(metadataSources);
    sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
  }

  private void registerClasses(MetadataSources metadataSources) {
    for (Class clazz : classList) {
      metadataSources.addAnnotatedClass(clazz);
    }
  }

}
