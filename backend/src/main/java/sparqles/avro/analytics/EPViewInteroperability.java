/**
 * Autogenerated by Avro
 *
 * <p>DO NOT EDIT DIRECTLY
 */
package sparqles.avro.analytics;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class EPViewInteroperability extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord {
    public static final org.apache.avro.Schema SCHEMA$ =
            new org.apache.avro.Schema.Parser()
                    .parse(
                            "{\"type\":\"record\",\"name\":\"EPViewInteroperability\",\"namespace\":\"sparqles.avro.analytics\",\"fields\":[{\"name\":\"SPARQL1Features\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"EPViewInteroperabilityData\",\"fields\":[{\"name\":\"label\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"boolean\"},{\"name\":\"exception\",\"type\":[\"string\",\"null\"]}]}}},{\"name\":\"SPARQL11Features\",\"type\":{\"type\":\"array\",\"items\":\"EPViewInteroperabilityData\"}}]}");

    @Deprecated
    public java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> SPARQL1Features;

    @Deprecated
    public java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> SPARQL11Features;

    /**
     * Default constructor. Note that this does not initialize fields to their default values from
     * the schema. If that is desired then one should use {@link \#newBuilder()}.
     */
    public EPViewInteroperability() {}

    /** All-args constructor. */
    public EPViewInteroperability(
            java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> SPARQL1Features,
            java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> SPARQL11Features) {
        this.SPARQL1Features = SPARQL1Features;
        this.SPARQL11Features = SPARQL11Features;
    }

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    /** Creates a new EPViewInteroperability RecordBuilder */
    public static sparqles.avro.analytics.EPViewInteroperability.Builder newBuilder() {
        return new sparqles.avro.analytics.EPViewInteroperability.Builder();
    }

    /** Creates a new EPViewInteroperability RecordBuilder by copying an existing Builder */
    public static sparqles.avro.analytics.EPViewInteroperability.Builder newBuilder(
            sparqles.avro.analytics.EPViewInteroperability.Builder other) {
        return new sparqles.avro.analytics.EPViewInteroperability.Builder(other);
    }

    /**
     * Creates a new EPViewInteroperability RecordBuilder by copying an existing
     * EPViewInteroperability instance
     */
    public static sparqles.avro.analytics.EPViewInteroperability.Builder newBuilder(
            sparqles.avro.analytics.EPViewInteroperability other) {
        return new sparqles.avro.analytics.EPViewInteroperability.Builder(other);
    }

    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return SPARQL1Features;
            case 1:
                return SPARQL11Features;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                SPARQL1Features =
                        (java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData>) value$;
                break;
            case 1:
                SPARQL11Features =
                        (java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData>) value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    /** Gets the value of the 'SPARQL1Features' field. */
    public java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> getSPARQL1Features() {
        return SPARQL1Features;
    }

    /**
     * Sets the value of the 'SPARQL1Features' field.
     *
     * @param value the value to set.
     */
    public void setSPARQL1Features(
            java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> value) {
        this.SPARQL1Features = value;
    }

    /** Gets the value of the 'SPARQL11Features' field. */
    public java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData>
            getSPARQL11Features() {
        return SPARQL11Features;
    }

    /**
     * Sets the value of the 'SPARQL11Features' field.
     *
     * @param value the value to set.
     */
    public void setSPARQL11Features(
            java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> value) {
        this.SPARQL11Features = value;
    }

    /** RecordBuilder for EPViewInteroperability instances. */
    public static class Builder
            extends org.apache.avro.specific.SpecificRecordBuilderBase<EPViewInteroperability>
            implements org.apache.avro.data.RecordBuilder<EPViewInteroperability> {

        private java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> SPARQL1Features;
        private java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> SPARQL11Features;

        /** Creates a new Builder */
        private Builder() {
            super(sparqles.avro.analytics.EPViewInteroperability.SCHEMA$);
        }

        /** Creates a Builder by copying an existing Builder */
        private Builder(sparqles.avro.analytics.EPViewInteroperability.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.SPARQL1Features)) {
                this.SPARQL1Features = data().deepCopy(fields()[0].schema(), other.SPARQL1Features);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.SPARQL11Features)) {
                this.SPARQL11Features =
                        data().deepCopy(fields()[1].schema(), other.SPARQL11Features);
                fieldSetFlags()[1] = true;
            }
        }

        /** Creates a Builder by copying an existing EPViewInteroperability instance */
        private Builder(sparqles.avro.analytics.EPViewInteroperability other) {
            super(sparqles.avro.analytics.EPViewInteroperability.SCHEMA$);
            if (isValidValue(fields()[0], other.SPARQL1Features)) {
                this.SPARQL1Features = data().deepCopy(fields()[0].schema(), other.SPARQL1Features);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.SPARQL11Features)) {
                this.SPARQL11Features =
                        data().deepCopy(fields()[1].schema(), other.SPARQL11Features);
                fieldSetFlags()[1] = true;
            }
        }

        /** Gets the value of the 'SPARQL1Features' field */
        public java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData>
                getSPARQL1Features() {
            return SPARQL1Features;
        }

        /** Sets the value of the 'SPARQL1Features' field */
        public sparqles.avro.analytics.EPViewInteroperability.Builder setSPARQL1Features(
                java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> value) {
            validate(fields()[0], value);
            this.SPARQL1Features = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /** Checks whether the 'SPARQL1Features' field has been set */
        public boolean hasSPARQL1Features() {
            return fieldSetFlags()[0];
        }

        /** Clears the value of the 'SPARQL1Features' field */
        public sparqles.avro.analytics.EPViewInteroperability.Builder clearSPARQL1Features() {
            SPARQL1Features = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /** Gets the value of the 'SPARQL11Features' field */
        public java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData>
                getSPARQL11Features() {
            return SPARQL11Features;
        }

        /** Sets the value of the 'SPARQL11Features' field */
        public sparqles.avro.analytics.EPViewInteroperability.Builder setSPARQL11Features(
                java.util.List<sparqles.avro.analytics.EPViewInteroperabilityData> value) {
            validate(fields()[1], value);
            this.SPARQL11Features = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /** Checks whether the 'SPARQL11Features' field has been set */
        public boolean hasSPARQL11Features() {
            return fieldSetFlags()[1];
        }

        /** Clears the value of the 'SPARQL11Features' field */
        public sparqles.avro.analytics.EPViewInteroperability.Builder clearSPARQL11Features() {
            SPARQL11Features = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        @Override
        public EPViewInteroperability build() {
            try {
                EPViewInteroperability record = new EPViewInteroperability();
                record.SPARQL1Features =
                        fieldSetFlags()[0]
                                ? this.SPARQL1Features
                                : (java.util.List<
                                                sparqles.avro.analytics.EPViewInteroperabilityData>)
                                        defaultValue(fields()[0]);
                record.SPARQL11Features =
                        fieldSetFlags()[1]
                                ? this.SPARQL11Features
                                : (java.util.List<
                                                sparqles.avro.analytics.EPViewInteroperabilityData>)
                                        defaultValue(fields()[1]);
                return record;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }
}
