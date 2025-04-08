![Java CI](https://github.com/cbrinkrolf/VANESA/actions/workflows/maven.yml/badge.svg?branch=master)
![Release](https://img.shields.io/github/v/release/cbrinkrolf/VANESA)
![Downloads](https://img.shields.io/github/downloads/cbrinkrolf/VANESA/total)
![License](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-blue)

# VANESA2
## Current Releases
- [latest pre release](https://github.com/cbrinkrolf/VANESA/releases/tag/v.0.5_pre_2) (v.0.5-pre_2 Release from 2025-04-08, for current paper review)
- [latest stable release](https://github.com/cbrinkrolf/VANESA/releases/tag/v.0.4) (v.0.4 from 2023-03-01)

## Instructions for simulation
In order to run simulations in VANESA, [OpenModelica](https://openmodelica.org) needs to be installed.

For newer  OpenModelica versions with package manager the [PNlib](https://github.com/AMIT-HSBI/PNlib) is automatically
installed. Otherwise, the PNlib needs to be installed as well.

## Running from source
To build VANESA2 run:
```shell
$ mvn compile
```

To run VANESA2 run:
```shell
$ mvn exec:java
```

To clean up generated files from the build process run:
```shell
$ mvn clean
```

To build a bundled executable jar file run:
```shell
$ mvn package
```

## Config YAML
If you want to use your own YAML configuration file, please just add a file `YamlSourceFile.txt` at the same location as
the `VANESA.jar` and put the path to the configuration file in first line. The following can be used as example
`YamlSourceFile.txt` file:

```
D:\Desktop\test\test.yaml
If you want to use your own YAML configuration file, THIS file has to be at the same location as the VANESA jar!
```
