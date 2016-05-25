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

import com.mitchellbosecke.pebble.loader.ClasspathLoader;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationResults;

/**
 * Manager to generate user reports for OCSANA results
 **/

public class ResultsReportManager {
    private static String GENERATION_STAGE_ONLY_REPORT_HTML_TEMPLATE = "results-reports/GenerationStageOnlyReport.html";
    private static String GENERATION_STAGE_ONLY_REPORT_TXT_TEMPLATE = "results-reports/GenerationStageOnlyReport.txt";
    private static String FULL_REPORT_HTML_TEMPLATE = "results-reports/FullResultsReport.html";
    private static String FULL_REPORT_TXT_TEMPLATE = "results-reports/FullResultsReport.txt";


    private GenerationContext generationContext;
    private GenerationResults generationResults;

    private PebbleTemplate generationReportHTMLTemplate;
    private PebbleTemplate generationReportTXTTemplate;

    private PrioritizationContext prioritizationContext;
    private PrioritizationResults prioritizationResults;

    private PebbleTemplate fullReportHTMLTemplate;
    private PebbleTemplate fullReportTXTTemplate;

    public ResultsReportManager () {
        // Compile templates
        ClasspathLoader loader = new ClasspathLoader();
        loader.setPrefix("templates/");

        PebbleEngine htmlEngine = new PebbleEngine.Builder().loader(loader).strictVariables(true).build();
        PebbleEngine textEngine = new PebbleEngine.Builder().loader(loader).strictVariables(true).autoEscaping(false).build();
        try {
            generationReportHTMLTemplate = htmlEngine.getTemplate(GENERATION_STAGE_ONLY_REPORT_HTML_TEMPLATE);
            generationReportTXTTemplate = textEngine.getTemplate(GENERATION_STAGE_ONLY_REPORT_TXT_TEMPLATE);

            fullReportHTMLTemplate = htmlEngine.getTemplate(FULL_REPORT_HTML_TEMPLATE);
            fullReportTXTTemplate = textEngine.getTemplate(FULL_REPORT_TXT_TEMPLATE);
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

    private Boolean hasPrioritizationResults () {
        return prioritizationContext != null;
    }

    private Map<String, Object> getData () {
        Map<String, Object> data = new HashMap<>();
        data.put("generationContext", generationContext);
        data.put("generationResults", generationResults);

        if (hasPrioritizationResults()) {
            data.put("prioritizationContext", prioritizationContext);
            data.put("prioritizationResults", prioritizationResults);
        }

        return data;
    }

    public String reportAsHTML () {
        Writer writer = new StringWriter();
        try {
            if (hasPrioritizationResults()) {
                fullReportHTMLTemplate.evaluate(writer, getData());
            } else {
                generationReportHTMLTemplate.evaluate(writer, getData());
            }
        }  catch (PebbleException|IOException e) {
            throw new IllegalStateException("Could not produce generation stage HTML report. Please report the following error to the plugin author: " + e.getMessage());
        }

        return writer.toString();
    }

    public String reportAsText () {
        Writer writer = new StringWriter();
        try {
            if (hasPrioritizationResults()) {
                fullReportTXTTemplate.evaluate(writer, getData());
            } else {
                generationReportTXTTemplate.evaluate(writer, getData());
            }
        }  catch (PebbleException|IOException e) {
            throw new IllegalStateException("Could not produce generation stage HTML report. Please report the following error to the plugin author: " + e.getMessage());
        }

        return writer.toString();
    }
}
