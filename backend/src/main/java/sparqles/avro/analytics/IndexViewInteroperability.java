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

@org.apache.avro.specific.AvroGenerated
public class IndexViewInteroperability extends org.apache.avro.specific.SpecificRecordBase
    implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 9215712904603231389L;

  public static final org.apache.avro.Schema SCHEMA$ =
      new org.apache.avro.Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"IndexViewInteroperability\",\"namespace\":\"sparqles.avro.analytics\",\"fields\":[{\"name\":\"data\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"IndexViewInterData\",\"fields\":[{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"color\",\"type\":\"string\"},{\"name\":\"data\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"IndexViewInterDataValues\",\"fields\":[{\"name\":\"label\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"double\"}]}}}]}}}]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<IndexViewInteroperability> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<IndexViewInteroperability> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   *
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<IndexViewInteroperability> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   *
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<IndexViewInteroperability> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link
   * SchemaStore}.
   *
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<IndexViewInteroperability> createDecoder(
      SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this IndexViewInteroperability to a ByteBuffer.
   *
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a IndexViewInteroperability from a ByteBuffer.
   *
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a IndexViewInteroperability instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of
   *     this class
   */
  public static IndexViewInteroperability fromByteBuffer(java.nio.ByteBuffer b)
      throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.util.List<sparqles.avro.analytics.IndexViewInterData> data;

  /**
   * Default constructor. Note that this does not initialize fields to their default values from the
   * schema. If that is desired then one should use <code>newBuilder()</code>.
   */
  public IndexViewInteroperability() {}

  /**
   * All-args constructor.
   *
   * @param data The new value for data
   */
  public IndexViewInteroperability(
      java.util.List<sparqles.avro.analytics.IndexViewInterData> data) {
    this.data = data;
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
        return data;
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
        data = (java.util.List<sparqles.avro.analytics.IndexViewInterData>) value$;
        break;
      default:
        throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'data' field.
   *
   * @return The value of the 'data' field.
   */
  public java.util.List<sparqles.avro.analytics.IndexViewInterData> getData() {
    return data;
  }

  /**
   * Sets the value of the 'data' field.
   *
   * @param value the value to set.
   */
  public void setData(java.util.List<sparqles.avro.analytics.IndexViewInterData> value) {
    this.data = value;
  }

  /**
   * Creates a new IndexViewInteroperability RecordBuilder.
   *
   * @return A new IndexViewInteroperability RecordBuilder
   */
  public static sparqles.avro.analytics.IndexViewInteroperability.Builder newBuilder() {
    return new sparqles.avro.analytics.IndexViewInteroperability.Builder();
  }

  /**
   * Creates a new IndexViewInteroperability RecordBuilder by copying an existing Builder.
   *
   * @param other The existing builder to copy.
   * @return A new IndexViewInteroperability RecordBuilder
   */
  public static sparqles.avro.analytics.IndexViewInteroperability.Builder newBuilder(
      sparqles.avro.analytics.IndexViewInteroperability.Builder other) {
    if (other == null) {
      return new sparqles.avro.analytics.IndexViewInteroperability.Builder();
    } else {
      return new sparqles.avro.analytics.IndexViewInteroperability.Builder(other);
    }
  }

  /**
   * Creates a new IndexViewInteroperability RecordBuilder by copying an existing
   * IndexViewInteroperability instance.
   *
   * @param other The existing instance to copy.
   * @return A new IndexViewInteroperability RecordBuilder
   */
  public static sparqles.avro.analytics.IndexViewInteroperability.Builder newBuilder(
      sparqles.avro.analytics.IndexViewInteroperability other) {
    if (other == null) {
      return new sparqles.avro.analytics.IndexViewInteroperability.Builder();
    } else {
      return new sparqles.avro.analytics.IndexViewInteroperability.Builder(other);
    }
  }

  /** RecordBuilder for IndexViewInteroperability instances. */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder
      extends org.apache.avro.specific.SpecificRecordBuilderBase<IndexViewInteroperability>
      implements org.apache.avro.data.RecordBuilder<IndexViewInteroperability> {

    private java.util.List<sparqles.avro.analytics.IndexViewInterData> data;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     *
     * @param other The existing Builder to copy.
     */
    private Builder(sparqles.avro.analytics.IndexViewInteroperability.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.data)) {
        this.data = data().deepCopy(fields()[0].schema(), other.data);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
    }

    /**
     * Creates a Builder by copying an existing IndexViewInteroperability instance
     *
     * @param other The existing instance to copy.
     */
    private Builder(sparqles.avro.analytics.IndexViewInteroperability other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.data)) {
        this.data = data().deepCopy(fields()[0].schema(), other.data);
        fieldSetFlags()[0] = true;
      }
    }

    /**
     * Gets the value of the 'data' field.
     *
     * @return The value.
     */
    public java.util.List<sparqles.avro.analytics.IndexViewInterData> getData() {
      return data;
    }

    /**
     * Sets the value of the 'data' field.
     *
     * @param value The value of 'data'.
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexViewInteroperability.Builder setData(
        java.util.List<sparqles.avro.analytics.IndexViewInterData> value) {
      validate(fields()[0], value);
      this.data = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
     * Checks whether the 'data' field has been set.
     *
     * @return True if the 'data' field has been set, false otherwise.
     */
    public boolean hasData() {
      return fieldSetFlags()[0];
    }

    /**
     * Clears the value of the 'data' field.
     *
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexViewInteroperability.Builder clearData() {
      data = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IndexViewInteroperability build() {
      try {
        IndexViewInteroperability record = new IndexViewInteroperability();
        record.data =
            fieldSetFlags()[0]
                ? this.data
                : (java.util.List<sparqles.avro.analytics.IndexViewInterData>)
                    defaultValue(fields()[0]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<IndexViewInteroperability> WRITER$ =
      (org.apache.avro.io.DatumWriter<IndexViewInteroperability>) MODEL$.createDatumWriter(SCHEMA$);

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<IndexViewInteroperability> READER$ =
      (org.apache.avro.io.DatumReader<IndexViewInteroperability>) MODEL$.createDatumReader(SCHEMA$);

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
    long size0 = this.data.size();
    out.writeArrayStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (sparqles.avro.analytics.IndexViewInterData e0 : this.data) {
      actualSize0++;
      out.startItem();
      e0.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException(
          "Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");
  }

  @Override
  public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      long size0 = in.readArrayStart();
      java.util.List<sparqles.avro.analytics.IndexViewInterData> a0 = this.data;
      if (a0 == null) {
        a0 =
            new SpecificData.Array<sparqles.avro.analytics.IndexViewInterData>(
                (int) size0, SCHEMA$.getField("data").schema());
        this.data = a0;
      } else a0.clear();
      SpecificData.Array<sparqles.avro.analytics.IndexViewInterData> ga0 =
          (a0 instanceof SpecificData.Array
              ? (SpecificData.Array<sparqles.avro.analytics.IndexViewInterData>) a0
              : null);
      for (; 0 < size0; size0 = in.arrayNext()) {
        for (; size0 != 0; size0--) {
          sparqles.avro.analytics.IndexViewInterData e0 = (ga0 != null ? ga0.peek() : null);
          if (e0 == null) {
            e0 = new sparqles.avro.analytics.IndexViewInterData();
          }
          e0.customDecode(in);
          a0.add(e0);
        }
      }

    } else {
      for (int i = 0; i < 1; i++) {
        switch (fieldOrder[i].pos()) {
          case 0:
            long size0 = in.readArrayStart();
            java.util.List<sparqles.avro.analytics.IndexViewInterData> a0 = this.data;
            if (a0 == null) {
              a0 =
                  new SpecificData.Array<sparqles.avro.analytics.IndexViewInterData>(
                      (int) size0, SCHEMA$.getField("data").schema());
              this.data = a0;
            } else a0.clear();
            SpecificData.Array<sparqles.avro.analytics.IndexViewInterData> ga0 =
                (a0 instanceof SpecificData.Array
                    ? (SpecificData.Array<sparqles.avro.analytics.IndexViewInterData>) a0
                    : null);
            for (; 0 < size0; size0 = in.arrayNext()) {
              for (; size0 != 0; size0--) {
                sparqles.avro.analytics.IndexViewInterData e0 = (ga0 != null ? ga0.peek() : null);
                if (e0 == null) {
                  e0 = new sparqles.avro.analytics.IndexViewInterData();
                }
                e0.customDecode(in);
                a0.add(e0);
              }
            }
            break;

          default:
            throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}
