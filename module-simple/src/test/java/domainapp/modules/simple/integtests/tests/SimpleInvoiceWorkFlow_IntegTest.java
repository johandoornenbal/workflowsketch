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
package domainapp.modules.simple.integtests.tests;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.wrapper.DisabledException;

import domainapp.modules.simple.dom.simpleinvoice.Person;
import domainapp.modules.simple.dom.simpleinvoice.Role;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoiceType;
import domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceState;
import domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceTask;
import domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceTaskRepository;
import domainapp.modules.simple.fixture.SimpleInvoice_persona;
import domainapp.modules.simple.integtests.SimpleModuleIntegTestAbstract;

public class SimpleInvoiceWorkFlow_IntegTest extends SimpleModuleIntegTestAbstract {

    SimpleInvoice simpleInvoice;

    @Before
    public void setUp() {
        // given
        simpleInvoice = fixtureScripts.runBuilderScript(SimpleInvoice_persona.I123.builder());
    }

    public static class SimpleInvoiceWorkFLow extends SimpleInvoiceWorkFlow_IntegTest {

        @Test
        public void initialisation_works(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.ADMINISTRATOR).size()).isEqualTo(1);

            // and expect
            expectedExceptions.expect(DisabledException.class);
            expectedExceptions.expectMessage("Reason: Amount and type are required to complete.");

            // when
            wrap(simpleInvoice).complete(Person.AMBROISE);

        }

        @Test
        public void complete_works(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.COMPLETED);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.MANAGER).size()).isEqualTo(1);
            SimpleInvoiceTask task = simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.MANAGER).get(0);
            Assertions.assertThat(task.assignedTo).isEqualTo(Person.AMBROISE);

        }

        @Test
        public void first_approval_works_with_large_amount(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).approveAsManager();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.APPROVED_BY_MANAGER);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.COUNTRY_LEADER).size()).isEqualTo(0);

        }

        @Test
        public void first_approval_works_with_small_amount(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("9999.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).approveAsManager();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAYABLE);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.TREASURER).size()).isEqualTo(0);

        }

        @Test
        public void second_approval_works(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).approveAsManager();
            wrap(simpleInvoice).approveAsCountryLeader();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAYABLE);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.TREASURER).size()).isEqualTo(0);

        }

        @Test
        public void pay_works(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).approveAsManager();
            wrap(simpleInvoice).approveAsCountryLeader();
            wrap(simpleInvoice).pay();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAID);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoice(simpleInvoice).size()).isEqualTo(0);

        }

        @Test
        public void reject_works_when_state_completed(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).reject();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.ADMINISTRATOR).size()).isEqualTo(1);
            SimpleInvoiceTask task = simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.ADMINISTRATOR).get(0);
            Assertions.assertThat(task.getAssignedTo()).isNull();

        }

        @Test
        public void reject_works_when_state_approved_by_manager(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).approveAsManager();
            wrap(simpleInvoice).reject();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.ADMINISTRATOR).size()).isEqualTo(1);

        }

        @Test
        public void reject_works_when_state_payable(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).approveAsManager();
            wrap(simpleInvoice).approveAsCountryLeader();
            wrap(simpleInvoice).reject();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.ADMINISTRATOR).size()).isEqualTo(1);

        }

        @Test
        public void reinitialisation_works_when_rejected(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).approveAsManager();
            wrap(simpleInvoice).approveAsCountryLeader();
            wrap(simpleInvoice).reject();
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);

            wrap(simpleInvoice).deleteState();
            wrap(simpleInvoice).initializeState();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoiceAndRole(simpleInvoice, Role.ADMINISTRATOR).size()).isEqualTo(1);

        }

        @Test
        public void reinitialisation_works_when_paid(){

            // given
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isNull();

            // when
            wrap(simpleInvoice).initializeState();
            simpleInvoice.setAmount(new BigDecimal("10000.00"));
            simpleInvoice.setInvoiceType(SimpleInvoiceType.CAPEX);
            wrap(simpleInvoice).complete(Person.AMBROISE);
            wrap(simpleInvoice).approveAsManager();
            wrap(simpleInvoice).approveAsCountryLeader();
            wrap(simpleInvoice).pay();
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAID);

            wrap(simpleInvoice).deleteState();
            wrap(simpleInvoice).initializeState();

            // then
            Assertions.assertThat(simpleInvoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAID);
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoice(simpleInvoice).size()).isEqualTo(0);

        }

        @Inject
        SimpleInvoiceTaskRepository simpleInvoiceTaskRepository;

    }



}