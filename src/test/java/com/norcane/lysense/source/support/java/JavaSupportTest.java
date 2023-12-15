package com.norcane.lysense.source.support.java;


import com.norcane.lysense.configuration.api.HeaderStyle;
import com.norcane.lysense.source.metadata.HeaderCandidate;
import com.norcane.lysense.source.metadata.LicenseHeader;
import com.norcane.lysense.source.support.SourceCodeSupport;
import com.norcane.lysense.source.support.SourceCodeSupportTestKit;
import com.norcane.toolkit.InstanceFactory;

import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class JavaSupportTest extends SourceCodeSupportTestKit {

    @Inject
    JavaSupportFactory javaSupportFactory;

    @Override
    protected InstanceFactory<SourceCodeSupport> sourceCodeSupportFactory() {
        return javaSupportFactory;
    }

    @Override
    protected List<TestSample> samples() {
        return List.of(
            sample(HeaderStyle.BLOCK_COMMENT, "classpath:/sources/java/sample-block-multi.java.txt",
                   new LicenseHeader(2, 4, 1, 2, List.of("/*", " * This is header", " */"))
            ),
            sample(HeaderStyle.BLOCK_COMMENT, "classpath:/sources/java/sample-block-multi-putBefore.java.txt",
                   new HeaderCandidate(0, 1)
            ),
            sample(HeaderStyle.LINE_COMMENT, "classpath:/sources/java/sample-line-multi.java.txt",
                   new LicenseHeader(2, 3, 1, 2, List.of("// this is", "// header"))
            ),
            sample(HeaderStyle.LINE_COMMENT, "classpath:/sources/java/sample-missing.java.txt",
                   new HeaderCandidate(0, 1)
            )
        );
    }
}

