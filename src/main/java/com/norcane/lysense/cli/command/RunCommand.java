package com.norcane.lysense.cli.command;

import com.norcane.lysense.cli.ReturnCode;
import com.norcane.lysense.ui.console.Console;

import jakarta.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(
    name = "run",
    description = "add, drop or update license headers",
    usageHelpAutoWidth = true,
    headerHeading = "@|bold,underline Usage|@:%n%n",
    descriptionHeading = "%n@|bold,underline Description|@:%n%n",
    parameterListHeading = "%n@|bold,underline Parameters|@:%n",
    optionListHeading = "%n@|bold,underline Options|@:%n"
)
public class RunCommand extends AbstractCommand{

    @Inject
    public RunCommand(Console console) {
        super(console);
    }

    @Override
    protected ReturnCode execute() {
        if (true) throw new IllegalStateException("fokume");
        return ReturnCode.SUCCESS;
    }
}
