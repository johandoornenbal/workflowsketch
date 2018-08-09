package domainapp.modules.simple.dom.impl.simpleworkflow;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import domainapp.modules.simple.dom.impl.SimpleObject;

@Mixin
public class SimpleObject_tryToTransition {

    private final SimpleObject simpleObject;

    public SimpleObject_tryToTransition(SimpleObject simpleObject) {
        this.simpleObject = simpleObject;
    }

    @Action()
    public SimpleObject $$() {
        simpleWorkFlowService.transition(simpleObject);
        return simpleObject;
    }

    @Inject
    SimpleWorkFlowStateMachine simpleWorkFlowService;

}
