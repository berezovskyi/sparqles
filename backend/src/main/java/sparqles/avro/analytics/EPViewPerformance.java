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
public class EPViewPerformance extends org.apache.avro.specific.SpecificRecordBase
    implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 5634356004997893572L;

  public static final org.apache.avro.Schema SCHEMA$ =
      new org.apache.avro.Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"EPViewPerformance\",\"namespace\":\"sparqles.avro.analytics\",\"fields\":[{\"name\":\"threshold\",\"type\":\"long\"},{\"name\":\"ask\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"EPViewPerformanceData\",\"fields\":[{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"color\",\"type\":\"string\"},{\"name\":\"data\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"EPViewPerformanceDataValues\",\"fields\":[{\"name\":\"label\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"double\"},{\"name\":\"exception\",\"type\":[\"string\",\"null\"]}]}}}]}}},{\"name\":\"join\",\"type\":{\"type\":\"array\",\"items\":\"EPViewPerformanceData\"}}]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<EPViewPerformance> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<EPViewPerformance> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   *
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<EPViewPerformance> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   *
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<EPViewPerformance> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link
   * SchemaStore}.
   *
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<EPViewPerformance> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this EPViewPerformance to a ByteBuffer.
   *
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a EPViewPerformance from a ByteBuffer.
   *
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a EPViewPerformance instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of
   *     this class
   */
  public static EPViewPerformance fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private long threshold;
  private java.util.List<sparqles.avro.analytics.EPViewPerformanceData> ask;
  private java.util.List<sparqles.avro.analytics.EPViewPerformanceData> join;

  /**
   * Default constructor. Note that this does not initialize fields to their default values from the
   * schema. If that is desired then one should use <code>newBuilder()</code>.
   */
  public EPViewPerformance() {}

  /**
   * All-args constructor.
   *
   * @param threshold The new value for threshold
   * @param ask The new value for ask
   * @param join The new value for join
   */
  public EPViewPerformance(
      java.lang.Long threshold,
      java.util.List<sparqles.avro.analytics.EPViewPerformanceData> ask,
      java.util.List<sparqles.avro.analytics.EPViewPerformanceData> join) {
    this.threshold = threshold;
    this.ask = ask;
    this.join = join;
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
        return threshold;
      case 1:
        return ask;
      case 2:
        return join;
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
        threshold = (java.lang.Long) value$;
        break;
      case 1:
        ask = (java.util.List<sparqles.avro.analytics.EPViewPerformanceData>) value$;
        break;
      case 2:
        join = (java.util.List<sparqles.avro.analytics.EPViewPerformanceData>) value$;
        break;
      default:
        throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'threshold' field.
   *
   * @return The value of the 'threshold' field.
   */
  public long getThreshold() {
    return threshold;
  }

  /**
   * Sets the value of the 'threshold' field.
   *
   * @param value the value to set.
   */
  public void setThreshold(long value) {
    this.threshold = value;
  }

  /**
   * Gets the value of the 'ask' field.
   *
   * @return The value of the 'ask' field.
   */
  public java.util.List<sparqles.avro.analytics.EPViewPerformanceData> getAsk() {
    return ask;
  }

  /**
   * Sets the value of the 'ask' field.
   *
   * @param value the value to set.
   */
  public void setAsk(java.util.List<sparqles.avro.analytics.EPViewPerformanceData> value) {
    this.ask = value;
  }

  /**
   * Gets the value of the 'join' field.
   *
   * @return The value of the 'join' field.
   */
  public java.util.List<sparqles.avro.analytics.EPViewPerformanceData> getJoin() {
    return join;
  }

  /**
   * Sets the value of the 'join' field.
   *
   * @param value the value to set.
   */
  public void setJoin(java.util.List<sparqles.avro.analytics.EPViewPerformanceData> value) {
    this.join = value;
  }

  /**
   * Creates a new EPViewPerformance RecordBuilder.
   *
   * @return A new EPViewPerformance RecordBuilder
   */
  public static sparqles.avro.analytics.EPViewPerformance.Builder newBuilder() {
    return new sparqles.avro.analytics.EPViewPerformance.Builder();
  }

  /**
   * Creates a new EPViewPerformance RecordBuilder by copying an existing Builder.
   *
   * @param other The existing builder to copy.
   * @return A new EPViewPerformance RecordBuilder
   */
  public static sparqles.avro.analytics.EPViewPerformance.Builder newBuilder(
      sparqles.avro.analytics.EPViewPerformance.Builder other) {
    if (other == null) {
      return new sparqles.avro.analytics.EPViewPerformance.Builder();
    } else {
      return new sparqles.avro.analytics.EPViewPerformance.Builder(other);
    }
  }

  /**
   * Creates a new EPViewPerformance RecordBuilder by copying an existing EPViewPerformance
   * instance.
   *
   * @param other The existing instance to copy.
   * @return A new EPViewPerformance RecordBuilder
   */
  public static sparqles.avro.analytics.EPViewPerformance.Builder newBuilder(
      sparqles.avro.analytics.EPViewPerformance other) {
    if (other == null) {
      return new sparqles.avro.analytics.EPViewPerformance.Builder();
    } else {
      return new sparqles.avro.analytics.EPViewPerformance.Builder(other);
    }
  }

  /** RecordBuilder for EPViewPerformance instances. */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder
      extends org.apache.avro.specific.SpecificRecordBuilderBase<EPViewPerformance>
      implements org.apache.avro.data.RecordBuilder<EPViewPerformance> {

    private long threshold;
    private java.util.List<sparqles.avro.analytics.EPViewPerformanceData> ask;
    private java.util.List<sparqles.avro.analytics.EPViewPerformanceData> join;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     *
     * @param other The existing Builder to copy.
     */
    private Builder(sparqles.avro.analytics.EPViewPerformance.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.threshold)) {
        this.threshold = data().deepCopy(fields()[0].schema(), other.threshold);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.ask)) {
        this.ask = data().deepCopy(fields()[1].schema(), other.ask);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.join)) {
        this.join = data().deepCopy(fields()[2].schema(), other.join);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing EPViewPerformance instance
     *
     * @param other The existing instance to copy.
     */
    private Builder(sparqles.avro.analytics.EPViewPerformance other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.threshold)) {
        this.threshold = data().deepCopy(fields()[0].schema(), other.threshold);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.ask)) {
        this.ask = data().deepCopy(fields()[1].schema(), other.ask);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.join)) {
        this.join = data().deepCopy(fields()[2].schema(), other.join);
        fieldSetFlags()[2] = true;
      }
    }

    /**
     * Gets the value of the 'threshold' field.
     *
     * @return The value.
     */
    public long getThreshold() {
      return threshold;
    }

    /**
     * Sets the value of the 'threshold' field.
     *
     * @param value The value of 'threshold'.
     * @return This builder.
     */
    public sparqles.avro.analytics.EPViewPerformance.Builder setThreshold(long value) {
      validate(fields()[0], value);
      this.threshold = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
     * Checks whether the 'threshold' field has been set.
     *
     * @return True if the 'threshold' field has been set, false otherwise.
     */
    public boolean hasThreshold() {
      return fieldSetFlags()[0];
    }

    /**
     * Clears the value of the 'threshold' field.
     *
     * @return This builder.
     */
    public sparqles.avro.analytics.EPViewPerformance.Builder clearThreshold() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
     * Gets the value of the 'ask' field.
     *
     * @return The value.
     */
    public java.util.List<sparqles.avro.analytics.EPViewPerformanceData> getAsk() {
      return ask;
    }

    /**
     * Sets the value of the 'ask' field.
     *
     * @param value The value of 'ask'.
     * @return This builder.
     */
    public sparqles.avro.analytics.EPViewPerformance.Builder setAsk(
        java.util.List<sparqles.avro.analytics.EPViewPerformanceData> value) {
      validate(fields()[1], value);
      this.ask = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
     * Checks whether the 'ask' field has been set.
     *
     * @return True if the 'ask' field has been set, false otherwise.
     */
    public boolean hasAsk() {
      return fieldSetFlags()[1];
    }

    /**
     * Clears the value of the 'ask' field.
     *
     * @return This builder.
     */
    public sparqles.avro.analytics.EPViewPerformance.Builder clearAsk() {
      ask = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
     * Gets the value of the 'join' field.
     *
     * @return The value.
     */
    public java.util.List<sparqles.avro.analytics.EPViewPerformanceData> getJoin() {
      return join;
    }

    /**
     * Sets the value of the 'join' field.
     *
     * @param value The value of 'join'.
     * @return This builder.
     */
    public sparqles.avro.analytics.EPViewPerformance.Builder setJoin(
        java.util.List<sparqles.avro.analytics.EPViewPerformanceData> value) {
      validate(fields()[2], value);
      this.join = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
     * Checks whether the 'join' field has been set.
     *
     * @return True if the 'join' field has been set, false otherwise.
     */
    public boolean hasJoin() {
      return fieldSetFlags()[2];
    }

    /**
     * Clears the value of the 'join' field.
     *
     * @return This builder.
     */
    public sparqles.avro.analytics.EPViewPerformance.Builder clearJoin() {
      join = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EPViewPerformance build() {
      try {
        EPViewPerformance record = new EPViewPerformance();
        record.threshold =
            fieldSetFlags()[0] ? this.threshold : (java.lang.Long) defaultValue(fields()[0]);
        record.ask =
            fieldSetFlags()[1]
                ? this.ask
                : (java.util.List<sparqles.avro.analytics.EPViewPerformanceData>)
                    defaultValue(fields()[1]);
        record.join =
            fieldSetFlags()[2]
                ? this.join
                : (java.util.List<sparqles.avro.analytics.EPViewPerformanceData>)
                    defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<EPViewPerformance> WRITER$ =
      (org.apache.avro.io.DatumWriter<EPViewPerformance>) MODEL$.createDatumWriter(SCHEMA$);

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<EPViewPerformance> READER$ =
      (org.apache.avro.io.DatumReader<EPViewPerformance>) MODEL$.createDatumReader(SCHEMA$);

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
    out.writeLong(this.threshold);

    long size0 = this.ask.size();
    out.writeArrayStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (sparqles.avro.analytics.EPViewPerformanceData e0 : this.ask) {
      actualSize0++;
      out.startItem();
      e0.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException(
          "Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");

    long size1 = this.join.size();
    out.writeArrayStart();
    out.setItemCount(size1);
    long actualSize1 = 0;
    for (sparqles.avro.analytics.EPViewPerformanceData e1 : this.join) {
      actualSize1++;
      out.startItem();
      e1.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize1 != size1)
      throw new java.util.ConcurrentModificationException(
          "Array-size written was " + size1 + ", but element count was " + actualSize1 + ".");
  }

  @Override
  public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.threshold = in.readLong();

      long size0 = in.readArrayStart();
      java.util.List<sparqles.avro.analytics.EPViewPerformanceData> a0 = this.ask;
      if (a0 == null) {
        a0 =
            new SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData>(
                (int) size0, SCHEMA$.getField("ask").schema());
        this.ask = a0;
      } else a0.clear();
      SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData> ga0 =
          (a0 instanceof SpecificData.Array
              ? (SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData>) a0
              : null);
      for (; 0 < size0; size0 = in.arrayNext()) {
        for (; size0 != 0; size0--) {
          sparqles.avro.analytics.EPViewPerformanceData e0 = (ga0 != null ? ga0.peek() : null);
          if (e0 == null) {
            e0 = new sparqles.avro.analytics.EPViewPerformanceData();
          }
          e0.customDecode(in);
          a0.add(e0);
        }
      }

      long size1 = in.readArrayStart();
      java.util.List<sparqles.avro.analytics.EPViewPerformanceData> a1 = this.join;
      if (a1 == null) {
        a1 =
            new SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData>(
                (int) size1, SCHEMA$.getField("join").schema());
        this.join = a1;
      } else a1.clear();
      SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData> ga1 =
          (a1 instanceof SpecificData.Array
              ? (SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData>) a1
              : null);
      for (; 0 < size1; size1 = in.arrayNext()) {
        for (; size1 != 0; size1--) {
          sparqles.avro.analytics.EPViewPerformanceData e1 = (ga1 != null ? ga1.peek() : null);
          if (e1 == null) {
            e1 = new sparqles.avro.analytics.EPViewPerformanceData();
          }
          e1.customDecode(in);
          a1.add(e1);
        }
      }

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
          case 0:
            this.threshold = in.readLong();
            break;

          case 1:
            long size0 = in.readArrayStart();
            java.util.List<sparqles.avro.analytics.EPViewPerformanceData> a0 = this.ask;
            if (a0 == null) {
              a0 =
                  new SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData>(
                      (int) size0, SCHEMA$.getField("ask").schema());
              this.ask = a0;
            } else a0.clear();
            SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData> ga0 =
                (a0 instanceof SpecificData.Array
                    ? (SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData>) a0
                    : null);
            for (; 0 < size0; size0 = in.arrayNext()) {
              for (; size0 != 0; size0--) {
                sparqles.avro.analytics.EPViewPerformanceData e0 =
                    (ga0 != null ? ga0.peek() : null);
                if (e0 == null) {
                  e0 = new sparqles.avro.analytics.EPViewPerformanceData();
                }
                e0.customDecode(in);
                a0.add(e0);
              }
            }
            break;

          case 2:
            long size1 = in.readArrayStart();
            java.util.List<sparqles.avro.analytics.EPViewPerformanceData> a1 = this.join;
            if (a1 == null) {
              a1 =
                  new SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData>(
                      (int) size1, SCHEMA$.getField("join").schema());
              this.join = a1;
            } else a1.clear();
            SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData> ga1 =
                (a1 instanceof SpecificData.Array
                    ? (SpecificData.Array<sparqles.avro.analytics.EPViewPerformanceData>) a1
                    : null);
            for (; 0 < size1; size1 = in.arrayNext()) {
              for (; size1 != 0; size1--) {
                sparqles.avro.analytics.EPViewPerformanceData e1 =
                    (ga1 != null ? ga1.peek() : null);
                if (e1 == null) {
                  e1 = new sparqles.avro.analytics.EPViewPerformanceData();
                }
                e1.customDecode(in);
                a1.add(e1);
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
