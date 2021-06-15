package org.io;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;


public class OntologyReader {

    OWLOntologyManager manager;


    public OntologyReader(){


    }

    public OWLOntology loadOntology(File f) throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        return manager.loadOntologyFromOntologyDocument(f);
    }

}
