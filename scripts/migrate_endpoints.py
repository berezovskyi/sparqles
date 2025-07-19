import json
from rdflib import Graph, URIRef, Literal
from rdflib.namespace import RDF, RDFS, DCTERMS

def migrate_endpoints():
    g = Graph()

    with open('database/dump/sparqles/endpoints.json', 'r') as f:
        for line in f:
            endpoint_data = json.loads(line)

            endpoint_uri = URIRef(endpoint_data['uri'])

            g.add((endpoint_uri, RDF.type, URIRef("http://sparqles.com/ontology#Endpoint")))

            for dataset in endpoint_data['datasets']:
                dataset_uri = URIRef(dataset['uri'])
                g.add((dataset_uri, RDF.type, URIRef("http://sparqles.com/ontology#Dataset")))
                g.add((dataset_uri, RDFS.label, Literal(dataset['label'])))
                g.add((endpoint_uri, URIRef("http://sparqles.com/ontology#hasDataset"), dataset_uri))

    with open('database/endpoints.ttl', 'w') as f:
        f.write(g.serialize(format='turtle'))

if __name__ == '__main__':
    migrate_endpoints()
