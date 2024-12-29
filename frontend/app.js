// Module dependencies.
var express = require('express')
var routes = require('./routes')
var user = require('./routes/user')
var http = require('http')
var path = require('path')
var fs = require('fs')
var favicon = require('serve-favicon')
var logger = require('morgan')
var bodyParser = require('body-parser')
var methodOverride = require('method-override')
var errorHandler = require('errorhandler')
var compression = require('compression')

var ConfigProvider = require('./configprovider').ConfigProvider
var MongoDBProvider = require('./mongodbprovider').MongoDBProvider
var mongoDBProvider = new MongoDBProvider(process.env.DBHOST || 'localhost', 27017)
var configApp = new ConfigProvider('./config.json')

var app = express()

// all environments
app.set('port', process.env.PORT || 3001)
app.use(favicon(path.join(__dirname, 'public', 'images', 'favicon.ico')))
app.use(compression())
app.use(express.static(path.join(__dirname, 'public')))
app.use(logger('dev'))
app.set('views', __dirname + '/views')
app.set('view engine', 'pug')
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))
app.use(methodOverride())

// set the default timezone to London
// process.env.TZ = 'Europe/London';
// process.env.TZ = 'Europe/Stockholm';
process.env.TZ = 'UTC'

// development only
if (app.get('env') === 'development') {
  app.use(errorHandler())
}

