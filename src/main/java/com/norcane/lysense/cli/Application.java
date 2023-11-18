package com.norcane.lysense.cli;

import com.norcane.lysense.cli.command.RunCommand;
import com.norcane.lysense.meta.ProductInfo;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(
    name = ProductInfo.NAME,
    description = ProductInfo.DESCRIPTION,
    version = ProductInfo.VERSION_STRING,
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    subcommands = {RunCommand.class},
    headerHeading = "@|bold,underline Usage|@:%n%n",
    descriptionHeading = "%n@|bold,underline Description|@:%n%n",
    parameterListHeading = "%n@|bold,underline Parameters|@:%n",
    optionListHeading = "%n@|bold,underline Options|@:%n"
)
public class Application {
}
