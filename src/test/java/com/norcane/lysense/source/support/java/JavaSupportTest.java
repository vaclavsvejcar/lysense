/*
 * lysense :: license header manager
 * Copyright (c) 2023-2024 Vaclav Svejcar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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

