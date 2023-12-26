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
package com.norcane.lysense.meta;

public final class ProductInfo {

    private ProductInfo() {
        // utility class - hence the private constructor
        throw new IllegalStateException("This class is not meant to be instantiated");
    }

    public static final String NAME = BuildInfo.NAME;
    public static final String DESCRIPTION = BuildInfo.DESCRIPTION;
    public static final String VERSION_STRING = BuildInfo.VERSION;
    public static final SemVer VERSION = SemVer.from(VERSION_STRING);
    public static final String URL_HOMEPAGE = "https://github.com/vaclavsvejcar/lysense";
    public static final String URL_REPORT_BUG = "https://github.com/vaclavsvejcar/lysense/issues/new";


    public static final String ERROR_CODE_PREFIX = "LSN";
    public static final String USER_CONFIGURATION_FILE = "lysense.yaml";
    public static final String TEMPLATES_DIR = "lysense-templates";

    public static String productHeader() {
        return STR."Welcome to @|bold,magenta \{NAME} \{VERSION}|@ :: @|underline \{URL_HOMEPAGE}|@";
    }
}
