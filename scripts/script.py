import sys
import rdflib
from rdflib.namespace import RDF, RDFS, DCTERMS
from rdflib import Namespace
import subprocess
import urllib.parse
import requests

# Define namespaces
VOID = Namespace("http://rdfs.org/ns/void#")
SD = Namespace("http://www.w3.org/ns/sparql-service-description#")

def parse_turtle_file(file_path):
    # Create a graph
    g = rdflib.Graph()

    g.bind("void", VOID)
    g.bind("sd", SD)
#    g.bind("dcterms", DCTERMS)

    # Parse the Turtle file
    g.parse(file_path, format='turtle')

    # Query the graph for the sparqlEndpoint and label
    query = """
    PREFIX dcterms: <http://purl.org/dc/terms/>

    SELECT ?endpointUri ?endpointLabel
    WHERE {
        {
            ?dataset a void:Dataset ;
                    void:sparqlEndpoint ?endpointUri ;
                    rdfs:label ?endpointLabel .
        }
        UNION
        {
            ?service a sd:Service ;
                    sd:endpoint ?endpointUri ;
                    dcterms:description ?endpointLabel .
        }
    }
    """
    results = g.query(query)

    return results

def decode_and_filter_uri(endpointUri):
    # Strip the 'urn:sd:' prefix if present
    if endpointUri.startswith("urn:sd:"):
        endpointUri = endpointUri[len("urn:sd:"):]

    # URL-decode the URI
    endpointUri = urllib.parse.unquote(endpointUri)

    return endpointUri

def check_url(endpointUri):
    try:
        response = requests.get(endpointUri + "?query=ASK+WHERE%7B?s+?p+?o%7D", timeout=5)
        if response.status_code == 200:
            return True
        else:
            print(f"URL {endpointUri} returned status code {response.status_code}")
            return False
    except requests.RequestException as e:
#        print(f"URL {endpointUri} failed to connect: {e}")
        return False

def execute_system_call(endpointUri, endpointLabel):
    # Construct the command
    command = [
        "docker", "compose", "exec", "backend-svc", "bash", "-c",
        f"/build/bin/sparqles -p /build/src/main/resources/sparqles_docker.properties --addEndpoint {endpointUri} '{endpointLabel}'"
    ]

    # Execute the command
    subprocess.run(command, check=True)
    #print(" ".join(command))


def truncate_description(description):
    # Split the description by lines
    lines = description.splitlines()

    # Take the first line
    first_line = lines[0] if lines else ""

    # Truncate to the first 160 characters
    truncated_description = first_line[:160]

    return truncated_description

def main(file_path):
    # Parse the Turtle file
    results = parse_turtle_file(file_path)

    # Iterate over the results and execute the system call for each endpoint
    for row in results:
        endpointUri = row.endpointUri
        endpointLabel = truncate_description(row.endpointLabel)
        if "'" in endpointLabel:
            endpointLabel = ""
        # Decode and filter the URI
        endpointUri = decode_and_filter_uri(endpointUri)
        if endpointUri.startswith("http://") or endpointUri.startswith("https://"):
            # Check if the URL is reachable and returns a 200 status code
            if check_url(endpointUri):
                execute_system_call(endpointUri, endpointLabel)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <path_to_turtle_file>")
        sys.exit(1)

    file_path = sys.argv[1]
    main(file_path)
