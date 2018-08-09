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

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import domainapp.modules.simple.dom.simpleinvoice.Role;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;
import domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceTaskRepository;
import domainapp.modules.simple.fixture.SimpleInvoice_persona;
import domainapp.modules.simple.integtests.SimpleModuleIntegTestAbstract;

public class SimpleInvoiceTaskRepo_IntegTest extends SimpleModuleIntegTestAbstract {

    SimpleInvoice simpleInvoice;

    @Before
    public void setUp() {
        // given
        simpleInvoice = fixtureScripts.runBuilderScript(SimpleInvoice_persona.I123.builder());
    }

    public static class FindByInvoice extends SimpleInvoiceTaskRepo_IntegTest {

        @Test
        public void findByInvoice_works(){

            // given
            Assertions.assertThat(simpleInvoiceTaskRepository.listAll()).isEmpty();

            // when
            simpleInvoiceTaskRepository.create(simpleInvoice, Role.ADMINISTRATOR);

            // then
            Assertions.assertThat(simpleInvoiceTaskRepository.listAll()).isNotEmpty();
            Assertions.assertThat(simpleInvoiceTaskRepository.findByInvoice(simpleInvoice).size()).isEqualTo(1);

        }

        @Inject
        SimpleInvoiceTaskRepository simpleInvoiceTaskRepository;

    }



}