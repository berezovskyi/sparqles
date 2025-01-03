/**
 * Autogenerated by Avro
 *
 * <p>DO NOT EDIT DIRECTLY
 */
package sparqles.avro.performance;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Run extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord {
    public static final org.apache.avro.Schema SCHEMA$ =
            new org.apache.avro.Schema.Parser()
                    .parse(
                            "{\"type\":\"record\",\"name\":\"Run\",\"namespace\":\"sparqles.avro.performance\",\"fields\":[{\"name\":\"frestout\",\"type\":\"long\"},{\"name\":\"solutions\",\"type\":\"int\"},{\"name\":\"inittime\",\"type\":\"long\"},{\"name\":\"exectime\",\"type\":\"long\"},{\"name\":\"closetime\",\"type\":\"long\"},{\"name\":\"Exception\",\"type\":[\"string\",\"null\"]},{\"name\":\"exectout\",\"type\":\"long\"}]}");
    @Deprecated public long frestout;
    @Deprecated public int solutions;
    @Deprecated public long inittime;
    @Deprecated public long exectime;
    @Deprecated public long closetime;
    @Deprecated public java.lang.CharSequence Exception;
    @Deprecated public long exectout;

    /**
     * Default constructor. Note that this does not initialize fields to their default values from
     * the schema. If that is desired then one should use {@link \#newBuilder()}.
     */
    public Run() {}

    /** All-args constructor. */
    public Run(
            java.lang.Long frestout,
            java.lang.Integer solutions,
            java.lang.Long inittime,
            java.lang.Long exectime,
            java.lang.Long closetime,
            java.lang.CharSequence Exception,
            java.lang.Long exectout) {
        this.frestout = frestout;
        this.solutions = solutions;
        this.inittime = inittime;
        this.exectime = exectime;
        this.closetime = closetime;
        this.Exception = Exception;
        this.exectout = exectout;
    }

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    /** Creates a new Run RecordBuilder */
    public static sparqles.avro.performance.Run.Builder newBuilder() {
        return new sparqles.avro.performance.Run.Builder();
    }

    /** Creates a new Run RecordBuilder by copying an existing Builder */
    public static sparqles.avro.performance.Run.Builder newBuilder(
            sparqles.avro.performance.Run.Builder other) {
        return new sparqles.avro.performance.Run.Builder(other);
    }

    /** Creates a new Run RecordBuilder by copying an existing Run instance */
    public static sparqles.avro.performance.Run.Builder newBuilder(
            sparqles.avro.performance.Run other) {
        return new sparqles.avro.performance.Run.Builder(other);
    }

    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return frestout;
            case 1:
                return solutions;
            case 2:
                return inittime;
            case 3:
                return exectime;
            case 4:
                return closetime;
            case 5:
                return Exception;
            case 6:
                return exectout;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                frestout = (java.lang.Long) value$;
                break;
            case 1:
                solutions = (java.lang.Integer) value$;
                break;
            case 2:
                inittime = (java.lang.Long) value$;
                break;
            case 3:
                exectime = (java.lang.Long) value$;
                break;
            case 4:
                closetime = (java.lang.Long) value$;
                break;
            case 5:
                Exception = (java.lang.CharSequence) value$;
                break;
            case 6:
                exectout = (java.lang.Long) value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    /** Gets the value of the 'frestout' field. */
    public java.lang.Long getFrestout() {
        return frestout;
    }

    /**
     * Sets the value of the 'frestout' field.
     *
     * @param value the value to set.
     */
    public void setFrestout(java.lang.Long value) {
        this.frestout = value;
    }

    /** Gets the value of the 'solutions' field. */
    public java.lang.Integer getSolutions() {
        return solutions;
    }

    /**
     * Sets the value of the 'solutions' field.
     *
     * @param value the value to set.
     */
    public void setSolutions(java.lang.Integer value) {
        this.solutions = value;
    }

    /** Gets the value of the 'inittime' field. */
    public java.lang.Long getInittime() {
        return inittime;
    }

    /**
     * Sets the value of the 'inittime' field.
     *
     * @param value the value to set.
     */
    public void setInittime(java.lang.Long value) {
        this.inittime = value;
    }

    /** Gets the value of the 'exectime' field. */
    public java.lang.Long getExectime() {
        return exectime;
    }

    /**
     * Sets the value of the 'exectime' field.
     *
     * @param value the value to set.
     */
    public void setExectime(java.lang.Long value) {
        this.exectime = value;
    }

    /** Gets the value of the 'closetime' field. */
    public java.lang.Long getClosetime() {
        return closetime;
    }

    /**
     * Sets the value of the 'closetime' field.
     *
     * @param value the value to set.
     */
    public void setClosetime(java.lang.Long value) {
        this.closetime = value;
    }

    /** Gets the value of the 'Exception' field. */
    public java.lang.CharSequence getException() {
        return Exception;
    }

    /**
     * Sets the value of the 'Exception' field.
     *
     * @param value the value to set.
     */
    public void setException(java.lang.CharSequence value) {
        this.Exception = value;
    }

    /** Gets the value of the 'exectout' field. */
    public java.lang.Long getExectout() {
        return exectout;
    }

    /**
     * Sets the value of the 'exectout' field.
     *
     * @param value the value to set.
     */
    public void setExectout(java.lang.Long value) {
        this.exectout = value;
    }

    /** RecordBuilder for Run instances. */
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Run>
            implements org.apache.avro.data.RecordBuilder<Run> {

        private long frestout;
        private int solutions;
        private long inittime;
        private long exectime;
        private long closetime;
        private java.lang.CharSequence Exception;
        private long exectout;

        /** Creates a new Builder */
        private Builder() {
            super(sparqles.avro.performance.Run.SCHEMA$);
        }

        /** Creates a Builder by copying an existing Builder */
        private Builder(sparqles.avro.performance.Run.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.frestout)) {
                this.frestout = data().deepCopy(fields()[0].schema(), other.frestout);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.solutions)) {
                this.solutions = data().deepCopy(fields()[1].schema(), other.solutions);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.inittime)) {
                this.inittime = data().deepCopy(fields()[2].schema(), other.inittime);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.exectime)) {
                this.exectime = data().deepCopy(fields()[3].schema(), other.exectime);
                fieldSetFlags()[3] = true;
            }
            if (isValidValue(fields()[4], other.closetime)) {
                this.closetime = data().deepCopy(fields()[4].schema(), other.closetime);
                fieldSetFlags()[4] = true;
            }
            if (isValidValue(fields()[5], other.Exception)) {
                this.Exception = data().deepCopy(fields()[5].schema(), other.Exception);
                fieldSetFlags()[5] = true;
            }
            if (isValidValue(fields()[6], other.exectout)) {
                this.exectout = data().deepCopy(fields()[6].schema(), other.exectout);
                fieldSetFlags()[6] = true;
            }
        }

        /** Creates a Builder by copying an existing Run instance */
        private Builder(sparqles.avro.performance.Run other) {
            super(sparqles.avro.performance.Run.SCHEMA$);
            if (isValidValue(fields()[0], other.frestout)) {
                this.frestout = data().deepCopy(fields()[0].schema(), other.frestout);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.solutions)) {
                this.solutions = data().deepCopy(fields()[1].schema(), other.solutions);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.inittime)) {
                this.inittime = data().deepCopy(fields()[2].schema(), other.inittime);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.exectime)) {
                this.exectime = data().deepCopy(fields()[3].schema(), other.exectime);
                fieldSetFlags()[3] = true;
            }
            if (isValidValue(fields()[4], other.closetime)) {
                this.closetime = data().deepCopy(fields()[4].schema(), other.closetime);
                fieldSetFlags()[4] = true;
            }
            if (isValidValue(fields()[5], other.Exception)) {
                this.Exception = data().deepCopy(fields()[5].schema(), other.Exception);
                fieldSetFlags()[5] = true;
            }
            if (isValidValue(fields()[6], other.exectout)) {
                this.exectout = data().deepCopy(fields()[6].schema(), other.exectout);
                fieldSetFlags()[6] = true;
            }
        }

        /** Gets the value of the 'frestout' field */
        public java.lang.Long getFrestout() {
            return frestout;
        }

        /** Sets the value of the 'frestout' field */
        public sparqles.avro.performance.Run.Builder setFrestout(long value) {
            validate(fields()[0], value);
            this.frestout = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /** Checks whether the 'frestout' field has been set */
        public boolean hasFrestout() {
            return fieldSetFlags()[0];
        }

        /** Clears the value of the 'frestout' field */
        public sparqles.avro.performance.Run.Builder clearFrestout() {
            fieldSetFlags()[0] = false;
            return this;
        }

        /** Gets the value of the 'solutions' field */
        public java.lang.Integer getSolutions() {
            return solutions;
        }

        /** Sets the value of the 'solutions' field */
        public sparqles.avro.performance.Run.Builder setSolutions(int value) {
            validate(fields()[1], value);
            this.solutions = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /** Checks whether the 'solutions' field has been set */
        public boolean hasSolutions() {
            return fieldSetFlags()[1];
        }

        /** Clears the value of the 'solutions' field */
        public sparqles.avro.performance.Run.Builder clearSolutions() {
            fieldSetFlags()[1] = false;
            return this;
        }

        /** Gets the value of the 'inittime' field */
        public java.lang.Long getInittime() {
            return inittime;
        }

        /** Sets the value of the 'inittime' field */
        public sparqles.avro.performance.Run.Builder setInittime(long value) {
            validate(fields()[2], value);
            this.inittime = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /** Checks whether the 'inittime' field has been set */
        public boolean hasInittime() {
            return fieldSetFlags()[2];
        }

        /** Clears the value of the 'inittime' field */
        public sparqles.avro.performance.Run.Builder clearInittime() {
            fieldSetFlags()[2] = false;
            return this;
        }

        /** Gets the value of the 'exectime' field */
        public java.lang.Long getExectime() {
            return exectime;
        }

        /** Sets the value of the 'exectime' field */
        public sparqles.avro.performance.Run.Builder setExectime(long value) {
            validate(fields()[3], value);
            this.exectime = value;
            fieldSetFlags()[3] = true;
            return this;
        }

        /** Checks whether the 'exectime' field has been set */
        public boolean hasExectime() {
            return fieldSetFlags()[3];
        }

        /** Clears the value of the 'exectime' field */
        public sparqles.avro.performance.Run.Builder clearExectime() {
            fieldSetFlags()[3] = false;
            return this;
        }

        /** Gets the value of the 'closetime' field */
        public java.lang.Long getClosetime() {
            return closetime;
        }

        /** Sets the value of the 'closetime' field */
        public sparqles.avro.performance.Run.Builder setClosetime(long value) {
            validate(fields()[4], value);
            this.closetime = value;
            fieldSetFlags()[4] = true;
            return this;
        }

        /** Checks whether the 'closetime' field has been set */
        public boolean hasClosetime() {
            return fieldSetFlags()[4];
        }

        /** Clears the value of the 'closetime' field */
        public sparqles.avro.performance.Run.Builder clearClosetime() {
            fieldSetFlags()[4] = false;
            return this;
        }

        /** Gets the value of the 'Exception' field */
        public java.lang.CharSequence getException() {
            return Exception;
        }

        /** Sets the value of the 'Exception' field */
        public sparqles.avro.performance.Run.Builder setException(java.lang.CharSequence value) {
            validate(fields()[5], value);
            this.Exception = value;
            fieldSetFlags()[5] = true;
            return this;
        }

        /** Checks whether the 'Exception' field has been set */
        public boolean hasException() {
            return fieldSetFlags()[5];
        }

        /** Clears the value of the 'Exception' field */
        public sparqles.avro.performance.Run.Builder clearException() {
            Exception = null;
            fieldSetFlags()[5] = false;
            return this;
        }

        /** Gets the value of the 'exectout' field */
        public java.lang.Long getExectout() {
            return exectout;
        }

        /** Sets the value of the 'exectout' field */
        public sparqles.avro.performance.Run.Builder setExectout(long value) {
            validate(fields()[6], value);
            this.exectout = value;
            fieldSetFlags()[6] = true;
            return this;
        }

        /** Checks whether the 'exectout' field has been set */
        public boolean hasExectout() {
            return fieldSetFlags()[6];
        }

        /** Clears the value of the 'exectout' field */
        public sparqles.avro.performance.Run.Builder clearExectout() {
            fieldSetFlags()[6] = false;
            return this;
        }

        @Override
        public Run build() {
            try {
                Run record = new Run();
                record.frestout =
                        fieldSetFlags()[0]
                                ? this.frestout
                                : (java.lang.Long) defaultValue(fields()[0]);
                record.solutions =
                        fieldSetFlags()[1]
                                ? this.solutions
                                : (java.lang.Integer) defaultValue(fields()[1]);
                record.inittime =
                        fieldSetFlags()[2]
                                ? this.inittime
                                : (java.lang.Long) defaultValue(fields()[2]);
                record.exectime =
                        fieldSetFlags()[3]
                                ? this.exectime
                                : (java.lang.Long) defaultValue(fields()[3]);
                record.closetime =
                        fieldSetFlags()[4]
                                ? this.closetime
                                : (java.lang.Long) defaultValue(fields()[4]);
                record.Exception =
                        fieldSetFlags()[5]
                                ? this.Exception
                                : (java.lang.CharSequence) defaultValue(fields()[5]);
                record.exectout =
                        fieldSetFlags()[6]
                                ? this.exectout
                                : (java.lang.Long) defaultValue(fields()[6]);
                return record;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }
}