app.get('/', function (req, res) {
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    mongoDBProvider.getLastUpdate(function (error, lastUpdate) {
      mongoDBProvider.getIndex(function (error, index) {
        mongoDBProvider.getAMonths(function (error, amonths) {
          var indexInterop = JSON.parse(
            JSON.stringify(index.interoperability.data),
            function (k, v) {
              if (k === 'data') this.values = v
              else return v
            }
          )

          // var availability = amonths;
          var availability = null; // do not use amoths, use index.availability
          if (typeof availability != undefined && availability != null && availability.length > 0) {
            // TODO: stop this senseless renaming 'zeroFive' to '0-5' to '[0-5)'
            for (var i = 0; i < availability.length; i++) {
              if (availability[i]['key'] == '0-5') availability[i]['key'] = '[0-5)'
              if (availability[i]['key'] == '5-75') availability[i]['key'] = '[5-75)'
              if (availability[i]['key'] == '75-95') availability[i]['key'] = '[75-95)'
              if (availability[i]['key'] == '95-99') availability[i]['key'] = '[95-99)'
              if (availability[i]['key'] == '99-100') availability[i]['key'] = '[99-100]'
            }
          }
          else {
            availability = index.availability;
            for (var i = 0; i < availability.length; i++) {
              if (availability[i]['key'] == '[0;5]') availability[i]['key'] = '[0-5)'
              if (availability[i]['key'] == ']5;90]') availability[i]['key'] = '[5-90)'
              if (availability[i]['key'] == ']90;95]') availability[i]['key'] = '[90-95)'
              if (availability[i]['key'] == ']95;100]') availability[i]['key'] = '[95-100]'
            }
          }

          
          //amonths = JSON.parse(JSON.stringify(amonths).replace("\"0\-5\":", "\"[0-5[\":"));

          //PERFORMANCE
          mongoDBProvider.getCollection('ptasks_agg', function (error, coll) {
            coll
              .find({})
              .sort({ date_calculated: -1 })
              .limit(1)
              .toArray(function (err, docs) {
                var avgASKCold = docs.length > 0 ? (docs[0].askMedianCold / 1000) % 60 : 0
                var avgJOINCold = docs.length > 0 ? (docs[0].joinMeanCold / 1000) % 60 : 0
                var avgASKWarm = docs.length > 0 ? (docs[0].askMedianWarm / 1000) % 60 : 0
                var avgJOINWarm = docs.length > 0 ? (docs[0].joinMeanWarm / 1000) % 60 : 0

                // get the discoverability stats
                mongoDBProvider.getDiscoView(function (error, docs) {
                  //var lastUpdate=0;
                  var nbEndpointsVoID = 0
                  var nbEndpointsSD = 0
                  var nbEndpointsServerName = 0
                  var nbEndpointsTotal = 0
                  var nbEndpointsNoDesc = 0
                  for (i in docs) {
                    nbEndpointsTotal++
                    //if(docs[i].lastUpdate>lastUpdate) lastUpdate=docs[i].lastUpdate;
                    if (docs[i].VoID == true) nbEndpointsVoID++
                    if (docs[i].SD == true) nbEndpointsSD++
                    if (docs[i].VoID != true && docs[i].SD != true) nbEndpointsNoDesc++
                    if (docs[i].serverName.length > 0 && docs[i].serverName != 'missing')
                      nbEndpointsServerName++
                  }
                  res.render('content/index.pug', {
                    configInstanceTitle: configApp.get('configInstanceTitle'),
                    baseUri: configApp.get('baseUri'),
                    gitRepo: configApp.get('gitRepo'),
                    amonths: availability, // TODO: refactor naming
                    index: index,
                    indexInterop: indexInterop,
                    nbEndpointsSearch: nbEndpointsSearch,
                    nbEndpointsVoID: nbEndpointsVoID,
                    nbEndpointsSD: nbEndpointsSD,
                    nbEndpointsServerName: nbEndpointsServerName,
                    nbEndpointsTotal: nbEndpointsTotal,
                    nbEndpointsNoDesc: nbEndpointsNoDesc,
                    lastUpdate: lastUpdate.length > 0 ? lastUpdate[0].lastUpdate : 0,
                    perf: {
                      threshold: 10000 /*mostCommonThreshold[0]*/,
                      data: [
                        {
                          key: 'Cold Tests',
                          color: '#1f77b4',
                          values: [
                            { label: 'Median ASK', value: avgASKCold },
                            { label: 'Median JOIN', value: avgJOINCold },
                          ],
                        },
                        {
                          key: 'Warm Tests',
                          color: '#2ca02c',
                          values: [
                            { label: 'Median ASK', value: avgASKWarm },
                            { label: 'Median JOIN', value: avgJOINWarm },
                          ],
                        },
                      ],
                    },
                    configInterop: JSON.parse(fs.readFileSync('./texts/interoperability.json')),
                    configPerformance: JSON.parse(fs.readFileSync('./texts/performance.json')),
                    configDisco: JSON.parse(fs.readFileSync('./texts/discoverability.json')),
                    configAvailability: JSON.parse(fs.readFileSync('./texts/availability.json')),
                  })
                  /*
                                res.render('content/index.pug',{
                                  configInstanceTitle: configApp.get('configInstanceTitle'),
                                  amonths: amonths,
                                  index:index,
                                  indexInterop:indexInterop,
                                  nbEndpointsSearch: nbEndpointsSearch,
                                  lastUpdate: lastUpdate[0].lastUpdate,
                                  perf: {"threshold":10000 ,"data":[{"key": "Cold Tests","color": "#1f77b4","values": [{"label" : "Median ASK" ,"value" : avgASKCold },{"label" : "Median JOIN" ,"value" : avgJOINCold}]},{"key": "Warm Tests","color": "#2ca02c","values": [{"label" : "Median ASK" ,"value" : avgASKWarm} ,{"label" : "Median JOIN" ,"value" : avgJOINWarm}]}]},
                                  configInterop: JSON.parse(fs.readFileSync('./texts/interoperability.json')),
                                  configPerformance: JSON.parse(fs.readFileSync('./texts/performance.json')),
                                  configDisco: JSON.parse(fs.readFileSync('./texts/discoverability.json')),
                          configAvailability: JSON.parse(fs.readFileSync('./texts/availability.json'))
                */
                })
              })
          })
        })
      })
    })
  })
})

