#!/usr/bin/env python

# Parser to translate DrugBank XML into drug-gene interaction data
#
# Copyright Vera-Licona Research Group (C) 2015
#
# This software is licensed under the Artistic License 2.0, see the
# LICENSE file or
# http://www.opensource.org/licenses/artistic-license-2.0.php for
# details

import argparse
from lxml import etree
from collections import defaultdict
import simplejson as json
import logging

def parse_xml(filename):
    # Construct a dict mapping each gene name (Entrez standard) to a
    # dict mapping action types to lists of drug names. For example, we
    # might have result["ERBB2"]["inhibitor"] == ["Afatinib"].
    logging.info("Reading XML file")
    try:
        tree = etree.parse(filename)
    except IOError:
        raise ValueError("Could not read DB file {0}".format(filename))

    DB = "{http://www.drugbank.ca}"

    logging.info("Parsing XML tree")
    root = tree.getroot()

    interactions = defaultdict(lambda: defaultdict(list))

    for drug in root.findall(DB + 'drug'):
        drugname = drug.find(DB + 'name').text
        druggroups = drug.find(DB + 'groups').findall(DB + 'group')

        approved = False;
        investigational = False;

        for group in druggroups:
            if group.text == 'approved':
                approved = True
            if group.text == 'investigational':
                investigational = True

        drugdict = {'name': drugname, 'approved': approved, 'investigational': investigational}

        for target in drug.find(DB + 'targets').findall(DB + 'target'):
            polypeptide = target.find(DB + 'polypeptide')
            if polypeptide is not None:
                geneXML = polypeptide.find(DB + 'gene-name')
                if geneXML is not None:
                    genename = geneXML.text
                    if genename is not None:
                        for action in target.find(DB + 'actions').getchildren():
                            actionname = action.text
                            interactions[genename][actionname].append(drugdict)

    logging.info("Found {0} drugs in DrugBank XML file".format(len(interactions)))
    return interactions

def write_json(interactions, filename):
    logging.info("Writing JSON file")
    with open(filename, 'w') as outfile:
        json.dump(interactions, outfile)

def main():
    # Set up argument processing
    parser = argparse.ArgumentParser(description="MHS algorithm benchmark runner")

    parser.add_argument("drugbank_db_file", help="XML file of DrugBank data (download from www.drugbank.ca)")
    parser.add_argument("json_output_file", help="JSON file to write with interaction data")
    parser.add_argument('-v', '--verbose', action="count", default=0, help="Print verbose logs (may be used multiple times)")

    args = parser.parse_args()

    # Set up logging
    if args.verbose == 0:
        log_level = logging.WARNING
    elif args.verbose == 1:
        log_level = logging.INFO
    else:
        log_level = logging.DEBUG

    logging.basicConfig(level = log_level)

    # Parse the XML file
    interactions = parse_xml(args.drugbank_db_file)

    # Write the result
    write_json(interactions, args.json_output_file)


if __name__ == "__main__":
    main()
