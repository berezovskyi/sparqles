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
public class IndexViewCalculationDataValues extends org.apache.avro.specific.SpecificRecordBase
    implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 7990489637718673347L;

  public static final org.apache.avro.Schema SCHEMA$ =
      new org.apache.avro.Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"IndexViewCalculationDataValues\",\"namespace\":\"sparqles.avro.analytics\",\"fields\":[{\"name\":\"label\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"double\"}]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<IndexViewCalculationDataValues> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<IndexViewCalculationDataValues> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   *
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<IndexViewCalculationDataValues> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   *
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<IndexViewCalculationDataValues> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link
   * SchemaStore}.
   *
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<IndexViewCalculationDataValues> createDecoder(
      SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this IndexViewCalculationDataValues to a ByteBuffer.
   *
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a IndexViewCalculationDataValues from a ByteBuffer.
   *
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a IndexViewCalculationDataValues instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of
   *     this class
   */
  public static IndexViewCalculationDataValues fromByteBuffer(java.nio.ByteBuffer b)
      throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence label;
  private double value;

  /**
   * Default constructor. Note that this does not initialize fields to their default values from the
   * schema. If that is desired then one should use <code>newBuilder()</code>.
   */
  public IndexViewCalculationDataValues() {}

  /**
   * All-args constructor.
   *
   * @param label The new value for label
   * @param value The new value for value
   */
  public IndexViewCalculationDataValues(java.lang.CharSequence label, java.lang.Double value) {
    this.label = label;
    this.value = value;
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
        return label;
      case 1:
        return value;
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
        label = (java.lang.CharSequence) value$;
        break;
      case 1:
        value = (java.lang.Double) value$;
        break;
      default:
        throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'label' field.
   *
   * @return The value of the 'label' field.
   */
  public java.lang.CharSequence getLabel() {
    return label;
  }

  /**
   * Sets the value of the 'label' field.
   *
   * @param value the value to set.
   */
  public void setLabel(java.lang.CharSequence value) {
    this.label = value;
  }

  /**
   * Gets the value of the 'value' field.
   *
   * @return The value of the 'value' field.
   */
  public double getValue() {
    return value;
  }

  /**
   * Sets the value of the 'value' field.
   *
   * @param value the value to set.
   */
  public void setValue(double value) {
    this.value = value;
  }

  /**
   * Creates a new IndexViewCalculationDataValues RecordBuilder.
   *
   * @return A new IndexViewCalculationDataValues RecordBuilder
   */
  public static sparqles.avro.analytics.IndexViewCalculationDataValues.Builder newBuilder() {
    return new sparqles.avro.analytics.IndexViewCalculationDataValues.Builder();
  }

  /**
   * Creates a new IndexViewCalculationDataValues RecordBuilder by copying an existing Builder.
   *
   * @param other The existing builder to copy.
   * @return A new IndexViewCalculationDataValues RecordBuilder
   */
  public static sparqles.avro.analytics.IndexViewCalculationDataValues.Builder newBuilder(
      sparqles.avro.analytics.IndexViewCalculationDataValues.Builder other) {
    if (other == null) {
      return new sparqles.avro.analytics.IndexViewCalculationDataValues.Builder();
    } else {
      return new sparqles.avro.analytics.IndexViewCalculationDataValues.Builder(other);
    }
  }

  /**
   * Creates a new IndexViewCalculationDataValues RecordBuilder by copying an existing
   * IndexViewCalculationDataValues instance.
   *
   * @param other The existing instance to copy.
   * @return A new IndexViewCalculationDataValues RecordBuilder
   */
  public static sparqles.avro.analytics.IndexViewCalculationDataValues.Builder newBuilder(
      sparqles.avro.analytics.IndexViewCalculationDataValues other) {
    if (other == null) {
      return new sparqles.avro.analytics.IndexViewCalculationDataValues.Builder();
    } else {
      return new sparqles.avro.analytics.IndexViewCalculationDataValues.Builder(other);
    }
  }

  /** RecordBuilder for IndexViewCalculationDataValues instances. */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder
      extends org.apache.avro.specific.SpecificRecordBuilderBase<IndexViewCalculationDataValues>
      implements org.apache.avro.data.RecordBuilder<IndexViewCalculationDataValues> {

    private java.lang.CharSequence label;
    private double value;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     *
     * @param other The existing Builder to copy.
     */
    private Builder(sparqles.avro.analytics.IndexViewCalculationDataValues.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.label)) {
        this.label = data().deepCopy(fields()[0].schema(), other.label);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.value)) {
        this.value = data().deepCopy(fields()[1].schema(), other.value);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing IndexViewCalculationDataValues instance
     *
     * @param other The existing instance to copy.
     */
    private Builder(sparqles.avro.analytics.IndexViewCalculationDataValues other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.label)) {
        this.label = data().deepCopy(fields()[0].schema(), other.label);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.value)) {
        this.value = data().deepCopy(fields()[1].schema(), other.value);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Gets the value of the 'label' field.
     *
     * @return The value.
     */
    public java.lang.CharSequence getLabel() {
      return label;
    }

    /**
     * Sets the value of the 'label' field.
     *
     * @param value The value of 'label'.
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexViewCalculationDataValues.Builder setLabel(
        java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.label = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
     * Checks whether the 'label' field has been set.
     *
     * @return True if the 'label' field has been set, false otherwise.
     */
    public boolean hasLabel() {
      return fieldSetFlags()[0];
    }

    /**
     * Clears the value of the 'label' field.
     *
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexViewCalculationDataValues.Builder clearLabel() {
      label = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
     * Gets the value of the 'value' field.
     *
     * @return The value.
     */
    public double getValue() {
      return value;
    }

    /**
     * Sets the value of the 'value' field.
     *
     * @param value The value of 'value'.
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexViewCalculationDataValues.Builder setValue(double value) {
      validate(fields()[1], value);
      this.value = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
     * Checks whether the 'value' field has been set.
     *
     * @return True if the 'value' field has been set, false otherwise.
     */
    public boolean hasValue() {
      return fieldSetFlags()[1];
    }

    /**
     * Clears the value of the 'value' field.
     *
     * @return This builder.
     */
    public sparqles.avro.analytics.IndexViewCalculationDataValues.Builder clearValue() {
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IndexViewCalculationDataValues build() {
      try {
        IndexViewCalculationDataValues record = new IndexViewCalculationDataValues();
        record.label =
            fieldSetFlags()[0] ? this.label : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.value =
            fieldSetFlags()[1] ? this.value : (java.lang.Double) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<IndexViewCalculationDataValues> WRITER$ =
      (org.apache.avro.io.DatumWriter<IndexViewCalculationDataValues>)
          MODEL$.createDatumWriter(SCHEMA$);

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<IndexViewCalculationDataValues> READER$ =
      (org.apache.avro.io.DatumReader<IndexViewCalculationDataValues>)
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
    out.writeString(this.label);

    out.writeDouble(this.value);
  }

  @Override
  public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.label = in.readString(this.label instanceof Utf8 ? (Utf8) this.label : null);

      this.value = in.readDouble();

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
          case 0:
            this.label = in.readString(this.label instanceof Utf8 ? (Utf8) this.label : null);
            break;

          case 1:
            this.value = in.readDouble();
            break;

          default:
            throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}
