package ru.sfedu.finalqualifyingwork.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.sfedu.finalqualifyingwork.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static final String hibernateConfigPath = "src/main/resources/hibernate.cfg.xml";

    private static final List<Class> classList = new ArrayList<>(Arrays.asList(new Class[]{
            User.class
    }));

    private static void registerClasses(MetadataSources metadataSources) {
        for (Class clazz : classList) {
            metadataSources.addAnnotatedClass(clazz);
        }
    }


    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
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
        return sessionFactory;
    }

}
