package domainapp.modules.simple.dom.impl.simpleworkflow;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.modules.simple.dom.impl.SimpleObject;

@Mixin
public class SimpleObject_tasks {

    private final SimpleObject simpleObject;

    public SimpleObject_tasks(SimpleObject simpleObject) {
        this.simpleObject = simpleObject;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<SimpleTask> $$() {
        return simpleTaskRepository.findByObject(simpleObject);
    }

    @Inject
    SimpleTaskRepository simpleTaskRepository;

}
