/**
 * Autogenerated by Avro
 *
 * <p>DO NOT EDIT DIRECTLY
 */
package sparqles.avro.analytics;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;

@org.apache.avro.specific.AvroGenerated
public class IndexAvailabilityDataPoint extends org.apache.avro.specific.SpecificRecordBase
    implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -3339399301666649668L;

  public static final org.apache.avro.Schema SCHEMA$ =
      new org.apache.avro.Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"IndexAvailabilityDataPoint\",\"namespace\":\"sparqles.avro.analytics\",\"fields\":[{\"name\":\"x\",\"type\":\"string\"},{\"name\":\"y\",\"type\":\"double\"}]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<IndexAvailabilityDataPoint> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<IndexAvailabilityDataPoint> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   *
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<IndexAvailabilityDataPoint> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   *
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<IndexAvailabilityDataPoint> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link
   * SchemaStore}.
   *
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<IndexAvailabilityDataPoint> createDecoder(
      SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this IndexAvailabilityDataPoint to a ByteBuffer.
   *
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a IndexAvailabilityDataPoint from a ByteBuffer.
   *
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a IndexAvailabilityDataPoint instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of
   *     this class
   */
  public static IndexAvailabilityDataPoint fromByteBuffer(java.nio.ByteBuffer b)
      throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence x;
  private double y;

  /**
   * Default constructor. Note that this does not initialize fields to their default values from the
   * schema. If that is desired then one should use <code>newBuilder()</code>.
   */
  public IndexAvailabilityDataPoint() {}

  /**
   * All-args constructor.
   *
   * @param x The new value for x
   * @param y The new value for y
   */
  public IndexAvailabilityDataPoint(java.lang.CharSequence x, java.lang.Double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() {
    return MODEL$;
  }

  @Override
  public org.apache.avro.Schema getSchema() {
    return SCHEMA$;
  }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
      case 0:
        return x;
      case 1:
        return y;
      default:
        throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value = "unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
      case 0:
        x = (java.lang.CharSequence) value$;
        break;
      case 1:
        y = (java.lang.Double) value$;
        break;
      default:
        throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'x' field.
   *
   * @return The value of the 'x' field.
   */
  public java.lang.CharSequence getX() {
    return x;
  }

  /**
   * Sets the value of the 'x' field.
   *
   * @param value the value to set.
   */
  public void setX(java.lang.CharSequence value) {
    this.x = value;
  }

  /**
   * Gets the value of the 'y' field.
   *
   * @return The value of the 'y' field.
   */
  public double getY() {
    return y;
  }

  /**
   * Sets the value of the 'y' field.
   *
   * @param value the value to set.
   */
  public void setY(double value) {
    this.y = value;
  }

  /**
   * Creates a new IndexAvailabilityDataPoint RecordBuilder.
   *
   * @return A new IndexAvailabilityDataPoint RecordBuilder
   */
  public static sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder newBuilder() {
    return new sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder();
  }

  /**
   * Creates a new IndexAvailabilityDataPoint RecordBuilder by copying an existing Builder.
   *
   * @param other The existing builder to copy.
   * @return A new IndexAvailabilityDataPoint RecordBuilder
   */
  public static sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder newBuilder(
      sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder other) {
    if (other == null) {
      return new sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder();
    } else {
      return new sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder(other);
    }
  }

  /**
   * Creates a new IndexAvailabilityDataPoint RecordBuilder by copying an existing
   * IndexAvailabilityDataPoint instance.
   *
   * @param other The existing instance to copy.
   * @return A new IndexAvailabilityDataPoint RecordBuilder
   */
  public static sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder newBuilder(
      sparqles.avro.analytics.IndexAvailabilityDataPoint other) {
    if (other == null) {
      return new sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder();
    } else {
      return new sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder(other);
    }
  }

  /** RecordBuilder for IndexAvailabilityDataPoint instances. */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder
      extends org.apache.avro.specific.SpecificRecordBuilderBase<IndexAvailabilityDataPoint>
      implements org.apache.avro.data.RecordBuilder<IndexAvailabilityDataPoint> {

    private java.lang.CharSequence x;
    private double y;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     *
     * @param other The existing Builder to copy.
     */
    private Builder(sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.x)) {
        this.x = data().deepCopy(fields()[0].schema(), other.x);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.y)) {
        this.y = data().deepCopy(fields()[1].schema(), other.y);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing IndexAvailabilityDataPoint instance
     *
     * @param other The existing instance to copy.
     */
    private Builder(sparqles.avro.analytics.IndexAvailabilityDataPoint other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.x)) {
        this.x = data().deepCopy(fields()[0].schema(), other.x);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.y)) {
        this.y = data().deepCopy(fields()[1].schema(), other.y);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Gets the value of the 'x' field.
     *
     * @return The value.
     */
    public java.lang.CharSequence getX() {
      return x;
    }

    /**
     * Sets the value of the 'x' field.
     *
     * @param value The value of 'x'.
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder setX(
        java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.x = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
     * Checks whether the 'x' field has been set.
     *
     * @return True if the 'x' field has been set, false otherwise.
     */
    public boolean hasX() {
      return fieldSetFlags()[0];
    }

    /**
     * Clears the value of the 'x' field.
     *
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder clearX() {
      x = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
     * Gets the value of the 'y' field.
     *
     * @return The value.
     */
    public double getY() {
      return y;
    }

    /**
     * Sets the value of the 'y' field.
     *
     * @param value The value of 'y'.
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder setY(double value) {
      validate(fields()[1], value);
      this.y = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
     * Checks whether the 'y' field has been set.
     *
     * @return True if the 'y' field has been set, false otherwise.
     */
    public boolean hasY() {
      return fieldSetFlags()[1];
    }

    /**
     * Clears the value of the 'y' field.
     *
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexAvailabilityDataPoint.Builder clearY() {
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IndexAvailabilityDataPoint build() {
      try {
        IndexAvailabilityDataPoint record = new IndexAvailabilityDataPoint();
        record.x = fieldSetFlags()[0] ? this.x : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.y = fieldSetFlags()[1] ? this.y : (java.lang.Double) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<IndexAvailabilityDataPoint> WRITER$ =
      (org.apache.avro.io.DatumWriter<IndexAvailabilityDataPoint>)
          MODEL$.createDatumWriter(SCHEMA$);

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<IndexAvailabilityDataPoint> READER$ =
      (org.apache.avro.io.DatumReader<IndexAvailabilityDataPoint>)
          MODEL$.createDatumReader(SCHEMA$);

  @Override
  public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override
  protected boolean hasCustomCoders() {
    return true;
  }

  @Override
  public void customEncode(org.apache.avro.io.Encoder out) throws java.io.IOException {
    out.writeString(this.x);

    out.writeDouble(this.y);
  }

  @Override
  public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.x = in.readString(this.x instanceof Utf8 ? (Utf8) this.x : null);

      this.y = in.readDouble();

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
          case 0:
            this.x = in.readString(this.x instanceof Utf8 ? (Utf8) this.x : null);
            break;

          case 1:
            this.y = in.readDouble();
            break;

          default:
            throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}
