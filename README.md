# ContoDiff
This project is the implementation of the ContoDiff approach enabling the diff computation for two ontology versions. The realization is based on a local h2 database for managing the two versions. The result is an evolution mapping consisting of additions, deletions, and changes of concepts, relationships, and attributes. Moreover, the method determines a complex diff regarding the basic diff. You can find more information in the following publication:

Michael Hartung, Anika Groß, Erhard Rahm:
<a href='https://dbs.uni-leipzig.de/file/contoDiff2013.pdf'>COnto-Diff: generation of complex evolution mappings for life science ontologies.</a> J. Biomed. Informatics 46(1): 15-32 (2013)


## Run
You can start the application by the following command line:

```
java org.ContoDiffMain -oa path_to_first_ontology -ob path_to_second_ontology -o outputfile
```
