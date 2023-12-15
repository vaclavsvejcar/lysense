package com.norcane.lysense.source.support;


import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.HeaderStyle;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.source.metadata.HeaderCandidate;
import com.norcane.lysense.source.metadata.LicenseHeader;
import com.norcane.lysense.source.metadata.Metadata;
import com.norcane.lysense.test.TestHeaderConfig;
import com.norcane.toolkit.InstanceFactory;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;

import static com.norcane.lysense.test.Assertions.assertIsPresent;
import static com.norcane.lysense.test.Assertions.assertNotPresent;
import static org.apache.commons.lang3.Validate.notNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public abstract class SourceCodeSupportTestKit {

    @Inject
    ResourceLoader resourceLoader;

    @InjectMock
    Configuration configuration;

    protected abstract InstanceFactory<SourceCodeSupport> sourceCodeSupportFactory();

    protected abstract List<TestSample> samples();

    @TestFactory
    Stream<DynamicTest> loadHeadersFromSamples() {
        final InstanceFactory<SourceCodeSupport> factory = notNull(sourceCodeSupportFactory(), "Provided source code support factory cannot be null");
        final List<TestSample> samples = notNull(samples(), "Provided collection of test samples cannot be null");

        return samples.stream().map(sample -> testForSample(factory, sample));
    }

    private DynamicTest testForSample(InstanceFactory<SourceCodeSupport> factory, TestSample sample) {
        final Resource resource = resourceLoader.resource(sample.resource());

        return DynamicTest.dynamicTest(sample.resource(), () -> {
            // -- mocks
            when(configuration.headerConfigOrFail(anyString())).thenReturn(new TestHeaderConfig(sample.headerStyle));

            final Metadata metadata = factory.instance().load(resource).metadata();
            if (sample.header() != null) {
                assertIsPresent(sample.header(), metadata.header());
            } else {
                assertEquals(sample.candidate(), metadata.headerCandidate());
                assertNotPresent(metadata.header());
            }
        });
    }

    protected TestSample sample(HeaderStyle headerStyle, String resource, LicenseHeader header) {
        return new TestSample(headerStyle, resource, header, null);
    }

    protected TestSample sample(HeaderStyle headerStyle, String resource, HeaderCandidate candidate) {
        return new TestSample(headerStyle, resource, null, candidate);
    }

    protected record TestSample(HeaderStyle headerStyle, String resource, LicenseHeader header, HeaderCandidate candidate) {
    }
}
