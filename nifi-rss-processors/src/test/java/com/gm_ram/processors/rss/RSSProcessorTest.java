/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gm_ram.processors.rss;

import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class RSSProcessorTest {

    private TestRunner testRunner;

    @Before
    public void init() {
        testRunner = TestRunners.newTestRunner(RSSProcessor.class);
        testRunner.setProperty(RSSProcessor.OUTPUT_FORMAT, "json");
    }

    /**
     * Parse valid XML
     *
     * @throws IOException Unexpected error
     */
    @Test
    public void testWithValidXML() throws IOException {
        testRunner.enqueue(Paths.get("src/test/resources/xml-snippet-valid.xml"));
        testRunner.clearTransferState();
        testRunner.run();

        testRunner.assertTransferCount(RSSProcessor.REL_FAILURE, 0);
        testRunner.assertTransferCount(RSSProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(RSSProcessor.REL_ITEM, 2);
        testRunner.assertQueueEmpty();
    }

    /**
     * Parse invalid XML
     *
     * @throws IOException Unexpected error
     */
    @Test
    public void testWithInvalidXML() throws IOException {
        testRunner.enqueue(Paths.get("src/test/resources/xml-snippet-invalid.xml"));
        testRunner.clearTransferState();
        testRunner.run();

        testRunner.assertTransferCount(RSSProcessor.REL_FAILURE, 1);
        testRunner.assertTransferCount(RSSProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(RSSProcessor.REL_ITEM, 0);
        testRunner.assertQueueEmpty();
    }

}
