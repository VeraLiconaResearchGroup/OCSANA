/**
 * Manager to generate various user reports for OCSANA results
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.*;

// Templating engine imports
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationResults;

/**
 * Manager to generate user reports for OCSANA results
 **/

public class ResultsReportManager {
    private static String GENERATION_REPORT_HTML_TEMPLATE = "templates/results-reports/GenerationResultsReport.html";
    private static String GENERATION_REPORT_TXT_TEMPLATE = "templates/results-reports/GenerationResultsReport.txt";
    private static String PRIORITIZATION_REPORT_HTML_TEMPLATE = "templates/results-reports/PrioritizationResultsReport.html";
    private static String PRIORITIZATION_REPORT_TXT_TEMPLATE = "templates/results-reports/PrioritizationResultsReport.txt";


    private GenerationContext generationContext;
    private GenerationResults generationResults;

    private PebbleTemplate generationReportHTMLTemplate;
    private PebbleTemplate generationReportTXTTemplate;

    private PrioritizationContext prioritizationContext;
    private PrioritizationResults prioritizationResults;

    private PebbleTemplate prioritizationReportHTMLTemplate;
    private PebbleTemplate prioritizationReportTXTTemplate;

    public ResultsReportManager () {
        // Compile templates
        PebbleEngine htmlEngine = new PebbleEngine.Builder().strictVariables(true).build();
        PebbleEngine textEngine = new PebbleEngine.Builder().strictVariables(true).autoEscaping(false).build();
        try {
            generationReportHTMLTemplate = htmlEngine.getTemplate(GENERATION_REPORT_HTML_TEMPLATE);
            generationReportTXTTemplate = textEngine.getTemplate(GENERATION_REPORT_TXT_TEMPLATE);

            prioritizationReportHTMLTemplate = htmlEngine.getTemplate(PRIORITIZATION_REPORT_HTML_TEMPLATE);
            prioritizationReportTXTTemplate = textEngine.getTemplate(PRIORITIZATION_REPORT_TXT_TEMPLATE);
        } catch (PebbleException e) {
            throw new IllegalStateException("Could not load result report templates. Please report the following error to the plugin author: " + e.getMessage());
        }
    }

    public void update (GenerationContext generationContext,
                        GenerationResults generationResults) {
        this.generationContext = generationContext;
        this.generationResults = generationResults;

        prioritizationContext = null;
        prioritizationResults = null;
    }

    public void update (PrioritizationContext prioritizationContext,
                        PrioritizationResults prioritizationResults) {
        this.prioritizationContext = prioritizationContext;
        this.prioritizationResults = prioritizationResults;
    }

    public String generationReportAsHTML () {
        Map<String, Object> data = new HashMap<>();
        data.put("generationContext", generationContext);
        data.put("generationResults", generationResults);

        Writer writer = new StringWriter();
        try {
            generationReportHTMLTemplate.evaluate(writer, data);
        }  catch (PebbleException|IOException e) {
            throw new IllegalStateException("Could not produce generation stage HTML report. Please report the following error to the plugin author: " + e.getMessage());
        }

        return writer.toString();
    }

    public String generationReportAsText () {
        Map<String, Object> data = new HashMap<>();
        data.put("generationContext", generationContext);
        data.put("generationResults", generationResults);

        Writer writer = new StringWriter();
        try {
            generationReportTXTTemplate.evaluate(writer, data);
        }  catch (PebbleException|IOException e) {
            throw new IllegalStateException("Could not produce generation stage HTML report. Please report the following error to the plugin author: " + e.getMessage());
        }

        return writer.toString();
    }
}
