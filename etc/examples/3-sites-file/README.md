Example 3: Using a custom sites file
------------------------------------

__Working directory:__ `/path/to/nshmp-haz/etc/examples/3-sites-file`

To compute hazard at more than one site, one may supply a comma-delimited (\*.csv) or [GeoJSON](http://geojson.org) (\*.geojson) formatted site data file instead:

```Shell
hazard ../../peer/models/Set1-Case1  sites.csv config.json
```

or

```Shell
hazard ../../peer/models/Set1-Case1 sites.geojson config.json
```

The [site specification](https://github.com/usgs/nshmp-haz/wiki/Sites) wiki page provides details on the two file formats. Note that with either format, if the name of a site is supplied, it will be included in the first column of any output curve files.

Note that both formats ([CSV](sites.csv) and [GeoJSON](sites.geojson)) are elegantly rendered by GitHub.

#### Directory structure and output files

<pre style="background: #f7f7f7">
|- <a href="../../example_outputs/3-sites-file">3-sites-file/ </a>
|- config.json 
|- <a href="../../example_outputs/3-sites-file/curves">curves/ </a>
  |- HazadCalc.log 
  |- <a href="../../example_outputs/3-sites-file/curves/PGA">PGA/ </a>
    |- total.csv 
  |- <a href="../../example_outputs/3-sites-file/curves/SA0P2">SA0P2/ </a>
    |- total.csv 
  |- <a href="../../example_outputs/3-sites-file/curves/SA1P0">SA1P0/ </a>
    |- total.csv 
  |- config.json
|- sites.csv
|- sites.geojson
</pre>



#### Next: [Example 4 – A simple hazard map](../4-hazard-map)
