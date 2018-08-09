/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package domainapp.modules.simple.dom.impl;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;

import domainapp.modules.simple.SimpleModule;
import domainapp.modules.simple.dom.WithWorkFlow;
import domainapp.modules.simple.dom.WorkFlowState;
import domainapp.modules.simple.dom.impl.simpleworkflow.SimpleState;
import domainapp.modules.simple.dom.impl.simpleworkflow.SimpleWorkFlowStateMachine;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE, schema = "simple")
@javax.jdo.annotations.DatastoreIdentity(strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column="id")
@javax.jdo.annotations.Version(strategy= VersionStrategy.DATE_TIME, column="version")
@javax.jdo.annotations.Unique(name="SimpleObject_name_UNQ", members = {"name"})
@DomainObject(auditing = Auditing.ENABLED)
@DomainObjectLayout()  // causes UI events to be triggered
public class SimpleObject implements Comparable<SimpleObject>, WithWorkFlow {

    public SimpleObject(final String name){
        this.name = name;
    }

    @Column(allowsNull = "false", length = 40)
    @Getter @Setter
    @Title(prepend = "Object: ")
    private String name;

    @Column(allowsNull = "true", length = 4000)
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private String notes;

    @Column(allowsNull = "true")
    @Getter @Setter
    private SimpleState state;

    @Override
    @Programmatic
    public WorkFlowState getWorkFlowState(){
        return getState();
    }

    @Override
    @Programmatic
    public void setWorkFlowState(WorkFlowState state){
        this.setState((SimpleState) state);
    }

    public static class NameUpdated extends SimpleModule.ActionDomainEvent<SimpleObject> { }

    @Action(semantics = SemanticsOf.IDEMPOTENT,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED,
            associateWith = "name",
            domainEvent = NameUpdated.class)
    public SimpleObject updateName(
            @Parameter(maxLength = 40)
            @ParameterLayout(named = "Name")
            final String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }

    public TranslatableString validate0UpdateName(final String name) {
        return name != null && name.contains("!") ? TranslatableString.tr("Exclamation mark is not allowed") : null;
    }

    public static class DeleteEvent extends SimpleModule.ActionDomainEvent<SimpleObject> { }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, domainEvent = DeleteEvent.class)
    public void delete() {
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deleted", title));
        repositoryService.remove(this);
    }

    //region > toString, compareTo
    @Override
    public String toString() {
        return getName();
    }

    public int compareTo(final SimpleObject other) {
        return ComparisonChain.start()
                .compare(this.getName(), other.getName())
                .result();
    }
    //endregion


    //region > injected services
    @Inject
    RepositoryService repositoryService;

    @Inject
    TitleService titleService;

    @Inject
    MessageService messageService;

    @Inject SimpleWorkFlowStateMachine simpleStateMachine;

    //endregion

}