app.get('/api/endpointsAutoComplete', function (req, res) {
  mongoDBProvider.autocomplete(req.query.q, function (error, docs) {
    //for(i in docs)console.log(docs[i].uri);
    if (docs) {
      res.json(docs)
    } else res.end()
  })
})

app.get('/api/endpoint/list', function (req, res) {
  mongoDBProvider.endpointsList(function (error, docs) {
    //for(i in docs)console.log(docs[i].uri);
    if (docs) {
      res.header('Content-Type', 'application/json; charset=utf-8')
      res.json(docs)
    } else res.end()
  })
})

app.get('/api/endpoint/autocomplete', function (req, res) {
  mongoDBProvider.autocomplete(req.query.q, function (error, docs) {
    //for(i in docs)console.log(docs[i].uri);
    if (docs) {
      res.json(docs)
    } else res.end()
  })
})

app.get('/api/endpoint/info', function (req, res) {
  var uri = req.query.uri
  mongoDBProvider.getEndpointView(uri, function (error, docs) {
    if (docs) {
      res.json(docs)
    } else res.end()
  })
})

app.get('/api/availability', function (req, res) {
  mongoDBProvider.getAvailView(function (error, docs) {
    if (docs) {
      res.json(docs)
    } else res.end()
  })
})
app.get('/api/discoverability', function (req, res) {
  mongoDBProvider.getDiscoView(function (error, docs) {
    if (docs) {
      res.json(docs)
    } else res.end()
  })
})
app.get('/api/performance', function (req, res) {
  mongoDBProvider.getPerfView(function (error, docs) {
    if (docs) {
      res.json(docs)
    } else res.end()
  })
})
app.get('/api/interoperability', function (req, res) {
  mongoDBProvider.getInteropView(function (error, docs) {
    if (docs) {
      res.json(docs)
    } else res.end()
  })
})

app.get('/endpoint', function (req, res) {
  var uri = req.query.uri
  var ep = JSON.parse(fs.readFileSync('./examples/endpoint.json'))
  //console.log(req.param('uri'))
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    //TODO deal with no URI
    console.log(uri)
    mongoDBProvider.getEndpointView(uri, function (error, docs) {
      mongoDBProvider.getCollection('endpoints', function (error, collection) {
        collection.find({ uri: uri }).toArray(function (err, results) {
          // get last 10 performance median runs
          mongoDBProvider.getLastTenPerformanceMedian(uri, function (error, lastTenObj) {
            var perfParsed = JSON.parse(JSON.stringify(docs[0].performance), function (k, v) {
              if (k === 'data') this.values = v
              else return v
            })
            // do ASK COLD
            if (typeof perfParsed.ask[0] !== 'undefined' && perfParsed.ask[0] !== null) {
              perfParsed.ask[0].values.forEach(function (o) {
              o.value = lastTenObj['ASK' + o.label.toUpperCase() + '_cold']
              })
            }
            // do ASK WARM
            if (typeof perfParsed.ask[1] !== 'undefined' && perfParsed.ask[1] !== null) {
              perfParsed.ask[1].values.forEach(function (o) {
              o.value = lastTenObj['ASK' + o.label.toUpperCase() + '_warm']
              })
            }
            // do JOIN COLD
            if (typeof perfParsed.join[0] !== 'undefined' && perfParsed.join[0] !== null) {
              perfParsed.join[0].values.forEach(function (o) {
              o.value = lastTenObj['JOIN' + o.label.toUpperCase() + '_cold']
              })
            }
            // do JOIN WARM
            if (typeof perfParsed.join[1] !== 'undefined' && perfParsed.join[1] !== null) {
              perfParsed.join[1].values.forEach(function (o) {
              o.value = lastTenObj['JOIN' + o.label.toUpperCase() + '_warm']
              })
            }
            if (typeof docs[0].availability.data !== 'undefined' && docs[0].availability.data !== null) {
              docs[0].availability.data.values.forEach(function (value, index) {
              if (value.x == 1421625600000) {
                docs[0].availability.data.values.splice(index, 1)
              }
              })
            }

            mongoDBProvider.getLatestDisco(uri, function (error, latestDisco) {
              var SDDescription = [
                {
                  label: 'foo',
                  value: true,
                },
              ]
              var SDDescription = []
              var descriptionFiles = latestDisco[0].descriptionFiles
              for (var i = 0; i < descriptionFiles.length; i++) {
                var d = descriptionFiles[i]
                var name = d.Operation
                if (name == 'EPURL') name = 'HTTP Get'
                if (name == 'wellknown') name = '/.well-known/void'
                var preds = false
                // check if SPARQLDESCpreds object is empty or not
                if (Object.keys(d.SPARQLDESCpreds).length) {
                  preds = true
                }
                SDDescription.push({
                  label: name,
                  value: preds,
                })
              }

              docs[0].discoverability.SDDescription = SDDescription

              res.render('content/endpoint.pug', {
                configInstanceTitle: configApp.get('configInstanceTitle'),
                baseUri: configApp.get('baseUri'),
                gitRepo: configApp.get('gitRepo'),
                ep: ep,
                nbEndpointsSearch: nbEndpointsSearch,
                lastUpdate: uri,
                configInterop: JSON.parse(fs.readFileSync('./texts/interoperability.json')),
                configPerf: JSON.parse(fs.readFileSync('./texts/performance.json')),
                configDisco: JSON.parse(fs.readFileSync('./texts/discoverability.json')),
                epUri: uri,
                epDetails: /*docs[0].endpoint*/ results[0],
                epPerf: perfParsed,
                epAvail: docs[0].availability,
                epInterop: docs[0].interoperability,
                epDisco: docs[0].discoverability,
              })
            })
          })
        })
      })
    })
  })
})

