const axios = require('axios');

SPARQLProvider = function (host, port) {
  this.sparqlEndpoint = `http://${host}:${port}/sparql`;
};

SPARQLProvider.prototype.query = async function (query, callback) {
  try {
    const response = await axios.get(this.sparqlEndpoint, {
      params: {
        query: query,
        format: 'json'
      }
    });
    callback(null, response.data.results.bindings);
  } catch (error) {
    callback(error);
  }
};

//Availability
SPARQLProvider.prototype.getAvailView = function (callback) {
  const query = `
    PREFIX sp: <http://sparqles.com/ontology#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    SELECT ?endpoint ?datasetLabel
    WHERE {
      ?endpoint a sp:Endpoint ;
                sp:hasDataset ?dataset .
      ?dataset rdfs:label ?datasetLabel .
    }
    ORDER BY ?datasetLabel ?endpoint
  `;
  this.query(query, callback);
};

//Interoperability
SPARQLProvider.prototype.getInteropView = function (callback) {
  const query = `
    PREFIX sp: <http://sparqles.com/ontology#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    SELECT ?endpoint ?datasetLabel
    WHERE {
      ?endpoint a sp:Endpoint ;
                sp:hasDataset ?dataset .
      ?dataset rdfs:label ?datasetLabel .
    }
    ORDER BY ?datasetLabel ?endpoint
  `;
  this.query(query, callback);
};

//Performance
SPARQLProvider.prototype.getPerfView = function (callback) {
  const query = `
    PREFIX sp: <http://sparqles.com/ontology#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    SELECT ?endpoint ?datasetLabel
    WHERE {
      ?endpoint a sp:Endpoint ;
                sp:hasDataset ?dataset .
      ?dataset rdfs:label ?datasetLabel .
    }
    ORDER BY ?datasetLabel ?endpoint
  `;
  this.query(query, callback);
};

//Discoverability
SPARQLProvider.prototype.getDiscoView = function (callback) {
  const query = `
    PREFIX sp: <http://sparqles.com/ontology#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    SELECT ?endpoint ?datasetLabel
    WHERE {
      ?endpoint a sp:Endpoint ;
                sp:hasDataset ?dataset .
      ?dataset rdfs:label ?datasetLabel .
    }
    ORDER BY ?datasetLabel ?endpoint
  `;
  this.query(query, callback);
};

//Endpoint view
SPARQLProvider.prototype.getEndpointView = function (epUri, callback) {
  const query = `
    PREFIX sp: <http://sparqles.com/ontology#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    SELECT ?dataset ?datasetLabel
    WHERE {
      <${epUri}> a sp:Endpoint ;
                sp:hasDataset ?dataset .
      ?dataset rdfs:label ?datasetLabel .
    }
  `;
  this.query(query, callback);
};

//autocomplete
SPARQLProvider.prototype.autocomplete = function (query, callback) {
  const sparqlQuery = `
    PREFIX sp: <http://sparqles.com/ontology#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    SELECT ?endpoint ?datasetLabel
    WHERE {
      ?endpoint a sp:Endpoint ;
                sp:hasDataset ?dataset .
      ?dataset rdfs:label ?datasetLabel .
      FILTER (regex(?datasetLabel, "${query}", "i") || regex(str(?endpoint), "${query}", "i"))
    }
    ORDER BY ?datasetLabel ?endpoint
  `;
  this.query(sparqlQuery, callback);
};

//endpoints count
SPARQLProvider.prototype.endpointsCount = function (callback) {
  const query = `
    PREFIX sp: <http://sparqles.com/ontology#>
    SELECT (COUNT(DISTINCT ?endpoint) as ?count)
    WHERE {
      ?endpoint a sp:Endpoint .
    }
  `;
  this.query(query, (error, results) => {
    if (error) {
      callback(error);
    } else {
      callback(null, results[0].count.value);
    }
  });
};

//endpoints list
SPARQLProvider.prototype.endpointsList = function (callback) {
  const query = `
    PREFIX sp: <http://sparqles.com/ontology#>
    SELECT DISTINCT ?endpoint
    WHERE {
      ?endpoint a sp:Endpoint .
    }
    ORDER BY ?endpoint
  `;
  this.query(query, callback);
};


//Last Update date
SPARQLProvider.prototype.getLastUpdate = function (callback) {
  // This is a placeholder. The data model needs to be updated to store this information.
  callback(null, [{ lastUpdate: new Date() }]);
};

//Index
SPARQLProvider.prototype.getIndex = function (callback) {
  // This is a placeholder. The data model needs to be updated to store this information.
  callback(null, {});
};

//Amonths
SPARQLProvider.prototype.getAMonths = function (callback) {
  // This is a placeholder. The data model needs to be updated to store this information.
  callback(null, []);
};

SPARQLProvider.prototype.getLastTenPerformanceMedian = function (uri, callback) {
  // This is a placeholder. The data model needs to be updated to store this information.
  callback(null, {});
};

SPARQLProvider.prototype.getLatestDisco = function (uri, callback) {
  // This is a placeholder. The data model needs to be updated to store this information.
  callback(null, []);
}

function median(values) {
  var arr = []
  for (var i in values) {
    if (values[i] == 0) continue;
    arr.push(values[i])
  }

  values = arr;

  values.sort(function (a, b) { return a - b; });

  var half = Math.floor(values.length / 2);

  if (values.length % 2)
    return values[half];
  else
    return (values[half - 1] + values[half]) / 2.0;
}

exports.SPARQLProvider = SPARQLProvider;
