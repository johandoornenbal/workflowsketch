package domainapp.modules.simple.dom.impl.simpleworkflow;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.modules.simple.dom.impl.SimpleObject;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = SimpleTask.class
)
public class SimpleTaskRepository {

    @Programmatic
    public java.util.List<SimpleTask> listAll() {
        return repositoryService.allInstances(SimpleTask.class);
    }

    @Programmatic
    public List<SimpleTask> findByObject(
            final SimpleObject simpleObject
    ) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        SimpleTask.class,
                        "findByAttached",
                        "simpleObject", simpleObject));
    }


    @Programmatic
    public SimpleTask create(final SimpleObject object) {
        final SimpleTask simpleTask = new SimpleTask();
        serviceRegistry2.injectServicesInto(simpleTask);
        simpleTask.setSimpleObject(object);
        repositoryService.persist(simpleTask);
        return simpleTask;
    }

    @Programmatic
    public SimpleTask findOrCreate(
            final SimpleObject object
    ) {
        List<SimpleTask> simpleTasks = findByObject(object);
        SimpleTask simpleTask;
        if (simpleTasks.isEmpty()) {
            simpleTask = create(object);
        } else {
            simpleTask = simpleTasks.get(0);
        }
        return simpleTask;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry2;
}