app.get('/fix-encoding', function (req, res) {
  mongoDBProvider.getCollection('endpoints', function (error, coll) {
    coll.find({}).toArray(function (err, endpoints) {
      mongoDBProvider.getCollection('atasks_agg', function (error, taskColl) {
        for (var i in endpoints) {
          var endpoint = endpoints[i]
          taskColl.update(
            { 'endpoint.uri': endpoint.uri },
            { $set: { 'endpoint.datasets': endpoint.datasets } },
            function (err, result) { }
          )
        }
      })

      mongoDBProvider.getCollection('dtasks_agg', function (error, taskColl) {
        for (var i in endpoints) {
          var endpoint = endpoints[i]
          taskColl.update(
            { 'endpoint.uri': endpoint.uri },
            { $set: { 'endpoint.datasets': endpoint.datasets } },
            function (err, result) { }
          )
        }
      })

      mongoDBProvider.getCollection('ptasks_agg', function (error, taskColl) {
        for (var i in endpoints) {
          var endpoint = endpoints[i]
          taskColl.update(
            { 'endpoint.uri': endpoint.uri },
            { $set: { 'endpoint.datasets': endpoint.datasets } },
            function (err, result) { }
          )
        }
      })
    })
  })
})

app.get('/availability', function (req, res) {
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    mongoDBProvider.getAvailView(function (error, docs) {
      var lastUpdate = 0
      var nbEndpointsUp = 0
      console.log(error)
      for (i in docs) {
        if (docs[i].upNow == true) nbEndpointsUp++
        if (docs[i].lastUpdate > lastUpdate) lastUpdate = docs[i].lastUpdate
      }
      res.render('content/availability.pug', {
        configInstanceTitle: configApp.get('configInstanceTitle'),
        baseUri: configApp.get('baseUri'),
        gitRepo: configApp.get('gitRepo'),
        lastUpdate: new Date(lastUpdate).toUTCString(),
        nbEndpointsSearch: nbEndpointsSearch,
        atasks_agg: docs,
        nbEndpointsUp: nbEndpointsUp,
        nbEndpointsTotal: docs.length,
      })
    })
  })
})

