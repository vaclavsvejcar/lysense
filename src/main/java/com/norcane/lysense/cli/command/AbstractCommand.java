package com.norcane.lysense.cli.command;

import com.norcane.lysense.cli.ReturnCode;
import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.UnexpectedBehaviorException;
import com.norcane.lysense.meta.ProductInfo;
import com.norcane.lysense.ui.alert.Alert;
import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.exception.ApplicationExceptionPrinter;

import java.util.concurrent.Callable;

import io.quarkus.logging.Log;
import picocli.CommandLine;

import static com.norcane.toolkit.Prelude.nonNull;
import static java.util.FormatProcessor.FMT;

/**
 * Base class to all <i>CLI</i> subcommands.
 */
public abstract class AbstractCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"--print-stack-trace"}, description = "print stack trace for errors", hidden = true)
    protected boolean printStackTrace;

    protected final Console console;

    public AbstractCommand(Console console) {
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
        console.printLn(ProductInfo.productHeader());
    }

    private void handleApplicationException(ApplicationException e) {
        final String errorCode = FMT."\{ProductInfo.ERROR_CODE_PREFIX}-%05d\{e.errorCode().code()}";

        Log.error(e);
        console.emptyLine();
        console.emptyLine();
        console.render(Alert.error("ERROR " + errorCode, e.getMessage()));
        console.render(ApplicationExceptionPrinter.of(printStackTrace, e));
    }
}
