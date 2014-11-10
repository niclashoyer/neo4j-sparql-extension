# neo4j-sparql-extension

[![Build Status](https://api.shippable.com/projects/540f1f2aec1d09a97e66f20d/badge?branchName=master)](https://app.shippable.com/projects/540f1f2aec1d09a97e66f20d/builds/latest)
[![Dependency Status](https://www.versioneye.com/user/projects/539018d346c4731b13000040/badge.svg?style=flat)](https://www.versioneye.com/user/projects/539018d346c4731b13000040)
[![License](http://img.shields.io/badge/license-GPLv3-lightgrey.svg?style=flat)](LICENSE)
[![Releases](http://img.shields.io/badge/release-1.0.0-blue.svg?style=flat)](https://github.com/niclashoyer/neo4j-sparql-extension/releases)
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

Please note that if there is any data in the database that
was not imported using the `/rdf/graph` resource the plugin might crash,
because the plugin expects the data to be stored in a special way to
support RDF storage in Neo4j.

### SPARQL Protocol (SPARQL 1.1 Queries)

Base resource: `/rdf/query`

Use this resource to execute [SPARQL queries](http://www.w3.org/TR/sparql11-query/).

```bash
$ curl -v -X POST localhost:7474/rdf/query \
       -H "Content-Type: application/sparql-query" \
       -H "Accept: application/sparql-results+json" \
       -d "SELECT ?s ?p ?o WHERE { ?s ?p ?o } LIMIT 5"
```

See [SPARQL 1.1 Protocol "2.1 query operation"](http://www.w3.org/TR/sparql11-protocol/#query-operation).

### SPARQL Graph Protocol

Base resource: `/rdf/graph`

Use this resource to add, replace or delete RDF data.

```bash
$ curl -v -X PUT \
       localhost:7474/rdf/graph \
       -H "Content-Type:text/turtle" --data-binary @data.ttl
```

```bash
$ curl -v localhost:7474/rdf/graph
```

See [SPARQL 1.1 Graph Store HTTP Protocol "5 Graph Management Operations"](http://www.w3.org/TR/sparql11-http-rdf-update/#graph-management).

### SPARQL Protocol (SPARQL 1.1 Update Queries)

Base resource: `/rdf/update`

Use this resource to execute [SPARQL update](http://www.w3.org/TR/sparql11-update/) queries.

```bash
$ curl -v -X POST localhost:7474/rdf/query \
       -H "Content-Type: application/sparql-update" \
       -d "@prefix dc: <http://purl.org/dc/elements/1.1/> . \
           @prefix ns: <http://example.org/ns#> . \
           <http://example/book1> ns:price 42 ."
```

See [SPARQL 1.1 Protocol "2.1 update operation"](http://www.w3.org/TR/sparql11-protocol/#update-operation).

## OWL-2 Inference

The plugin supports (limited) OWL-2 reasoning using query rewriting of SPARQL
algebra expressions. For a list of supported axioms, see
[the inference wiki page](https://github.com/niclashoyer/neo4j-sparql-extension/wiki/Inference).

To use inference the TBox must be uploaded to the special graph
`urn:sparqlextension:tbox`:

```bash
$ curl -v -X PUT \
       localhost:7474/rdf/graph\?graph=urn%3Asparqlextension%3Atbox \
       -H "Content-Type:text/turtle" --data-binary @tbox.ttl
```

Now it is possible to send SPARQL queries that additionally return
inferrend solutions. There are two ways to enable inference:

#### Using a Query Parameter

Just send your SPARQL query to `/rdf/query` and add a query parameter
`inference=true`:

```bash
$ curl -v -X POST localhost:7474/rdf/query\?inference=true \
       -H "Content-Type: application/sparql-query" \
       -H "Accept: application/sparql-results+json" \
       -d "SELECT ?s ?p ?o WHERE { ?s ?p ?o } LIMIT 5"
```

#### Using the Inference Resource

Send your SPARQL query to `/rdf/query/inference`:

```bash
$ curl -v -X POST localhost:7474/rdf/query/inference \
       -H "Content-Type: application/sparql-query" \
       -H "Accept: application/sparql-results+json" \
       -d "SELECT ?s ?p ?o WHERE { ?s ?p ?o } LIMIT 5"
```

## Chunked Imports

If you want to import a large amount of RDF data, you should enable the
chunked import. The import will be split into smaller chunks and each chunk
will be committed seperately to the database. To enable a chunked import
set the query parameter `chunked=true` when using the `/rdf/graph` resource.

## Configuration

To change the configuration add a `sparql-extension.properties` file in the
`/conf` folder of the Neo4j server installation.

The default configuration is as follows:

```
de.unikiel.inf.comsys.neo4j.query.timeout = 120
de.unikiel.inf.comsys.neo4j.query.patterns = p,c,pc
de.unikiel.inf.comsys.neo4j.inference.graph = urn:sparqlextension:tbox
de.unikiel.inf.comsys.neo4j.chunksize = 1000
```

## License

[GPLv3](https://github.com/niclashoyer/neo4j-sparql-extension/blob/master/LICENSE)
