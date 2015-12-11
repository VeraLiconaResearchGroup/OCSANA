# OCSANA

This software is a [CytoScape][] plugin to compute optimal combinations of interventions in biological signaling networks.
The original version of OCSANA was a plugin for Cytoscape 2, published in 2013 in ["OCSANA: optimal combinations of interventions from network analysis"][origpaper].
It has been rewritten for Cytoscape 3 with new algorithms for improved performance.

## Running the software
Load your network in Cytoscape.
From the "Apps" menu, select the "OCSANA" option.

## Building the software
To build the software, you must first clone this repository on your computer.
You must also install the [Maven][] build management system.
From the root directory of the project, you can then run `mvn package` to build the plugin JAR file and run the test suite.
Once the build is completed, you must copy the file `target/OCSANA-1.0-SNAPSHOT.jar` to your `CytoscapeConfiguration/3/apps/installed` directory.

[cytoscape]://cytoscape.org
[origpaper]: //dx.doi.org/10.1093/bioinformatics/btt195
[maven]: //maven.apache.org
