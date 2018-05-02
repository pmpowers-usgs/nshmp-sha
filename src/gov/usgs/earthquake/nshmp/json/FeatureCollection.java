package gov.usgs.earthquake.nshmp.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import gov.usgs.earthquake.nshmp.geo.Location;
import gov.usgs.earthquake.nshmp.geo.LocationList;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Create a GeoJson {@code FeatureCollection}. See
 *    {@link #FeatureCollection(List)} for an example. 
 *  <br><br>
 * 
 * A GeoJson {@code FeatureCollection} is a GeoJson object with {@link #type}
 *    "FeatureCollection" and a single member {@link #features}. The
 *    {@link #features} member is a {@code List} of {@link Feature}s containing
 *    a {@link Geometry} of {@link Point}(s) and/or {@link Polygon}(s). 
 * <br><br>
 * 
 * The {@code List} of {@link Feature}s can be a mix of
 *    {@link Point}(s) and {@link Polygon}(s). 
 * <br><br>
 *
 * Convenience methods are added to easily read and write GeoJson files:
 *    <ul>
 *      <li> Write to a file: {@link FeatureCollection#write(Path)} </li>
 *      <li> Read a file: {@link FeatureCollection#read(InputStreamReader)} </li>
 *    </ul>
 * <br><br> 
 * 
 * A {@link Builder} is supplied for ease of adding {@link Feature}s and
 *    creating a {@link Geometry}: 
 *    <ul> 
 *      <li> {@link Builder#add(Feature)} </li> 
 *      <li> {@link Builder#createPoint(Location, Properties)} </li> 
 *      <li> {@link Builder#createPoint(double, double, Properties)} </li> 
 *      <li> {@link Builder#createPolygon(LocationList, Properties)} </li> 
 *    </ul> 
 *    See {@link Builder} for example.
 * 
 * @author Brandon Clayton
 */
public class FeatureCollection {
  /** The {@link GeoJsonType} of GeoJson object: FeatureCollection */
  public String type;
  /** The {@code List} of {@link Feature}s. */
  public List<Feature> features;

  /**
   * Return a new instance of a GeoJson {@code FeatureCollection}
   *    using the {@link #builder()}. 
   */
  private FeatureCollection(Builder builder) {
    this.type = GeoJsonType.FEATURE_COLLECTION.toUpperCamelCase();
    this.features = builder.features;
  }

  /**
   * Read in a GeoJson {@code FeatureCollection} from a
   *    {@code InputStreamReader}. 
   * <br><br>
   * 
   * Example:
   * 
   * <pre>
   * {@code
   *   String urlStr = "url of GeoJson FeatureCollection file";
   *   URL url = new URL(urlStr);
   *   InputStreamReader reader = new InputStreamReader(url.openStream());
   *   FeatureCollection fc = FeatureCollection.read(reader);
   * 
   *   Feature singleFeature = fc.features.get(0);
   *   Point point = (Point) singleFeature.geometry;
   *   double[] coords = point.coordinates;
   *   Properties properties = Properties.builder()
   *       .putAll(singleFeature.properties)
   *       .build();
   * }
   * </pre>
   * 
   * @param reader The {@code InputStreamReader}
   * @return A new instance of a {@code FeatureCollection}.
   */
  public static FeatureCollection read(InputStreamReader reader) {
    checkNotNull(reader, "Input stream cannot be null");
    return Util.GSON.fromJson(reader, FeatureCollection.class);
  }

  /**
   * Write a {@code FeatureCollection} to a file.
   * <br><br>
   * 
   * Example:
   * <pre>
   * {@code 
   *  Properties properties = Properties.builder()
   *      .title("Title")
   *      .id("id")
   *      .build();
   *  FeatureCollection fc = FeatureCollection.builder()
   *      .createPoint(40, -120, properties)
   *      .build();
   *  Path out = Paths.get("etc").resolve("test.geojson");
   *  fc.write(out);
   * }
   * </pre>
   *
   * @param out The {@code Path} to write the file.
   * @throws IOException 
   */
  public void write(Path out) throws IOException {
    checkNotNull(out, "Path cannot be null");
    String json = this.toJsonString();
    Files.write(out, json.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Return a {@code String} in JSON format.
   */
  public String toJsonString() {
    return Util.cleanPoly(Util.GSON.toJson(this));
  }

  /**
   * Return a new instance of {@link Builder}.
   * @return New {@link Builder}.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Convenience builder to build a new instance of a {@link FeatureCollection}.
   * <br><br>
   * 
   * Easily add {@link Feature}s to a {@code List} by: 
   *    <ul> 
   *      <li> {@link Builder#add(Feature)} </li> 
   *      <li> {@link Builder#createPoint(Location, Properties)} </li> 
   *      <li> {@link Builder#createPoint(double, double, Properties)} </li> 
   *      <li> {@link Builder#createPolygon(LocationList, Properties)} </li> 
   *    </ul>
   * <br><br>
   * 
   * Example:
   * <pre>
   * {@code
   *   Properties properties = Properties.builder()
   *       .title("Golden")
   *       .id("golden")
   *       .build();
   *   FeatureCollection fc = FeatureCollection.builder()
   *       .createPoint(39.75, -105, properties)
   *       .build();
   * }
   * </pre>
   * 
   * @author Brandon Clayton
   */
  public static class Builder {
    private List<Feature> features = new ArrayList<>();

    private Builder() {}

    /**
     * Return a new instance of a {@link FeatureCollection}.
     * @return New {@link FeatureCollection}.
     */
    public FeatureCollection build() {
      checkState(!features.isEmpty(), "List of features cannot be empty");
      return new FeatureCollection(this);
    }

    /**
     * Add a {@link Feature} to the {@link FeatureCollection#features}
     *    {@code List}.
     * @param feature The {@code Feature} to add.
     * @return Return the {@code Builder} to make chainable.
     */
    public Builder add(Feature feature) {
      checkNotNull(feature, "A feature cannot be null");
      this.features.add(feature);
      return this;
    }

    /**
     * Add a {@link Feature} with {@link Geometry} of {@link Point} to
     *    the {@link FeatureCollection#features} {@code List}.
     * @param loc The {@link Location} of the point.
     * @param properties The {@link Properties} of the point.
     * @return Return the {@code Builder} to make chainable.
     */
    public Builder createPoint(Location loc, Properties properties) {
      this.features.add(Feature.createPoint(loc, properties));
      return this;
    }

    /**
     * Add a {@link Feature} with {@link Geometry} of {@link Point} to
     *    the {@link FeatureCollection#features} {@code List}.
     * @param latitude The latitude of the point.
     * @param longitude The longitude of the point.
     * @param properties The {@link Properties} of the point.
     * @return Return the {@code Builder} to make chainable.
     */
    public Builder createPoint(double latitude, double longitude, Properties properties) {
      this.features.add(Feature.createPoint(latitude, longitude, properties));
      return this;
    }

    /**
     * Add a {@link Feature} with {@link Geometry} of {@link Polygon}
     *    to the {@link FeatureCollection#features} {@code List}.
     * @param locs The {@link LocationList} of the polygon.
     * @param properties The {@link Properties} of the polygon.
     * @return Return the {@code Builder} to make chainable.
     */
    public Builder createPolygon(LocationList locs, Properties properties) {
      this.features.add(Feature.createPolygon(locs, properties));
      return this;
    }
  }

}
