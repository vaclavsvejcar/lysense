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
              STR. "Incompatible configuration found, minimum base version is \{ minBaseVersion }, current base version is \{ currentBaseVersion }" );

        this.minBaseVersion = minBaseVersion;
        this.currentBaseVersion = currentBaseVersion;
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR. """
                         Incompatible configuration file has been found. Your configuration file has base version \{ currentBaseVersion }, but the minimum \
                         supported base version is \{ minBaseVersion }.""" )
            .solution(
                """
                    Please check that some of the following isn't wrong:
                                
                      - you are using an old version of the configuration file
                      - you are using a newer version of the configuration file with an older version of the application
                      
                    If you need to upgrade your configuration file, please refer to the official documentation.""")
            .build();
    }
}
