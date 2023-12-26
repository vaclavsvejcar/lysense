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
package com.norcane.lysense.cli.command;

import com.norcane.lysense.cli.ReturnCode;
import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.UnexpectedBehaviorException;
import com.norcane.lysense.meta.ProductInfo;
import com.norcane.lysense.ui.alert.Alert;
import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.exception.ApplicationExceptionPrinter;
import io.quarkus.logging.Log;
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static com.norcane.toolkit.Prelude.nonNull;
import static java.util.FormatProcessor.FMT;

/**
 * Base class to all <i>CLI</i> subcommands.
 */
public abstract class CliCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"--print-stack-trace"}, description = "print stack trace for errors", hidden = true)
    protected boolean printStackTrace;

    protected final Console console;

    public CliCommand(Console console) {
        this.console = nonNull(console);
    }

    /**
     * Main logic of the command.
     *
     * @return exit code
     */
    protected abstract ReturnCode execute();

    @Override
    public final Integer call() {
        printProductHeader();

        try {
            return execute().code();
        } catch (ApplicationException e) {
            handleApplicationException(e);
            return ReturnCode.ERROR.code();
        } catch (Exception e) {
            handleApplicationException(UnexpectedBehaviorException.wrap(e));
            return ReturnCode.ERROR.code();
        }
    }

    void printProductHeader() {
        console.emptyLine();
        console.printLn(ProductInfo.productHeader());
    }

    private void handleApplicationException(ApplicationException e) {
        final String errorCode = FMT."\{ProductInfo.ERROR_CODE_PREFIX}-%05d\{e.errorCode().code()}";

        Log.error(e);
        console.emptyLine();
        console.emptyLine();
        console.render(Alert.error(STR."ERROR \{errorCode}", e.getMessage()));
        console.render(ApplicationExceptionPrinter.of(printStackTrace, e));
    }
}
