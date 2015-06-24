# LODIE_WI

## Table of contents ##

- [Introduction](#introduction)
- [Resources](#resources)
- [Workflow](#workflow)
- [Page pre-processing](#page-pre-processing)
- [Candidate patterns for entity attributes](#candidate-patterns-for-entity-attributes)
- [Boilerplate removal](#boilerplate-removal)
- [Pattern ranking](#pattern-ranking)

### Introduction ###

This projects provides methods to learn website wrappers.
The assumptions are:
- we have a given Knowledge base K (e.g. DBpedia)
- we want to extract instances of a particular concept C and its attributes (as defined in K)
- there is a fixed domain D (e.g. Book) D ={d_1 … d_n} where each d_i is a set of homogeneous *entity-centric* webpages i.e.:
-- each webpage in d_i belongs to the same website (and share a common template)
-- each webpage in d_i describes one entity e of type C


A very short presentation can be found [here](http://www.slideshare.net/AnnaGentile/mining-entities-from-the-web) 

Relevant papers:
- **AI Magazine 2015**. Anna Lisa Gentile, Ziqi Zhang and Fabio Ciravegna (2015). [Early Steps Towards Web Scale Information Extraction with LODIE](http://www.aaai.org/ojs/index.php/aimagazine/article/view/2567). AI Magazine, 36(1), 55--64.

- **KCAP 2013**. Anna Lisa Gentile, Ziqi Zhang, Isabelle Augenstein and Fabio Ciravegna (2013). [Unsupervised wrapper induction using linked data](http://dl.acm.org/citation.cfm?doid=2479832.2479845). Proceedings of the seventh international conference on Knowledge capture, 41--48. Banff, Canada: ACM 

- **TSD 2014**. Anna Lisa Gentile, Ziqi Zhang and Fabio Ciravegna (2014). [Self Training Wrapper Induction with Linked Data](http://link.springer.com/chapter/10.1007%2F978-3-319-10816-2_35). Text, Speech and Dialogue - 17th International Conference, {TSD} 2014, Brno, Czech Republic, September 8-12, 2014. Proceedings, 285--292. [Paper PREPRINT](http://www.tsdconference.org/tsd2014/download/preprints/681.pdf)

- **ISWC 2014**. Anna Lisa Gentile and Suvodeep Mazumdar (2014). [User driven Information Extraction with LODIE](http://ceur-ws.org/Vol-1272/paper_112.pdf). Proceedings of the ISWC 2014 Posters & Demonstrations Track a track within the 13th International Semantic Web Conference (ISWC 2014), 385-388.

You can also view a less than two minutes [demo video](http://staffwww.dcs.shef.ac.uk/people/A.L.Gentile/demo/iswc2014.html).

### Resources ###
The folder [resources](./resources) contains:
- [gazetteers](./resources/gazetteers) that are used to seed the annotation phase. These gazetteers have been automatically generated, but are given as static resource here for reproducibility. Relevant gazetteers are provided for all domain-attributes tackled in the the evaluation [datasets](./resources/datasets)
- evaluation [datasets](./resources/datasets) with the relative groundtruth
- the [temp](./temp) folder is the default location where the method creates intermediate representations of pages.

The folder [experimentResults](./experimentResults) is the default location where the method saves experimental results.

### Workflow ###

### Page pre-processing ###

### Candidate patterns for entity attributes ###

### Boilerplate removal ###

### Pattern ranking ###
