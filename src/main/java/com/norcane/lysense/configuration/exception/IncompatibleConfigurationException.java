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
package com.norcane.lysense.configuration.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.meta.SemVer;

public class IncompatibleConfigurationException extends ApplicationException {

    private final SemVer minBaseVersion;
    private final SemVer currentBaseVersion;

    public IncompatibleConfigurationException(SemVer minBaseVersion, SemVer currentBaseVersion) {
        super(ErrorCode.INCOMPATIBLE_CONFIGURATION,
                STR."Incompatible configuration found, minimum base version is \{minBaseVersion}, current base version is \{currentBaseVersion}");

        this.minBaseVersion = minBaseVersion;
        this.currentBaseVersion = currentBaseVersion;
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
                .problem(STR."""
                         Incompatible configuration file has been found. Your configuration file has base version \{currentBaseVersion}, but the minimum \
                         supported base version is \{minBaseVersion}.""")
                .solution(
                        """
                                Please check that some of the following isn't wrong:
                                            
                                  - you are using an old version of the configuration file
                                  - you are using a newer version of the configuration file with an older version of the application
                                  
                                If you need to upgrade your configuration file, please refer to the official documentation.""")
                .build();
    }
}
