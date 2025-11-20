const { MongoClient } = require('mongodb');

class MongoDBProvider {
  constructor(host, port) {
    const url = `mongodb://${host}:${port}`;
    this.client = new MongoClient(url);
    this.db = null;
    this.connectPromise = this.client.connect()
      .then(client => {
        this.db = client.db('sparqles');
        console.log('Connected to database');
      })
      .catch(err => {
        console.error('Failed to connect to the database', err);
        process.exit(1);
      });
  }

  getCollection(collectionName, callback) {
    this.connectPromise.then(() => {
      try {
        const collection = this.db.collection(collectionName);
        callback(null, collection);
      } catch (error) {
        callback(error);
      }
    }).catch(err => callback(err));
  }

  getAvailView(callback) {
    this.getCollection('atasks_agg', (error, collection) => {
      if (error) return callback(error);
      collection.find({}, { projection: { "_id": 0 } }).sort({ "endpoint.datasets.0.label": 1, "endpoint.uri": 1 }).toArray()
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  getInteropView(callback) {
    this.getCollection('ftasks_agg', (error, collection) => {
      if (error) return callback(error);
      collection.find({}, { projection: { "_id": 0 } }).sort({ "endpoint.datasets.0.label": 1, "endpoint.uri": 1 }).toArray()
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  getPerfView(callback) {
    this.getCollection('ptasks_agg', (error, collection) => {
      if (error) return callback(error);
      collection.find({}, { projection: { "_id": 0 } }).sort({ "endpoint.datasets.0.label": 1, "endpoint.uri": 1 }).toArray()
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  getDiscoView(callback) {
    this.getCollection('dtasks_agg', (error, collection) => {
      if (error) return callback(error);
      collection.find({}, { projection: { "_id": 0 } }).sort({ "endpoint.datasets.0.label": 1, "endpoint.uri": 1 }).toArray()
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  getEndpointView(epUri, callback) {
    this.getCollection('epview', (error, collection) => {
      if (error) return callback(error);
      collection.find({ "endpoint.uri": epUri }, { projection: { "_id": 0 } }).toArray()
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  autocomplete(query, callback) {
    this.getCollection('endpoints', (error, collection) => {
      if (error) return callback(error);
      collection.find({ $or: [{ 'datasets.label': { $regex: '.*' + query + '.*', $options: 'i' } }, { 'uri': { $regex: '.*' + query + '.*', $options: 'i' } }] }, { projection: { "_id": 0 } }).sort({ "datasets.0.label": 1, "uri": 1 }).toArray()
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  endpointsCount(callback) {
    this.getCollection('endpoints', (error, collection) => {
      if (error) return callback(error);
      collection.countDocuments({})
        .then(count => callback(null, count))
        .catch(err => callback(err));
    });
  }

  endpointsList(callback) {
    this.getCollection('endpoints', (error, collection) => {
      if (error) return callback(error);
      collection.find({}, { projection: { "_id": 0 } }).sort({ "uri": 1 }).toArray()
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  getLastUpdate(callback) {
    this.getCollection('atasks_agg', (error, collection) => {
      if (error) return callback(error);
      collection.find({}, { projection: { "lastUpdate": 1 } }).sort({ "lastUpdate": -1 }).limit(1).toArray()
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  getIndex(callback) {
    this.getCollection('index', (error, collection) => {
      if (error) return callback(error);
      collection.findOne({})
        .then(results => callback(null, results))
        .catch(err => callback(err));
    });
  }

  getAMonths(callback) {
    this.getCollection('amonths', (error, collection) => {
      if (error) return callback(error);
      collection.find({}).sort({ "date": 1 }).toArray()
        .then(months => {
          // transform the months view into d3js expected format
          var valZeroFive = [];
          var valfiveSeventyfive = [];
          var valseventyfiveNintyfive = [];
          var valnintyfiveNintynine = [];
          var valnintynineHundred = [];
          for (var i in months) {
            var d = Date.parse(months[i].date);
            valZeroFive.push([d, months[i].zeroFive]);
            valfiveSeventyfive.push([d, months[i].fiveSeventyfive]);
            valseventyfiveNintyfive.push([d, months[i].seventyfiveNintyfive]);
            valnintyfiveNintynine.push([d, months[i].nintyfiveNintynine]);
            valnintynineHundred.push([d, months[i].nintynineHundred]);
          }
          var res = [
            { "key": "0-5", "index": 1, "values": valZeroFive },
            { "key": "5-75", "index": 2, "values": valfiveSeventyfive },
            { "key": "75-95", "index": 3, "values": valseventyfiveNintyfive },
            { "key": "95-99", "index": 4, "values": valnintyfiveNintynine },
            { "key": "99-100", "index": 5, "values": valnintynineHundred }
          ];
          callback(null, res)
        })
        .catch(err => callback(err));
    });
  }

  getLastTenPerformanceMedian(uri, callback) {
    this.getCollection('ptasks', (error, collection) => {
      if (error) return callback(error);
      collection.find({ "endpointResult.endpoint.uri": uri })
        .sort({ "endpointResult.end": -1 })
        .limit(10)
        .toArray()
        .then(results => {
          var ret = {};
          for (var i = 0; i < results.length; i++) {
            var obj = results[i];
            for (var x in obj.results) {
              var coldKey = x + '_cold';
              var warmKey = x + '_warm';
              if (!ret[coldKey]) ret[coldKey] = [];
              if (!ret[warmKey]) ret[warmKey] = [];
              ret[warmKey].push(obj.results[x].warm.exectime);
              ret[coldKey].push(obj.results[x].cold.exectime);
            }
          }

          // calculate median
          for (var y in ret) {
            ret[y] = median(ret[y]) / 1000; // 1000 is for millisceonds to seconds
          }

          callback(null, ret);
        })
        .catch(err => callback(err));
    });
  }

  getLatestDisco(uri, callback) {
    this.getCollection('dtasks', (error, collection) => {
      if (error) return callback(error);
      collection.find({ "endpointResult.endpoint.uri": uri })
        .sort({ "endpointResult.end": -1 })
        .limit(1)
        .toArray()
        .then(result => callback(null, result))
        .catch(err => callback(err));
    });
  }
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

exports.MongoDBProvider = MongoDBProvider;
