package com.norcane.lysense.cli.command;

import com.google.common.base.Stopwatch;

import com.norcane.lysense.cli.ReturnCode;
import com.norcane.lysense.configuration.ConfigurationManager;
import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.RunMode;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.source.SourceCode;
import com.norcane.lysense.source.SourceCodeProcessor;
import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import jakarta.inject.Inject;
import picocli.CommandLine;

import static com.norcane.toolkit.Prelude.when;

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

    @CommandLine.Option(
        names = {"-m", "--mode"},
        description = "run mode, available values: ${COMPLETION-CANDIDATES}",
        paramLabel = "MODE"
    )
    RunMode cliRunMode;

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

        final RunMode runMode = cliRunMode != null ? cliRunMode : configuration.runMode();

        console.printLn(STR."Loaded configuration from \{configurationManager.userConfigurationResource().uri()}");

        final List<SourceCode> sourceCodes = loadSourceCodes();
        console.printLn(STR."Found @|bold \{sourceCodes.size()}|@ source code files from @|bold \{configuration.sources()}|@");

        final RunResult runResult = switch (runMode) {
            case ADD -> addHeaders(sourceCodes);
            case DROP -> dropHeaders(sourceCodes);
            case UPDATE -> updateHeaders(sourceCodes);
        };

        stopwatch.stop();
        console.emptyLine();

        final long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        final int modifiedSourcesNo = runResult.modifiedSources().size();
        console.printLn(STR."Modified @|bold \{modifiedSourcesNo}|@ source code file(s) in @|bold \{elapsedMs}ms|@");

        return ReturnCode.SUCCESS;
    }

    private RunResult addHeaders(List<SourceCode> sourceCodes) {
        final List<SourceCode> modifiedSources = new ArrayList<>();

        final Function<SourceCode, String> messageFn =
            sourceCode -> sourceCode.metadata().header().isEmpty()
                          ? STR."Adding header to @|bold \{sourceCode.resource().uri()}|@"
                          : STR."Header already present in @|bold \{sourceCode.resource().uri()}|@";

        for (final SourceCode sourceCode : ProgressBar.concise(sourceCodes, messageFn, console)) {
            when(sourceCodeProcessor.addHeader(sourceCode).modified(), () -> modifiedSources.add(sourceCode));
        }

        return new RunResult(ReturnCode.SUCCESS, Collections.unmodifiableList(modifiedSources));
    }

    private RunResult dropHeaders(List<SourceCode> sourceCodes) {
        final List<SourceCode> modifiedSources = new ArrayList<>();

        final Function<SourceCode, String> messageFn =
            sourceCode -> sourceCode.metadata().header().isPresent()
                          ? STR."Dropping header from @|bold \{sourceCode.resource().uri()}|@"
                          : STR."No header present in @|bold \{sourceCode.resource().uri()}|@";

        for (final SourceCode sourceCode : ProgressBar.concise(sourceCodes, messageFn, console)) {
            when(sourceCodeProcessor.dropHeader(sourceCode).modified(), () -> modifiedSources.add(sourceCode));
        }

        return new RunResult(ReturnCode.SUCCESS, Collections.unmodifiableList(modifiedSources));
    }

    private RunResult updateHeaders(List<SourceCode> sourceCodes) {
        final List<SourceCode> modifiedSources = new ArrayList<>();

        final Function<SourceCode, String> messageFn =
            sourceCode -> sourceCode.metadata().header().isPresent()
                          ? STR."Updating header in @|bold \{sourceCode.resource().uri()}|@"
                          : STR."Adding header to @|bold \{sourceCode.resource().uri()}|@";

        for (final SourceCode sourceCode : ProgressBar.concise(sourceCodes, messageFn, console)) {
            when(sourceCodeProcessor.updateHeader(sourceCode).modified(), () -> modifiedSources.add(sourceCode));
        }

        return new RunResult(ReturnCode.SUCCESS, Collections.unmodifiableList(modifiedSources));
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

    private record RunResult(ReturnCode returnCode, List<SourceCode> modifiedSources) {
    }
}
