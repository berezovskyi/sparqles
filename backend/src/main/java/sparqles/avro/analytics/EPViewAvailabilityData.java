/**
 * Autogenerated by Avro
 *
 * <p>DO NOT EDIT DIRECTLY
 */
package sparqles.avro.analytics;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class EPViewAvailabilityData extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord {
    public static final org.apache.avro.Schema SCHEMA$ =
            new org.apache.avro.Schema.Parser()
                    .parse(
                            "{\"type\":\"record\",\"name\":\"EPViewAvailabilityData\",\"namespace\":\"sparqles.avro.analytics\",\"fields\":[{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"EPViewAvailabilityDataPoint\",\"fields\":[{\"name\":\"x\",\"type\":\"long\"},{\"name\":\"y\",\"type\":\"double\"}]}}}]}");
    @Deprecated public java.lang.CharSequence key;
    @Deprecated public java.util.List<sparqles.avro.analytics.EPViewAvailabilityDataPoint> values;

    /**
     * Default constructor. Note that this does not initialize fields to their default values from
     * the schema. If that is desired then one should use {@link \#newBuilder()}.
     */
    public EPViewAvailabilityData() {}

    /** All-args constructor. */
    public EPViewAvailabilityData(
            java.lang.CharSequence key,
            java.util.List<sparqles.avro.analytics.EPViewAvailabilityDataPoint> values) {
        this.key = key;
        this.values = values;
    }

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    /** Creates a new EPViewAvailabilityData RecordBuilder */
    public static sparqles.avro.analytics.EPViewAvailabilityData.Builder newBuilder() {
        return new sparqles.avro.analytics.EPViewAvailabilityData.Builder();
    }

    /** Creates a new EPViewAvailabilityData RecordBuilder by copying an existing Builder */
    public static sparqles.avro.analytics.EPViewAvailabilityData.Builder newBuilder(
            sparqles.avro.analytics.EPViewAvailabilityData.Builder other) {
        return new sparqles.avro.analytics.EPViewAvailabilityData.Builder(other);
    }

    /**
     * Creates a new EPViewAvailabilityData RecordBuilder by copying an existing
     * EPViewAvailabilityData instance
     */
    public static sparqles.avro.analytics.EPViewAvailabilityData.Builder newBuilder(
            sparqles.avro.analytics.EPViewAvailabilityData other) {
        return new sparqles.avro.analytics.EPViewAvailabilityData.Builder(other);
    }

    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return key;
            case 1:
                return values;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                key = (java.lang.CharSequence) value$;
                break;
            case 1:
                values =
                        (java.util.List<sparqles.avro.analytics.EPViewAvailabilityDataPoint>)
                                value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    /** Gets the value of the 'key' field. */
    public java.lang.CharSequence getKey() {
        return key;
    }

    /**
     * Sets the value of the 'key' field.
     *
     * @param value the value to set.
     */
    public void setKey(java.lang.CharSequence value) {
        this.key = value;
    }

    /** Gets the value of the 'values' field. */
    public java.util.List<sparqles.avro.analytics.EPViewAvailabilityDataPoint> getValues() {
        return values;
    }

    /**
     * Sets the value of the 'values' field.
     *
     * @param value the value to set.
     */
    public void setValues(
            java.util.List<sparqles.avro.analytics.EPViewAvailabilityDataPoint> value) {
        this.values = value;
    }

    /** RecordBuilder for EPViewAvailabilityData instances. */
    public static class Builder
            extends org.apache.avro.specific.SpecificRecordBuilderBase<EPViewAvailabilityData>
            implements org.apache.avro.data.RecordBuilder<EPViewAvailabilityData> {

        private java.lang.CharSequence key;
        private java.util.List<sparqles.avro.analytics.EPViewAvailabilityDataPoint> values;

        /** Creates a new Builder */
        private Builder() {
            super(sparqles.avro.analytics.EPViewAvailabilityData.SCHEMA$);
        }

        /** Creates a Builder by copying an existing Builder */
        private Builder(sparqles.avro.analytics.EPViewAvailabilityData.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.key)) {
                this.key = data().deepCopy(fields()[0].schema(), other.key);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.values)) {
                this.values = data().deepCopy(fields()[1].schema(), other.values);
                fieldSetFlags()[1] = true;
            }
        }

        /** Creates a Builder by copying an existing EPViewAvailabilityData instance */
        private Builder(sparqles.avro.analytics.EPViewAvailabilityData other) {
            super(sparqles.avro.analytics.EPViewAvailabilityData.SCHEMA$);
            if (isValidValue(fields()[0], other.key)) {
                this.key = data().deepCopy(fields()[0].schema(), other.key);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.values)) {
                this.values = data().deepCopy(fields()[1].schema(), other.values);
                fieldSetFlags()[1] = true;
            }
        }

        /** Gets the value of the 'key' field */
        public java.lang.CharSequence getKey() {
            return key;
        }

        /** Sets the value of the 'key' field */
        public sparqles.avro.analytics.EPViewAvailabilityData.Builder setKey(
                java.lang.CharSequence value) {
            validate(fields()[0], value);
            this.key = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /** Checks whether the 'key' field has been set */
        public boolean hasKey() {
            return fieldSetFlags()[0];
        }

        /** Clears the value of the 'key' field */
        public sparqles.avro.analytics.EPViewAvailabilityData.Builder clearKey() {
            key = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /** Gets the value of the 'values' field */
        public java.util.List<sparqles.avro.analytics.EPViewAvailabilityDataPoint> getValues() {
            return values;
        }

        /** Sets the value of the 'values' field */
        public sparqles.avro.analytics.EPViewAvailabilityData.Builder setValues(
                java.util.List<sparqles.avro.analytics.EPViewAvailabilityDataPoint> value) {
            validate(fields()[1], value);
            this.values = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /** Checks whether the 'values' field has been set */
        public boolean hasValues() {
            return fieldSetFlags()[1];
        }

        /** Clears the value of the 'values' field */
        public sparqles.avro.analytics.EPViewAvailabilityData.Builder clearValues() {
            values = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        @Override
        public EPViewAvailabilityData build() {
            try {
                EPViewAvailabilityData record = new EPViewAvailabilityData();
                record.key =
                        fieldSetFlags()[0]
                                ? this.key
                                : (java.lang.CharSequence) defaultValue(fields()[0]);
                record.values =
                        fieldSetFlags()[1]
                                ? this.values
                                : (java.util.List<
                                                sparqles.avro.analytics
                                                        .EPViewAvailabilityDataPoint>)
                                        defaultValue(fields()[1]);
                return record;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }
}