app.get('/discoverability', function (req, res) {
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    mongoDBProvider.getDiscoView(function (error, docs) {
      var lastUpdate = 0
      var nbEndpointsVoID = 0
      var nbEndpointsSD = 0
      var nbEndpointsServerName = 0
      var nbEndpointsTotal = 0
      for (i in docs) {
        nbEndpointsTotal++
        if (docs[i].lastUpdate > lastUpdate) lastUpdate = docs[i].lastUpdate
        if (docs[i].VoID == true) nbEndpointsVoID++
        if (docs[i].SD == true) nbEndpointsSD++
        if (docs[i].serverName.length > 0 && docs[i].serverName != 'missing')
          nbEndpointsServerName++
      }
      res.render('content/discoverability.pug', {
        configInstanceTitle: configApp.get('configInstanceTitle'),
        baseUri: configApp.get('baseUri'),
        gitRepo: configApp.get('gitRepo'),
        lastUpdate: new Date(lastUpdate).toUTCString(),
        nbEndpointsSearch: nbEndpointsSearch,
        nbEndpointsVoID: nbEndpointsVoID,
        nbEndpointsSD: nbEndpointsSD,
        nbEndpointsServerName: nbEndpointsServerName,
        nbEndpointsTotal: nbEndpointsTotal,
        dtasks_agg: docs,
        configDisco: JSON.parse(fs.readFileSync('./texts/discoverability.json')),
      })
    })
  })
})

app.get('/performance', function (req, res) {
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    mongoDBProvider.getPerfView(function (error, docs) {
      var lastUpdate = 0
      var nbEndpointsWithThreshold = 0
      var nbEndpointsTotal = 0
      var thresholds = []
      for (i in docs) {
        if (docs[i].lastUpdate > lastUpdate) lastUpdate = docs[i].lastUpdate
        if (docs[i].threshold > 0 && docs[i].threshold % 100 == 0) {
          nbEndpointsWithThreshold++
          if (thresholds[docs[i].threshold]) thresholds[docs[i].threshold]++
          else thresholds[docs[i].threshold] = 1
        }
        if (docs[i].askMeanCold + docs[i].joinMeanCold > 0) nbEndpointsTotal++
      }
      var mostCommonThreshold = [0, 0]
      for (i in thresholds) {
        if (thresholds[i] > mostCommonThreshold[1]) {
          mostCommonThreshold[0] = i
          mostCommonThreshold[1] = thresholds[i]
        }
      }
      //console.log(mostCommonThreshold);
      res.render('content/performance.pug', {
        configInstanceTitle: configApp.get('configInstanceTitle'),
        baseUri: configApp.get('baseUri'),
        gitRepo: configApp.get('gitRepo'),
        lastUpdate: new Date(lastUpdate).toUTCString(),
        nbEndpointsSearch: nbEndpointsSearch,
        configPerformance: JSON.parse(fs.readFileSync('./texts/performance.json')),
        ptasks_agg: docs,
        nbEndpointsWithThreshold: nbEndpointsWithThreshold,
        nbEndpointsTotal: nbEndpointsTotal,
        mostCommonThreshold: mostCommonThreshold[0],
      })
    })
  })
})

