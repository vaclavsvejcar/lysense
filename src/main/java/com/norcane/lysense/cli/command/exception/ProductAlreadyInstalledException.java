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
package com.norcane.lysense.cli.command.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.meta.ProductInfo;
import com.norcane.lysense.resource.Resource;

/**
 * Exception thrown when user attempts to install the product in directory where user configuration has been already found.
 */
public class ProductAlreadyInstalledException extends ApplicationException {

    private final Resource existingUserConfiguration;

    public ProductAlreadyInstalledException(Resource existingUserConfiguration) {
        super(ErrorCode.PRODUCT_ALREADY_INSTALLED, STR."\{ProductInfo.NAME} already installed, found user configuration: \{existingUserConfiguration.uri()}");

        this.existingUserConfiguration = existingUserConfiguration;
    }


    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
                .problem(
                        STR."It seems you're attempting to install \{ProductInfo.NAME} in current directory, but user configuration has been already found in @|bold \{existingUserConfiguration.uri()}|@."
                )

                .solution(
                        STR."Please check that you're installing \{ProductInfo.NAME} in correct directory."
                )

                .build();
    }
}
