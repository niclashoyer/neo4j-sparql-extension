# neo4j-sparql-extension

[![Build Status](https://api.shippable.com/projects/540f1f2aec1d09a97e66f20d/badge?branchName=master)](https://app.shippable.com/projects/540f1f2aec1d09a97e66f20d/builds/latest)
[![Dependency Status](https://www.versioneye.com/user/projects/539018d346c4731b13000040/badge.svg?style=flat)](https://www.versioneye.com/user/projects/539018d346c4731b13000040)
[![License](http://img.shields.io/badge/license-GPLv3-lightgrey.svg?style=flat)](LICENSE)
[![Releases](http://img.shields.io/badge/release-0.4.1-blue.svg?style=flat)](https://github.com/niclashoyer/neo4j-sparql-extension/releases)
[![Neo4j](http://img.shields.io/badge/Neo4j-2.1.5-77CE56.svg?style=flat)](http://www.neo4j.org/)
[![Gittip](http://img.shields.io/gratipay/niclashoyer.svg?style=flat)](https://www.gittip.com/niclashoyer/)

Neo4j [unmanaged extension](http://docs.neo4j.org/chunked/stable/server-unmanaged-extensions.html)
for [RDF](http://www.w3.org/TR/rdf-primer/) storage and
[SPARQL 1.1 query](http://www.w3.org/TR/sparql11-protocol/) features.

## Installation

Download the latest release from the [releases page](https://github.com/niclashoyer/neo4j-sparql-extension/releases) and place it
inside the `/plugins/` directory of the Neo4j server installation.

To enable the extension add it to the
`org.neo4j.server.thirdparty_jaxrs_classes` key in the
`/conf/neo4j-server.properties` file. For example:

```
org.neo4j.server.thirdparty_jaxrs_classes=de.unikiel.inf.comsys.neo4j=/rdf
```

The RDF/SPARQL extension is then avaiable as `/rdf` resource on the
Neo4j server.

### SPARQL Protocol (Queries)

Base resource: `/rdf/query`

### SPARQL Graph Protocol

Base resource: `/rdf/graph`

### SPARQL Protocol (Update)

Base resource: `/rdf/update`

## Configuration

To change the configuration add a `sparql-extension.properties` file in the
`/conf` folder of the Neo4j server installation.

The default configuration is as follows:

```
de.unikiel.inf.comsys.neo4j.query.timeout = 120
de.unikiel.inf.comsys.neo4j.query.patterns = p,c,pc
de.unikiel.inf.comsys.neo4j.inference.graph = urn:sparqlextension:tbox
```

## License

GPLv3