app.get('/interoperability', function (req, res) {
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    var nbSPARQL1Features = 24
    var nbSPARQL11Features = 18
    mongoDBProvider.getInteropView(function (error, docs) {
      var lastUpdate = 0
      var nbCompliantSPARQL1Features = 0
      var nbFullCompliantSPARQL1Features = 0
      var nbCompliantSPARQL11Features = 0
      var nbEndpointsTotal = 0
      var nbFullCompliantSPARQL11Features = 0
      for (i in docs) {
        if (docs[i].nbCompliantSPARQL1Features + docs[i].nbCompliantSPARQL11Features > 0)
          nbEndpointsTotal++
        if (docs[i].nbCompliantSPARQL1Features > 0) {
          nbCompliantSPARQL1Features++
          if (docs[i].nbCompliantSPARQL1Features == nbSPARQL1Features)
            nbFullCompliantSPARQL1Features++
        }
        if (docs[i].nbCompliantSPARQL11Features > 0) {
          nbCompliantSPARQL11Features++
          if (docs[i].nbCompliantSPARQL11Features == nbSPARQL11Features)
            nbFullCompliantSPARQL11Features++
        }
        if (docs[i].lastUpdate > lastUpdate) lastUpdate = docs[i].lastUpdate
      }
      //console.log(nbCompliantSPARQL1Features+' - '+nbFullCompliantSPARQL1Features+' - '+nbCompliantSPARQL11Features+' - '+nbFullCompliantSPARQL11Features);
      res.render('content/interoperability.pug', {
        configInstanceTitle: configApp.get('configInstanceTitle'),
        baseUri: configApp.get('baseUri'),
        gitRepo: configApp.get('gitRepo'),
        lastUpdate: new Date(lastUpdate).toUTCString(),
        nbEndpointsSearch: nbEndpointsSearch,
        configInterop: JSON.parse(fs.readFileSync('./texts/interoperability.json')),
        nbSPARQL1Features: nbSPARQL1Features,
        nbSPARQL11Features: nbSPARQL11Features,
        nbCompliantSPARQL1Features: nbCompliantSPARQL1Features,
        nbFullCompliantSPARQL1Features: nbFullCompliantSPARQL1Features,
        nbCompliantSPARQL11Features: nbCompliantSPARQL11Features,
        nbFullCompliantSPARQL11Features: nbFullCompliantSPARQL11Features,
        ftasks_agg: docs,
        nbEndpointsTotal: nbEndpointsTotal,
      })
    })
  })
})

app.get('/data', function (req, res) {
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    var dir = '../dumps/' // data dir
    function bytesToSize(bytes) {
      if (bytes == 0) return '0 Byte'
      var k = 1000
      var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
      var i = Math.floor(Math.log(bytes) / Math.log(k))
      return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i]
    }
    var files = fs.readdirSync(dir).map(function (v) {
      return {
        name: v,
        time: fs.statSync(dir + v).mtime.getTime(),
        size: bytesToSize(fs.statSync(dir + v).size),
      }
    })
    res.render('content/data.pug', {
      configInstanceTitle: configApp.get('configInstanceTitle'),
      baseUri: configApp.get('baseUri'),
      gitRepo: configApp.get('gitRepo'),
      files: files,
      nbEndpointsSearch: nbEndpointsSearch,
    })
  })
})

app.use('/dumps', express.static('/dumps'));

app.get('/iswc2013', function (req, res) {
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    mongoDBProvider.autocomplete(req.query.q, function (error, docs) {
      res.render('content/iswc2013.pug', {
        configInstanceTitle: configApp.get('configInstanceTitle'),
        baseUri: configApp.get('baseUri'),
        gitRepo: configApp.get('gitRepo'),
        nbEndpointsSearch: nbEndpointsSearch,
      })
    })
  })
})

app.get('/api', function (req, res) {
  mongoDBProvider.endpointsCount(function (error, nbEndpointsSearch) {
    mongoDBProvider.autocomplete(req.query.q, function (error, docs) {
      res.render('content/api.pug', {
        configInstanceTitle: configApp.get('configInstanceTitle'),
        baseUri: configApp.get('baseUri'),
        gitRepo: configApp.get('gitRepo'),
        nbEndpointsSearch: nbEndpointsSearch,
      })
    })
  })
})

http.createServer(app).listen(app.get('port'), function () {
  console.log('Express server listening on port ' + app.get('port'))
})
