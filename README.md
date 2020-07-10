# Third-party rates adapter

# Build and run
`sbt run` to run application and 
`sbt test` to run unit tests

# Technologies considerations

`scalaCache` is used as caching library and `scalaMemcached` as implementation. `Memcached` is chosen because it supports
distribution between hosts out of the box and the system could be easily scaled.

`scalaMock` is used as testing library for mocking objects, it has advanced support of scala features and nice documentation.

# Configuration considerations

Cache TTL is set to 295 sec (5 mins = 300 sec) to make low load on oneFrame service and to be sure that after cache evict
a new value will be stored in the cache and returned.
Depending on infrastructure configuration this parameter could be adjasted.

5 threads are chosen for http client because most of the time rates will be returned from cache and also client is used
 only for oneFrame service. 

# Error handling

Basic errors hierarchy was added. With the service development more complex hierarchy could be added and handled. Currently 
errors are grouped to business errors and all other (run time system errors).

## API
Get rates
/rates?from={currency}&to={currency}
```
curl -v -X GET 'localhost:8081/rates?from=USD&to=EUR'

{"from":"USD","to":"EUR","price":0.807565302453637,"timestamp":"2020-07-09T12:43:44.421+03:00"}
HTTP/1.1 200 OK

```

Evict cache
/rates/evict?from={currency}&to={currency}

```
curl -v -X PUT 'localhost:8081/rates/evict?from=USD&to=EUR'
HTTP/1.1 200 OK

```


