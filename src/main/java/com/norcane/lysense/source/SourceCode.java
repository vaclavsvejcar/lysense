package com.norcane.lysense.source;


import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.source.metadata.Metadata;
import com.norcane.lysense.source.support.SourceCodeSupport;
import com.norcane.lysense.template.Variables;

/**
 * Represents loaded and analyzed source code.
 *
 * @param languageId ID of the programming language and corresponding implementation of {@link SourceCodeSupport} that was used to analyze this source code
 * @param resource   resource of the source code
 * @param variables  extracted <i>dynamic variables</i>
 * @param metadata   analyzed <i>metadata</i>
 * @see SourceCodeSupport#languageId()
 */
public record SourceCode(LanguageId languageId, Resource resource, Variables variables, Metadata metadata) {
}
