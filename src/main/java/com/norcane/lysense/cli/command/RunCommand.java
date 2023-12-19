package com.norcane.lysense.cli.command;

import com.google.common.base.Stopwatch;

import com.norcane.lysense.cli.ReturnCode;
import com.norcane.lysense.configuration.ConfigurationManager;
import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.source.SourceCode;
import com.norcane.lysense.source.SourceCodeProcessor;
import com.norcane.lysense.ui.console.Console;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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
public class RunCommand extends AbstractCommand {

    private final Configuration configuration;
    private final ConfigurationManager configurationManager;
    private final ResourceLoader resourceLoader;
    private final SourceCodeProcessor sourceCodeProcessor;

    @Inject
    public RunCommand(Console console,
                      Configuration configuration,
                      ConfigurationManager configurationManager,
                      ResourceLoader resourceLoader,
                      SourceCodeProcessor sourceCodeProcessor) {

        super(console);
        this.configuration = configuration;
        this.configurationManager = configurationManager;
        this.resourceLoader = resourceLoader;
        this.sourceCodeProcessor = sourceCodeProcessor;
    }

    @Override
    protected ReturnCode execute() {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        console.printLn(STR."Loaded configuration from \{configurationManager.userConfigurationResource().uri()}");

        final List<SourceCode> sourceCodes = loadSourceCodes();
        console.printLn(STR."Found @|bold \{sourceCodes.size()}|@ source code files from @|bold \{configuration.sources()}|@");


        stopwatch.stop();
        return ReturnCode.SUCCESS;
    }

    private List<SourceCode> loadSourceCodes() {
        final Set<String> resourceTypes = sourceCodeProcessor.sourceCodeSupports().keySet();
        final Predicate<Resource> filter = resource -> resourceTypes.contains(resource.extension());

        return configuration.sources().stream()
            .map(sourcePath -> resourceLoader.resources(sourcePath, filter, true))
            .flatMap(Collection::stream)
            .map(Resource::asWritableOrFail)
            .map(sourceCodeProcessor::process)
            .toList();
    }
}
