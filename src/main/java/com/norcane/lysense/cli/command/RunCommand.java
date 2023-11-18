package com.norcane.lysense.cli.command;

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
public class RunCommand {
}
