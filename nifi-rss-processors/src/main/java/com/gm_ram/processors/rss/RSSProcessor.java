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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm_ram.models.RSSItem;
import com.gm_ram.services.RSSFeedParser;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.apache.nifi.flowfile.attributes.FragmentAttributes.*;

@Tags({"rss", "parser", "source"})
@CapabilityDescription("Parses the XML of an RSS feed to extract the individual elements as their own flow files. The processor uses the ROME Java framework for RSS and Atom feeds.")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class RSSProcessor extends AbstractProcessor {

    public static final PropertyDescriptor OUTPUT_FORMAT = new PropertyDescriptor
            .Builder().name("OUTPUT_FORMAT")
            .displayName("Output Format")
            .description("Format for the parsed RSS feed")
            .required(true)
            .allowableValues("json")
            .defaultValue("json")
            .build();

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("File routed to success when RSS Feed XML parsed successfully")
            .build();

    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("File routed to failure when RSS Feed XML is not parsed")
            .build();

    public static final Relationship REL_ITEM = new Relationship.Builder()
            .name("item")
            .description("Extracted RSS XML elements are routed to item as one flow file per element")
            .build();

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        descriptors.add(OUTPUT_FORMAT);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<Relationship>();
        relationships.add(REL_SUCCESS);
        relationships.add(REL_FAILURE);
        relationships.add(REL_ITEM);
        this.relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext processContext) {

    }

    @Override
    public void onTrigger(final ProcessContext processContext, final ProcessSession processSession) throws ProcessException {
        FlowFile flowFile = processSession.get();
        if (flowFile == null) {
            return;
        }

        final ComponentLog logger = getLogger();

        SyndFeed syndFeed;
        List<RSSItem> items; // TODO: final?

        try (InputStream inputStream = processSession.read(flowFile)) {
            SyndFeedInput syndFeedInput = new SyndFeedInput();
            syndFeed = syndFeedInput.build(new XmlReader(inputStream));
            RSSFeedParser rssFeedParser = new RSSFeedParser();
            items = rssFeedParser.extractItems(syndFeed);
            logger.info("RSS Items extracted from XML in FlowFile {}", new Object[]{flowFile});
        } catch (FeedException | IOException e) {
            // TODO: ProvenanceReporter?
            processSession.transfer(flowFile, REL_FAILURE);
            logger.error("XML could not be parsed for FlowFile {}", new Object[]{flowFile});
            return;
        }

        Map<String, String> attributes = new HashMap<>();
        final String fragmentId = UUID.randomUUID().toString();
        attributes.put(FRAGMENT_ID.key(), fragmentId);
        attributes.put(FRAGMENT_COUNT.key(), Integer.toString(items.size()));

        int index = 0;

        for (RSSItem item : items) {
            FlowFile itemFlowFile = processSession.create(flowFile);
            processSession.getProvenanceReporter().create(itemFlowFile);
            ObjectMapper objectMapper = new ObjectMapper();
            processSession.write(itemFlowFile, outputStream -> objectMapper.writeValue(outputStream, item));
            attributes.put(SEGMENT_ORIGINAL_FILENAME.key(), itemFlowFile.getAttribute(CoreAttributes.FILENAME.key()));
            attributes.put(FRAGMENT_INDEX.key(), Integer.toString(index));
            // TODO: ProvenanceReporter?
            processSession.transfer(processSession.putAllAttributes(itemFlowFile, attributes), REL_ITEM);
            index++;
        }

        flowFile = copyAttributesToOriginal(processSession, flowFile, fragmentId, items.size());
        // TODO: ProvenanceReporter?
        processSession.transfer(flowFile, REL_SUCCESS);
        logger.info("Split {} into {} FlowFiles", new Object[]{flowFile, items.size()});
    }
}
