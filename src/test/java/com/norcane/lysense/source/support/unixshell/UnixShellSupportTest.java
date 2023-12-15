package com.norcane.lysense.source.support.unixshell;


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
public class UnixShellSupportTest extends SourceCodeSupportTestKit {

    @Inject
    UnixShellSupportFactory unixShellSupportFactory;

    @Override
    protected InstanceFactory<SourceCodeSupport> sourceCodeSupportFactory() {
        return unixShellSupportFactory;
    }

    @Override
    protected List<TestSample> samples() {
        return List.of(
            sample(HeaderStyle.LINE_COMMENT, "classpath:/sources/unix-shell/sample-1.sh.txt",
                   new LicenseHeader(3, 4, 1, 1, List.of("# This is", "# header"))
            ),
            sample(HeaderStyle.LINE_COMMENT, "classpath:/sources/unix-shell/sample-2.sh.txt",
                   new HeaderCandidate(1, 1)
            ),
            sample(HeaderStyle.LINE_COMMENT, "classpath:/sources/unix-shell/sample-3.sh.txt",
                   new HeaderCandidate(1, 1)
            )
        );
    }
}
