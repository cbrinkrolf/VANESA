## Overview
VANESA is an open-source software project which aims to support scientist in the workflow of systems biology application cases. It provides a graphical user interface (GUI) and the general modeling approach is based on graphs. For **Modeling**, either biological networks or Petri nets can be chosen. Beside start modeling from scratch, a biological network can be retrieved from an data warehouse via web service. 

Petri nets can be simulated with the help of an [OpenModelica](https://www.openmodelica.org/) installation. Simulation results are made available for further investigation during process of **simulation**.

Both, biological networks as well as Petri nets, can be analyzed by general graph algorithms. Models are saved as SBML file, simulation results can be exported as CSV files, and graph pictures and charts can be saved as SVG, PNG and PDF files. 

## Releases
The latest release is [pre-v.0.4](https://github.com/cbrinkrolf/VANESA/releases/tag/v.0.4) which requires a [Java SE 11 (or newer)](https://www.oracle.com/java/technologies/javase-downloads.html) installation. It is compatible with OpenModelica 1.17.0 stable release, in case Petri net simulation is desired.

The last relese of VANESA1 is [v.0.1.9](https://sourceforge.net/projects/vanesa/files/vanesa/0.1/VANESA-0.1.9.jar/download). It is based on the old VANESA branch of the former [SourceForge SVN repository](https://sourceforge.net/p/vanesa/code/HEAD/tree/VANESA/) and requires Java 1.8 runtime. 

## Installation
Unless simulation is desired, installation of Java SE 11+ and executing the provided jar file is sufficient. Simulation of Petri nets is based on OpenModelica. The latest compatible stable release should be used instead of nightly builds. The installation path must not contain any whitespaces! During installation process, make sure that you select PNlib as library to install. By default, all third party libraries will be installed. The installation process will also set some environment variables which are needed for VANESA to detect the OpenModelica installation.

## Further project information
TBA
### Publications
TBA
### Links
TBA

## Citation
If you used VANESA as part of your scientific work, please cite the following article:

C. Brinkrolf, C., S. J. Janowski, B. Kormeier, M. Lewinski, K. Hippe, D. Borck, R. Hofestädt
VANESA - A Software Application for the Visualization and Analysis of Networks in Systems Biology Applications.
Journal of Integrative Bioinformatics 11(2):239, 2014.
[DOI: 10.2390/biecoll-jib-2014-239](http://dx.doi.org/10.2390/biecoll-jib-2014-239)

## About
VANESA is an academic software project of the [Bioinformatics / Medical Informatics Department](https://www.techfak.uni-bielefeld.de/ags/bi/) of [Faculty of Technology](https://www.uni-bielefeld.de/(en)/technische-fakultaet/) at [Bielefeld University](https://www.uni-bielefeld.de/%3C-de,en%3E/) in Germany and its development started more than 10 years ago. 
